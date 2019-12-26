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

package io.nuls.contract.func.council;

import io.nuls.contract.model.council.Applicant;
import io.nuls.contract.sdk.Address;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: Charlie
 * @date: 2019/8/14
 */
public interface CouncilVote {

    int getCurrentCouncilMemberCount();
    /**
     * 申请理事
     * @param type
     * @param desc
     * @param email
     * @return
     */
    boolean apply(int type, String desc, String email);

    /**
     * 获取理事信息
     * @param address
     * @return
     */
    Applicant getApplicantInfo(String address);

    /**
     * 给理事投票
     * @param addresses
     * @return
     */
    boolean voteDirector(Address[] addresses);

    /**
     * 对单个理事投票
     * @param address
     * @return
     */
    boolean voteOneDirector(String address);

    /**
     * 取消对单个理事的投票
     * @param address
     * @return
     */
    boolean cancelVoteOneDirector(String address);

    /**
     * 移除一个候选人
     * @param address
     * @return
     */
    boolean removeApplicant(String address);

    /**
     * 添加理事
     * @param address
     * @return
     */
    boolean addDirector(String address);

    /**
     * 移除理事
     * @param address
     * @return
     */
    boolean removeDirector(String address);

    /**
     * 是否是理事会成员
     * @param address
     * @return
     */
    boolean isCouncilMember(String address);

    boolean replaceDirector(String outAddress, String inAddress);

    /**
     * 根据地址查询 所有已投的申请人
     * @param address
     * @return
     */
    List<String> getVotedApplicantAddress(String address);

    /**
     * 作废正在投票中的该地址的投票记录
     * @param address
     * @return
     */
    void invalidVotes(String address);

    void setAllApplicants(String[] keys, String[] values);

    void setVotes(String[] keys, String[] values);

    void setCouncilMember(String[] keys, String[] values);

    /**
     * 获取理事会成员信息
     * @return
     */
    Map<String, Applicant> getCouncilMember();


    Map<String, Applicant> getAllApplicants();

    Map<String, Set<String>> getVotes();
}
