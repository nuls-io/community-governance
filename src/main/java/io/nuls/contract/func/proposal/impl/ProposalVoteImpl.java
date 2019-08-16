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
import io.nuls.contract.event.proposal.VoteProposalEvent;
import io.nuls.contract.func.council.CouncilConfig;
import io.nuls.contract.func.proposal.ProposalConstant;
import io.nuls.contract.func.proposal.ProposalVote;
import io.nuls.contract.model.proposal.Proposal;
import io.nuls.contract.model.proposal.ProposalConfig;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Msg;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: Charlie
 * @date: 2019/8/15
 */
public class ProposalVoteImpl implements ProposalVote {

    protected Map<Long, Proposal> proposals = new HashMap<>();
    /**
     * K:提案id, V:Map(K:投票人; V:投票结果)
     */
    protected Map<Long, Map<Address, Integer>> voteRecords = new HashMap<>();

    @Override
    public Proposal createProposal(String name, int type, String desc, String email) {
        require(null != name, "name can not empty");
        require(type == ProposalConstant.ROLE || type == ProposalConstant.SYS_PARAM || type == ProposalConstant.COMMUNITY_FUND
                || type == ProposalConstant.OTHER_TYPE , "Invalid proposal type");
        require(null != desc, "desc can not empty");
        require(null != email, "email can not empty");

        BigInteger value = Msg.value();
        require(value.compareTo(ProposalConstant.RECOGNIZANCE) >= 0, "value need greater than " + ProposalConstant.RECOGNIZANCE);

        Long proposalId = Long.valueOf(proposals.size() + 1);
        Proposal proposal = new Proposal(proposalId, name, type, desc, email, Msg.sender());
        proposals.put(proposalId, proposal);
        emit(new CreateProposalEvent(proposalId, name, type, desc, email));
        return proposal;
    }

    @Override
    public boolean voteProposal(long proposalId, int voteOptionId) {
        require(proposalId > 0L, "Option id error");
        require(voteOptionId == ProposalConstant.FAVOUR || voteOptionId == ProposalConstant.AGAINST
                || voteOptionId == ProposalConstant.ABSTENTION,"The vote option is wrong, please check.");
        require(canVote(proposalId), "The proposal cannot currently vote, please check.");

       /* Proposal proposal = proposals.get(proposalId);
        require(null != proposal, "Proposal is not exist, please check.");
        require(proposal.getStatus() == ProposalConstant.VOTING , "The proposal cannot currently vote, please check.");
        long current = System.currentTimeMillis();
        require(proposal.getConfig().getStartTime() <= current && proposal.getConfig().getEndTime() >= current, "The proposal cannot currently vote, please check.");
*/
        Address address = Msg.sender();
        Map<Address, Integer> record = voteRecords.get(proposalId);
        if(null == record){
            record = new HashMap<>();
            voteRecords.put(proposalId, record);
        }else{
            require(record.containsKey(address),"The address already voted, please check.");
        }
        record.put(address, voteOptionId);
        emit(new VoteProposalEvent(proposalId, address.toString(), voteOptionId));
        return false;
    }

    @Override
    public void auditProposal(long proposalId, int auditOptionId, String reason) {
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
        require(auditRefuseRecords.containsKey(address), "Director has reviewed");
        require(proposal.getStatus() == ProposalConstant.INREVIEW, "The Proposal audit has been end");
        if(auditOptionId == ProposalConstant.YES){
            //任意一个理事支持提案，则提案进入投票流程，审核流程终止。
            proposal.setStatus(ProposalConstant.VOTING);
            //设置投票时间段
            long start = System.currentTimeMillis();
            ProposalConfig proposalConfig = new ProposalConfig(start, start + ProposalConstant. DAY15_MS);
            proposal.setConfig(proposalConfig);
        }else {
            //审核时， 拒绝后记录结果，满理事会成员总数，则表示提案最终被拒接
            auditRefuseRecords.put(address, reason);
            if (auditRefuseRecords.size() == CouncilConfig.COUNCIL_MEMBERS) {
                proposal.setStatus(ProposalConstant.UNAPPROVED);
            }
        }
        emit(new AuditProposalEvent(proposalId, address, auditOptionId, reason));
    }


    @Override
    public boolean redemption(long proposalId) {
        require(proposalId > 0L, "Proposal id error, please check.");
        Proposal proposal = proposals.get(proposalId);
        require(null != proposal, "Proposal is not exist, please check.");
        if(proposal.getStatus() == ProposalConstant.VOTING){
            require(!canVote(proposalId), "current proposal vote is not over yet!");
        }
        require(!proposal.getRecognizanceRedeemed(), "recognizance has been redeemed");
        BigInteger balance = Msg.address().balance();
        require(balance.compareTo(ProposalConstant.RECOGNIZANCE) >= 0, "The contract balance is less than the recognizance amount");

        // return amount
        proposal.getOwner().transfer(ProposalConstant.RECOGNIZANCE);
        proposal.setRecognizanceRedeemed(true);
        return true;
    }

    private boolean canVote(long proposalId){
        /**
         * 是否有该提案
         * 该提案是否可以投票(状态，时间段)
         */
        Proposal proposal = proposals.get(proposalId);
        if(null == proposal){
            return false;
        }
        if(proposal.getStatus() != ProposalConstant.VOTING ){
            return false;
        }
        long current = System.currentTimeMillis();
        if(proposal.getConfig().getStartTime() > current || proposal.getConfig().getEndTime() < current){
            return false;
        }
        return true;
    }
}
