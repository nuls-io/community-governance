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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: Charlie
 * @date: 2019/8/14
 */
public class CouncilVoteImpl implements CouncilVote {


    protected Map<String, Applicant> allApplicants = new HashMap<String, Applicant>();

    /**
     * K:候选人， V：投给该候选人的投票者列表
     */
    protected Map<String, Set<String>> votes = new HashMap<String, Set<String>>();

    protected Map<String, Applicant> councilMember = new HashMap<>(CouncilConfig.COUNCIL_MEMBERS);


    @Override
    public boolean isCouncilMember(String address){
        return councilMember.containsKey(address);
    }

    @Override
    public boolean apply(int type, String desc, String email) {
        require(type == CouncilConfig.MANAGEMENT || type == CouncilConfig.OPERATIONS || type == CouncilConfig.TECHNOLOGY, "Invalid candidate director type");
        require(null != desc, "desc can not empty");
        require(null != email, "email can not empty");
        Address address = Msg.sender();
        String addr = address.toString();
        require(!allApplicants.containsKey(addr), "The address has applied");
        if(type == CouncilConfig.TECHNOLOGY){
            require(address.totalBalance().compareTo(CouncilConfig.TECHNOLOGY_ENTRY_MINIMUM) >= 0,
                    "The balance is not enough to apply for this type of director");
        }else{
            require(address.totalBalance().compareTo(CouncilConfig.NON_TECHNOLOGY_ENTRY_MINIMUM) >= 0,
                    "The balance is not enough to apply for this type of director");
        }
        Applicant applicant = new Applicant(addr, type, desc, email);
        allApplicants.put(addr, applicant);
        emit(new ApplyEvent(addr, type));
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
        require(null != address, "address can not empty");
        require(allApplicants.containsKey(address), "The applicant does not exist");
        allApplicants.remove(address);
        votes.remove(address);
        emit(new RemoveApplicantEvent(address));
        return true;
    }

    @Override
    public boolean voteDirector(String[] addresses) {
        require(addresses != null && addresses.length > 0 && addresses.length < CouncilConfig.COUNCIL_MEMBERS, "The number of voting addresses must be between 1 and 11");
        String voter = Msg.sender().toString();
        for (int i = 0; i < addresses.length; i++) {
            String address = addresses[i];
            require(allApplicants.containsKey(address), address + "is not a applicant's address and cannot be voted on");
            Set<String> addressSet = votes.get(address);
            if (null == addressSet) {
                addressSet = new HashSet<>();
            }
            boolean rs = addressSet.add(voter);
            require(rs, "The address has already voted");
            votes.put(address, addressSet);
        }
        emit(new VoteDirectorEvent(voter, addresses));
        return true;
    }

    @Override
    public boolean addDirector(String address) {
        require(null != address, "address can not empty");
        require(councilMember.size() < CouncilConfig.COUNCIL_MEMBERS, "The council is full");
        Applicant applicant = allApplicants.get(address);
        require(null != applicant, "The address did not apply to the director");
        councilMember.put(applicant.getAddress(), applicant);
        emit(new AddDirectorEvent(address));
        return true;
    }

    @Override
    public boolean removeDirector(String address) {
        require(null != address, "address can not empty");
        require(councilMember.containsKey(address), "The director does not exist");
        councilMember.remove(address);
        votes.remove(address);
        emit(new RemoveDirectorEvent(address));
        return true;
    }

    @Override
    public Map<String, Applicant> getCouncilMember() {
        return councilMember;
    }
}
