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

package io.nuls.contract.func.proposal.impl;

import io.nuls.contract.event.proposal.AuditProposalEvent;
import io.nuls.contract.event.proposal.CreateProposalEvent;
import io.nuls.contract.event.proposal.StatusCompletedProposalEvent;
import io.nuls.contract.event.proposal.VoteProposalEvent;
import io.nuls.contract.func.council.CouncilConfig;
import io.nuls.contract.func.proposal.ProposalConstant;
import io.nuls.contract.func.proposal.ProposalVote;
import io.nuls.contract.model.proposal.Proposal;
import io.nuls.contract.model.proposal.ProposalConfig;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: Charlie
 * @date: 2019/8/15
 */
public class ProposalVoteImpl implements ProposalVote {

    protected Map<Integer, Proposal> proposals = new HashMap<Integer, Proposal>();
    /**
     * K:提案id, V:Map(K:投票人; V:投票结果)
     */
    protected Map<Integer, Map<Address, Integer>> voteRecords = new HashMap<Integer, Map<Address, Integer>>();

    @Override
    public void setProposals(String[] keys, String[] values, String[] names, String[] pidAddresses, String[] reason){
        require(keys.length == values.length, "Keys and values length are not equal");
        require(keys.length == names.length, "Keys and names length are not equal");
        require(pidAddresses.length == reason.length, "addresses and reason length are not equal");
        Proposal proposal = null;
        for(int i = 0; i < keys.length; i++){
            String[] fields = values[i].split("-");
            proposal = new Proposal();
            proposal.setId(Integer.parseInt(fields[0]));
            proposal.setType(Integer.parseInt(fields[1]));
            proposal.setOwner(new Address(fields[2]));
            proposal.setVoteCanModify(Boolean.parseBoolean(fields[3]));
            if(null != fields[4] && null != fields[5] && !"".equals(fields[4]) && !"".equals(fields[5])) {
                proposal.setConfig(new ProposalConfig(Long.parseLong(fields[4]), Long.parseLong(fields[5])));
            }
            proposal.setStatus(Integer.parseInt(fields[6]));
            proposal.setName(names[i]);
            Map<String, String> auditRefuseRecords = new HashMap<String, String>();
            for(int j = 0; j < pidAddresses.length; j++){
                String[] temp = pidAddresses[j].split("-");
                if(fields[0].equals(temp[0])){
                    auditRefuseRecords.put(temp[1], reason[j]);
                }
            }
            proposal.setAuditRefuseRecords(auditRefuseRecords);
            proposals.put(Integer.parseInt(keys[i]), proposal);
        }
    }

    @Override
    public void setVoteRecords(String[] keys, String[] values){
        require(keys.length == values.length, "Keys and values length are not equal");
        Map<Address, Integer> recordsMap = null;
        for (int i = 0; i < keys.length; i++) {
            String[] temp = values[i].split("=");
            recordsMap = new HashMap<Address, Integer>();
            for (int j = 0; j < temp.length; j++) {
                String[] record = temp[j].split("-");
                recordsMap.put(new Address(record[0]), Integer.parseInt(record[1]));
            }
            if (recordsMap.isEmpty()) {
                continue;
            }
            voteRecords.put(Integer.parseInt(keys[i]), recordsMap);
        }
    }

    @Override
    public List<Proposal> getVotedProposal(Address address) {
        List<Proposal> list = new ArrayList<Proposal>();
        for (Map.Entry<Integer, Map<Address, Integer>> entry : voteRecords.entrySet()){
            if(entry.getValue().containsKey(address)){
                list.add(proposals.get(entry.getKey()));
            }
        }
        return list;
    }

    @Override
    public void invalidVotes(Address address) {
        require(null != address, "address can not empty");
        List<Proposal> proposalList = this.getVotedProposal(address);
        for (Proposal proposal : proposalList){
            if(this.canVote(proposal)){
                voteRecords.get(proposal.getId()).remove(address);
            }
        }
    }

    @Override
    public Proposal getProposal(int id){
        Proposal proposal = proposals.get(id);
        require(null != proposal, "The proposal is nou exist");
        return proposal;
    }

    @Override
    public Proposal createProposal(String name, int type, String desc, String email, boolean voteCanModify) {
        require(null != name, "name can not empty");
        require(type == ProposalConstant.ROLE || type == ProposalConstant.SYS_PARAM || type == ProposalConstant.COMMUNITY_FUND
                || type == ProposalConstant.OTHER_TYPE , "Invalid proposal type");
        require(null != desc, "desc can not empty");
        require(null != email, "email can not empty");

        int proposalId = proposals.size() + 1;
        Proposal proposal = new Proposal(proposalId, name, type, Msg.sender(),voteCanModify);
        proposals.put(proposalId, proposal);
        emit(new CreateProposalEvent(proposalId, name, type, desc, email, Msg.sender().toString(), voteCanModify));
        return proposal;
    }

