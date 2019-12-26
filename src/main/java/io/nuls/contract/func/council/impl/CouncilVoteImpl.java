/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.contract.func.council.impl;

import io.nuls.contract.event.council.*;
import io.nuls.contract.func.council.CouncilConfig;
import io.nuls.contract.func.council.CouncilVote;
import io.nuls.contract.model.council.Applicant;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Msg;

import java.util.*;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: Charlie
 * @date: 2019/8/14
 */
public class CouncilVoteImpl implements CouncilVote {

    /**
     * 所有申请人
     */
    protected Map<String, Applicant> allApplicants = new HashMap<String, Applicant>();
    /**
     * K:候选人， V：投给该候选人的投票者列表
     */
    protected Map<String, Set<String>> votes = new HashMap<String, Set<String>>();
    /**
     * 理事
     */
    protected Map<String, Applicant> councilMember = new HashMap<String, Applicant>(CouncilConfig.COUNCIL_MEMBERS);

    @Override
    public void setAllApplicants(String[] keys, String[] values) {
        require(keys.length == values.length, "Keys and values length are not equal");
        Map<Address, Integer> recordsMap = null;
        for (int i = 0; i < keys.length; i++) {
            String[] records = values[i].split("-");
            allApplicants.put(keys[i], new Applicant(records[0], Integer.parseInt(records[1])));
        }
    }

