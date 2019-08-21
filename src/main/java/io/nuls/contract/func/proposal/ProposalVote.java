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

/**
 * @author: Charlie
 * @date: 2019/8/15
 */
public interface ProposalVote {


    Proposal getProposal(long id);
    /**
     * 创建提案
     */
    Proposal createProposal(String name, int type, String desc, String email);

    /**
     *  为提案投票
     * @param proposalId 提案id
     * @param voteOptionId 投票选项id
     * @return
     */
    boolean voteProposal(long proposalId, int voteOptionId);

    /**
     * 审核提案
     * @param proposalId 提案id
     * @param auditOptionId 审核选项 0:拒绝, 1:同意
     */
    void auditProposal(long proposalId, int auditOptionId, String reason);

    /**
     * 退还押金
     */
    boolean redemption(long proposalId);
}