    @Override
    public boolean voteProposal(int proposalId, int voteOptionId) {
        require(proposalId > 0L, "Proposal id error");
        require(voteOptionId == ProposalConstant.FAVOUR || voteOptionId == ProposalConstant.AGAINST
                || voteOptionId == ProposalConstant.ABSTENTION,"The vote option is wrong, please check.");
        Proposal proposal = proposals.get(proposalId);
        require(null != proposal, "Proposal is not exist, please check.");
        require(canVote(proposal), "The proposal cannot currently vote, please check.");

        Address address = Msg.sender();
        Map<Address, Integer> record = voteRecords.get(proposalId);
        if(null == record){
            record = new HashMap<Address, Integer>();
            voteRecords.put(proposalId, record);
        }else{
            if(!proposal.isVoteCanModify()) {
                //如果提案已设置为不能改票，就要验证该用户是否重复投票
                require(!record.containsKey(address), "The address already voted, please check.");
            }
        }
        record.put(address, voteOptionId);
        emit(new VoteProposalEvent(proposalId, address.toString(), voteOptionId));
        return false;
    }

    @Override
    public void auditProposal(int proposalId, int auditOptionId, String reason, int currentCouncilMemberCount) {
        require(proposalId > 0L, "Proposal id error, please check.");
        require(auditOptionId == ProposalConstant.YES || auditOptionId == ProposalConstant.NO, "audit option id error, please check.");
        /**
         * 1.该理事是否已经审核过
         * 2.状态在投票中， 就不能继续审核
         * 3.判断审核拒绝的人数是否是理事会总人数
         */
        Proposal proposal = proposals.get(proposalId);
        require(null != proposal, "Proposal is not exist, please check.");
        String address = Msg.sender().toString();
        Map<String, String> auditRefuseRecords = proposal.getAuditRefuseRecords();
        require(!auditRefuseRecords.containsKey(address), "Director has reviewed");
        require(proposal.getStatus() == ProposalConstant.INREVIEW, "The Proposal audit has been end");
        if(auditOptionId == ProposalConstant.YES){
            //任意一个理事支持提案，则提案进入投票流程，审核流程终止。
            proposal.setStatus(ProposalConstant.VOTING);
            //设置投票时间段
            long start = Block.timestamp();
            ProposalConfig proposalConfig = new ProposalConfig(start, start + ProposalConstant.DAY15_SECONDS);
            proposal.setConfig(proposalConfig);
            emit(new AuditProposalEvent(proposalId, address, auditOptionId, reason, (byte) ProposalConstant.VOTING, start, start + ProposalConstant.DAY15_SECONDS));
        }else {
            //审核时， 拒绝后记录结果，满理事会成员总数，则表示提案最终被拒接
            auditRefuseRecords.put(address, reason);
            Byte proposalStatus = null;
            int councilMembers = CouncilConfig.COUNCIL_MEMBERS >= currentCouncilMemberCount ? currentCouncilMemberCount : CouncilConfig.COUNCIL_MEMBERS;
            if (auditRefuseRecords.size() == councilMembers) {
                proposal.setStatus(ProposalConstant.UNAPPROVED);
                proposalStatus = ProposalConstant.UNAPPROVED;
            }
            emit(new AuditProposalEvent(proposalId, address, auditOptionId, reason, proposalStatus));
        }
    }

    @Override
    public boolean setProposalCompleted(int proposalId) {
        require(proposalId > 0L, "Proposal id error, please check.");
        Proposal proposal = proposals.get(proposalId);
        require(null != proposal, "Proposal is not exist, please check.");
        if(proposal.getStatus() == ProposalConstant.VOTING){
            require(!canVote(proposal), "current proposal vote is not over yet!");
        }
        proposal.setStatus(ProposalConstant.COMPLETED);
        //事件
        emit(new StatusCompletedProposalEvent(proposalId));
        return true;
    }

    /**
     * 该提案是否可以投票(状态，时间段)
     * @return
     */
    @Override
    public boolean canVote(Proposal proposal){

        if(proposal.getStatus() != ProposalConstant.VOTING ){
            return false;
        }
        long current = Block.timestamp();
        if(proposal.getConfig().getStartTime() > current || proposal.getConfig().getEndTime() < current){
            return false;
        }
        return true;
    }

    @Override
    public Map<Integer, Proposal> getProposals() {
        return proposals;
    }
    @Override
    public Map<Integer, Map<Address, Integer>> getVoteRecords() {
        return voteRecords;
    }
}