    @Override
    public void setVotes(String[] keys, String[] values) {
        require(keys.length == values.length, "Keys and values length are not equal");
        Map<Address, Integer> recordsMap = null;
        Set<String> addressSet = null;
        for (int i = 0; i < keys.length; i++) {
            String[] records = values[i].split("-");
            for (int j = 0; j < records.length; j++) {
                addressSet = new HashSet<>();
                addressSet.add(records[j]);
            }
            votes.put(keys[i], addressSet);
        }
    }
    @Override
    public void setCouncilMember(String[] keys, String[] values) {
        require(keys.length == values.length, "Keys and values length are not equal");
        Map<Address, Integer> recordsMap = null;
        for (int i = 0; i < keys.length; i++) {
            String[] records = values[i].split("-");
            councilMember.put(keys[i], new Applicant(records[0], Integer.parseInt(records[1])));
        }
    }
    @Override
    public List<String> getVotedApplicantAddress(String address) {
        require(null != address, "address can not empty");
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Set<String>> entry : votes.entrySet()) {
            if (entry.getValue().contains(address)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
    @Override
    public void invalidVotes(String address) {
        require(null != address, "address can not empty");
        for (Set<String> set : votes.values()) {
            set.remove(address);
        }
    }

    @Override
    public int getCurrentCouncilMemberCount() {
        return councilMember.size();
    }

    @Override
    public boolean isCouncilMember(String address) {
        return councilMember.containsKey(address);
    }

    @Override
    public boolean apply(int type, String desc, String email) {
        require(type == CouncilConfig.MANAGEMENT_TYPE || type == CouncilConfig.OPERATIONS_TYPE || type == CouncilConfig.TECHNOLOGY_TYPE, "Invalid candidate director type");
        require(null != desc, "desc can not empty");
        require(null != email, "email can not empty");
        Address address = Msg.sender();
        String addr = address.toString();
        require(!allApplicants.containsKey(addr), "The address has applied");
        if (type == CouncilConfig.TECHNOLOGY_TYPE) {
            require(address.totalBalance().compareTo(CouncilConfig.TECHNOLOGY_ENTRY_MINIMUM) >= 0,
                    "The balance is not enough to apply for this type of director");
        } else {
            require(address.totalBalance().compareTo(CouncilConfig.NON_TECHNOLOGY_ENTRY_MINIMUM) >= 0,
                    "The balance is not enough to apply for this type of director");
        }
        Applicant applicant = new Applicant(addr, type);
        allApplicants.put(addr, applicant);
        emit(new ApplyEvent(addr, type, desc, email));
        return false;
    }

    @Override
    public Applicant getApplicantInfo(String address) {
        require(null != address, "address can not empty");
        Applicant applicant = allApplicants.get(address);
        require(null != applicant, "The director does not exist");
        return applicant;
    }

    @Override
    public boolean removeApplicant(String address) {
        /**
         * 直接移除理事申请人所有数据,不论是否已经成为理事
         */
        require(null != address, "address can not empty");
        require(allApplicants.containsKey(address), "The applicant does not exist");
        allApplicants.remove(address);
        councilMember.remove(address);
        votes.remove(address);
        emit(new RemoveApplicantEvent(address));
        return true;
    }

    @Override
    public boolean voteDirector(Address[] addresses) {
        int addressesLength = addresses.length;
        require(addressesLength <= CouncilConfig.COUNCIL_MEMBERS, "The number of voting addresses must be between 0 and 11");

        String voter = Msg.sender().toString();
        //扫描之前的投票记录，如果该投票者投过票则先取消，再重新记录新的投票
        //TODO 风险点，候选人没有上限(按申请理事费用 技术2.5万 非技术5万 和币总量，估算极端情况有2000~4000个申请人)
        for (Set<String> set : votes.values()) {
            set.remove(voter);
        }
        String[] addrs = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            String address = addresses[i].toString();
            require(allApplicants.containsKey(address), address + "is not a applicant's address and cannot be voted on");
            addrs[i] = address;
            Set<String> addressSet = votes.get(address);
            if (null == addressSet) {
                addressSet = new HashSet<String>();
                votes.put(address, addressSet);
            }
            addressSet.add(voter);
        }
        emit(new VoteDirectorEvent(voter, addrs));
        return true;
    }

    @Override
    public boolean voteOneDirector(String address) {
        require(address != null, "address can not empty");
        require(allApplicants.containsKey(address), address + "is not a applicant's address and cannot be voted on");
        String voter = Msg.sender().toString();
        //是否已投11个
        int voted = 0;
        for (Set<String> set : votes.values()) {
            if (set.contains(voter)) {
                voted++;
            }
        }
        require(voted < CouncilConfig.COUNCIL_MEMBERS, "The number of voting addresses must be between 1 and 11");

        Set<String> addressSet = votes.get(address);
        if (null == addressSet) {
            addressSet = new HashSet<String>();
            votes.put(address, addressSet);
        }
        require(!addressSet.contains(voter), "The address was voted");
        addressSet.add(voter);
        emit(new VoteOneDirectorEvent(voter, address));
        return true;
    }

    @Override
    public boolean removeDirector(String address) {
        /**
         * 仅撤销理事身份, 符合条件仍然是申请者 保留投票记录
         */
        require(null != address, "address can not empty");
        require(councilMember.containsKey(address), "The director does not exist");
        councilMember.remove(address);
        emit(new RemoveDirectorEvent(address));
        return true;
    }

    @Override
    public boolean cancelVoteOneDirector(String address) {
        require(address != null, "address can not empty");
        String voter = Msg.sender().toString();
        Set<String> addressSet = votes.get(address);
        require(null != addressSet && addressSet.contains(voter), "The address is not voted");
        addressSet.remove(voter);
        emit(new CancelVoteOneDirectorEvent(voter, address));
        return false;
    }

    @Override
    public boolean addDirector(String address) {
        require(null != address, "address can not empty");
        require(!councilMember.containsKey(address), "This address is already a director");
        require(councilMember.size() < CouncilConfig.COUNCIL_MEMBERS, "The council is full");
        Applicant applicant = allApplicants.get(address);
        require(null != applicant, "The address did not apply to the director");
        int type = applicant.getType();
        int currentTypeMember = getCountByType(type);
        if (type == CouncilConfig.MANAGEMENT_TYPE) {
            require(currentTypeMember < CouncilConfig.MANAGEMENT_MEMBERS, "The management director is full");
        } else if (type == CouncilConfig.OPERATIONS_TYPE) {
            require(currentTypeMember < CouncilConfig.OPERATIONS_MEMBERS, "The operations director is full");
        } else if (type == CouncilConfig.TECHNOLOGY_TYPE) {
            require(currentTypeMember < CouncilConfig.TECHNOLOGY_MEMBERS, "The technology director is full");
        }
        councilMember.put(applicant.getAddress(), applicant);
        emit(new AddDirectorEvent(address));
        return true;
    }

    @Override
    public boolean replaceDirector(String outAddress, String inAddress) {
        require(null != outAddress && null != inAddress, "address can not empty");
        require(councilMember.containsKey(outAddress), "The director does not exist");
        require(!councilMember.containsKey(inAddress), "This address is already a director");
        Applicant applicantIn = allApplicants.get(inAddress);
        require(null != applicantIn, inAddress + " has not been applied for");
        Applicant applicantOut = councilMember.get(outAddress);
        require(applicantOut.getType() == applicantIn.getType(), "The two types of directors are different");
        councilMember.remove(outAddress);
        councilMember.put(inAddress, applicantIn);
        emit(new RemoveDirectorEvent(outAddress));
        emit(new AddDirectorEvent(inAddress));
        return true;
    }

    @Override
    public Map<String, Applicant> getCouncilMember() {
        return councilMember;
    }
    @Override
    public Map<String, Applicant> getAllApplicants() {
        return allApplicants;
    }
    @Override
    public Map<String, Set<String>> getVotes() {
        return votes;
    }

    private int getCountByType(int type) {
        int count = 0;
        for (Applicant applicant : councilMember.values()) {
            if (type == applicant.getType()) {
                count++;
            }
        }
        return count;
    }
}
