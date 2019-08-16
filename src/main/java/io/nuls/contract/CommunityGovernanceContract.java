package io.nuls.contract;

import io.nuls.contract.func.council.CouncilVote;
import io.nuls.contract.func.council.impl.CouncilVoteImpl;
import io.nuls.contract.func.proposal.ProposalVote;
import io.nuls.contract.func.proposal.impl.ProposalVoteImpl;
import io.nuls.contract.func.proxy.ProxyAgent;
import io.nuls.contract.func.proxy.impl.ProxyAgentImpl;
import io.nuls.contract.func.vote.BaseVote;
import io.nuls.contract.func.vote.impl.BaseBaseVoteImpl;
import io.nuls.contract.model.council.Applicant;
import io.nuls.contract.model.proposal.Proposal;
import io.nuls.contract.model.vote.VoteConfig;
import io.nuls.contract.model.vote.VoteEntity;
import io.nuls.contract.ownership.Ownable;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.Utils;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.nuls.contract.sdk.Utils.require;

public class CommunityGovernanceContract extends Ownable implements Contract {

    private BaseVote baseVote;

    private ProxyAgent proxyAgent;

    private CouncilVote councilVote;

    private ProposalVote proposalVote;

    public CommunityGovernanceContract(long minRecognizance) {
        //票权委托
        proxyAgent = new ProxyAgentImpl();
        //理事会选举
        councilVote = new CouncilVoteImpl();
        //提案
        proposalVote = new ProposalVoteImpl();
        //投票
        baseVote = new BaseBaseVoteImpl(BigInteger.valueOf(minRecognizance));

    }

    @Payable
    public VoteEntity createBaseVote(String title, String desc, String[] items, long startTime, long endTime, boolean isMultipleSelect, int minSelectCount, int maxSelectCount, boolean voteCanModify) {
        VoteEntity voteEntity = baseVote.create(title, desc, items);

        VoteConfig config = new VoteConfig(startTime, endTime, isMultipleSelect, minSelectCount, maxSelectCount, voteCanModify);
        boolean success = baseVote.init(voteEntity.getId(), config);

        Utils.require(success, "vote init fail");

        return voteEntity;
    }

//    public boolean init(long voteId, long startTime, long endTime, boolean isMultipleSelect, int maxSelectCount, boolean voteCanModify) {
//        VoteConfig config = new VoteConfig(startTime, endTime, isMultipleSelect, maxSelectCount, voteCanModify);
//        return baseVote.init(voteId, config);
//    }

    public boolean vote(long voteId, long[] itemIds) {
        return baseVote.vote(voteId, itemIds);
    }

    public boolean redemptionVote(long voteId) {
        return baseVote.redemption(voteId);
    }

    @View
    public boolean canVote(long voteId) {
        return baseVote.canVote(voteId);
    }

    @View
    public VoteEntity queryVote(long voteId) {
        return baseVote.queryVote(voteId);
    }

    @View
    public Map<Address, List<Long>> queryVoteResult(long voteId) {
        return baseVote.queryVoteResult(voteId);
    }

    @View
    public boolean queryAddressHasVote(long voteId, Address address) {
        return baseVote.queryAddressHasVote(voteId, address);
    }


    /**
     * 委托人设置代理
     * @param agentAddress
     * @return
     */
    public boolean setAgent(String agentAddress){
       return proxyAgent.setAgent(agentAddress);
    }
    /**
     * 委托人更改代理地址
     * @param agentAddress
     * @return
     */
    public boolean modifyAgent(String agentAddress){
        return proxyAgent.setAgent(agentAddress);
    }
    /**
     * 委托人撤销代理地址
     * @return
     */
    public boolean revokeAgent(){
        return proxyAgent.revokeAgent();
    }
    /**
     * 代理人撤销委托人地址
     * @return
     */
    public boolean revokeMandator(String mandatorAddress){
        return proxyAgent.revokeMandator(mandatorAddress);
    }
    /**
     * 账户开启不接受委托的功能
     */
    public boolean closeAgent(){
        return proxyAgent.closeAgent();
    }
    /**
     * 账户关闭不接受委托的功能
     */
    public boolean openAgent(){
        return proxyAgent.openAgent();
    }
    /**
     * 查询委托人的代理地址 - view方法
     * @return
     */
    @View
    public String getAgent(String mandatorAddress){
        return proxyAgent.getAgent(mandatorAddress);
    }
    /**
     * 查询代理人的委托地址列表 - view方法
     * @return
     */
    @View
    public Set<String> getMandators(String agentAddress){
        return proxyAgent.getMandators(agentAddress);
    }

    /**
     * 申请理事
     */
    public boolean apply(int type, String desc, String email){
        return councilVote.apply(type, desc, email);
    }
    /**
     * 获取理事信息
     */
    @View
    public Applicant getApplicantInfo(String address){
        return councilVote.getApplicantInfo(address);
    }
    /**
     * 给理事投票
     */
    public boolean voteDirector(String[] addresses){
        return councilVote.voteDirector(addresses);
    }
    /**
     * 移除一个候选人
     */
    public boolean removeApplicant(String address){
        onlyOwner();
        return councilVote.removeApplicant(address);
    }
    /**
     * 添加理事
     */
    public boolean addDirector(String address){
        onlyOwner();
        return councilVote.addDirector(address);
    }
    /**
     * 移除理事
     */
    public boolean removeDirector(String address){
        onlyOwner();
        return councilVote.removeDirector(address);
    }



    /**
     * 创建提案
     */
    @Payable
    public Proposal createProposal(String name, int type, String desc, String email){
        return proposalVote.createProposal(name, type, desc, email);
    }

    /**
     *  为提案投票
     * @param proposalId 提案id
     * @param voteOptionId 投票选项id
     * @return
     */
    public boolean voteProposal(long proposalId, int voteOptionId){
        return proposalVote.voteProposal(proposalId, voteOptionId);
    }

    /**
     * 审核提案
     * @param proposalId 提案id
     * @param auditOptionId 审核选项 0:拒绝, 1:同意
     */
    public void auditProposal(long proposalId, int auditOptionId, String reason){
        /**
         * 是否是理事
         */
        String address = Msg.sender().toString();
        require(councilVote.isCouncilMember(address), "Permission Denied");
        proposalVote.auditProposal(proposalId, auditOptionId, reason);
    }

    /**
     * 退还押金
     */
    public boolean redemptionProposal(long proposalId){
        return proposalVote.redemption(proposalId);
    }
}