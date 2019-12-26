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

package io.nuls.contract.func.proposal;

import io.nuls.contract.model.proposal.Proposal;
import io.nuls.contract.sdk.Address;

import java.util.List;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2019/8/15
 */
public interface ProposalVote {


    Proposal getProposal(int id);
    /**
     * 创建提案
     */
    Proposal createProposal(String name, int type, String desc, String email, boolean voteCanModify);

    /**
     *  为提案投票
     * @param proposalId 提案id
     * @param voteOptionId 投票选项id
     * @return
     */
    boolean voteProposal(int proposalId, int voteOptionId);

    /**
     * 审核提案
     * @param proposalId 提案id
     * @param auditOptionId 审核选项 0:拒绝, 1:同意
     * @param currentCouncilMemberCount 当前理事会成员个数
     */
    void auditProposal(int proposalId, int auditOptionId, String reason, int currentCouncilMemberCount);

    /**
     * 退还押金
     */
//    boolean redemption(int proposalId);

    /**
     * 将执行中的提案设置为已完成状态, 只能由现任理事会成员来执行
     * @param proposalId
     * @return
     */
    boolean setProposalCompleted(int proposalId);

    boolean canVote(Proposal proposal);

    /**
     * 根据地址查询 所有已投的提案
     * @param address
     * @return
     */
    List<Proposal> getVotedProposal(Address address);

    /**
     * 作废正在投票中的该地址的投票记录
     * @param address
     * @return
     */
    void invalidVotes(Address address);

    void setProposals(String[] keys, String[] values, String[] names, String[] addresses, String[] reason);

    void setVoteRecords(String[] keys, String[] values);

    Map<Integer, Proposal> getProposals();
    Map<Integer, Map<Address, Integer>> getVoteRecords();
}
