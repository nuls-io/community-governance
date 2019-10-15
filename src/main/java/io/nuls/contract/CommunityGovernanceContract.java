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
import io.nuls.contract.sdk.annotation.JSONSerializable;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.nuls.contract.sdk.Utils.require;

public class CommunityGovernanceContract extends Ownable implements Contract {

    private BaseVote baseVote;

    private ProxyAgent proxyAgent;

    private CouncilVote councilVote;

    private ProposalVote proposalVote;

    public CommunityGovernanceContract() {
        //票权委托
        proxyAgent = new ProxyAgentImpl();
        //理事会选举
        councilVote = new CouncilVoteImpl();
        //提案
        proposalVote = new ProposalVoteImpl();
        //投票
        baseVote = new BaseBaseVoteImpl();

    }

    @Payable
    public VoteEntity createBaseVote(@Required String title, @Required String desc, @Required String[] items, @Required long startTime, @Required long endTime,
                                     @Required boolean isMultipleSelect, @Required int minSelectCount, @Required int maxSelectCount, @Required boolean voteCanModify, Integer proposalId) {
        VoteEntity voteEntity = baseVote.create(title, desc, items, proposalId);

        VoteConfig config = new VoteConfig(startTime, endTime, isMultipleSelect, minSelectCount, maxSelectCount, voteCanModify);

        boolean success = baseVote.init(voteEntity.getId(), config);

        Utils.require(success, "vote init fail");

        return voteEntity;
    }

//    public boolean init(long voteId, long startTime, long endTime, boolean isMultipleSelect, int maxSelectCount, boolean voteCanModify) {
//        VoteConfig config = new VoteConfig(startTime, endTime, isMultipleSelect, maxSelectCount, voteCanModify);
//        return baseVote.init(voteId, config);
//    }

    public boolean vote(@Required long voteId, @Required long[] itemIds) {
        //验证票权
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return baseVote.vote(voteId, itemIds);
    }

    public boolean redemptionVote(@Required long voteId) {
        return baseVote.redemption(voteId);
    }

    @View
    public boolean canVote(@Required long voteId) {
        return baseVote.canVote(voteId);
    }

    @View
    public VoteEntity queryVote(@Required long voteId) {
        return baseVote.queryVote(voteId);
    }

    @View
    public Map<Address, List<Long>> queryVoteResult(@Required long voteId) {
        return baseVote.queryVoteResult(voteId);
    }

    @View
    public boolean queryAddressHasVote(@Required long voteId, @Required  Address address) {
        return baseVote.queryAddressHasVote(voteId, address);
    }


    /**
     * 委托人设置代理
     * @param agentAddress
     * @return
     */
    public boolean setAgent(@Required String agentAddress){
       return proxyAgent.setAgent(agentAddress);
    }
    /**
     * 委托人更改代理地址
     * @param agentAddress
     * @return
     */
    public boolean modifyAgent(@Required String agentAddress){
        return proxyAgent.modifyAgent(agentAddress);
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
    public boolean revokeMandator(@Required String mandatorAddress){
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
    public boolean apply(@Required int type, @Required String desc, @Required String email){
        return councilVote.apply(type, desc, email);
    }
    /**
     * 获取理事信息
     */
    @View
    @JSONSerializable
    public Applicant getApplicantInfo(@Required String address){
        return councilVote.getApplicantInfo(address);
    }
    /**
     * 给理事投票
     */
    public boolean voteDirector(@Required String[] addresses){
        //验证票权
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return councilVote.voteDirector(addresses);
    }
    /**
     * 对单个理事投票
     * @param address
     * @return
     */
    public boolean voteOneDirector(@Required String address){
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return councilVote.voteOneDirector(address);
    }

    /**
     * 取消对单个理事的投票
     * @param address
     * @return
     */
    public boolean cancelVoteOneDirector(@Required String address){
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return councilVote.cancelVoteOneDirector(address);
    }


    /**
     * 移除一个候选人
     */
    public boolean removeApplicant(@Required String address){
        onlyOwner();
        return councilVote.removeApplicant(address);
    }
    /**
     * 添加理事
     */
    public boolean addDirector(@Required String address){
        onlyOwner();
        return councilVote.addDirector(address);
    }
    /**
     * 移除理事
     */
    public boolean removeDirector(@Required String address){
        onlyOwner();
        return councilVote.removeDirector(address);
    }

    /**
     * 获取理事会成员信息
     * @return
     */
    @View
    @JSONSerializable
    public Map<String, Applicant> getCouncilMember(){
        return councilVote.getCouncilMember();
    }

    /**
     * 创建提案
     */
    @Payable
    public Proposal createProposal(@Required String name, @Required int type, @Required String desc, @Required String email){
        return proposalVote.createProposal(name, type, desc, email);
    }

    @View
    @JSONSerializable
    public Proposal getProposal(@Required int id){
        return proposalVote.getProposal(id);
    }

    /**
     *  为提案投票
     * @param proposalId 提案id
     * @param voteOptionId 投票选项id
     * @return
     */
    public boolean voteProposal(@Required int proposalId, @Required int voteOptionId){
        //验证票权
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return proposalVote.voteProposal(proposalId, voteOptionId);
    }

    /**
     * 审核提案
     * @param proposalId 提案id
     * @param auditOptionId 审核选项 0:拒绝, 1:同意
     */
    public void auditProposal(@Required int proposalId, @Required int auditOptionId, String reason){
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
/*    public boolean redemptionProposal(@Required int proposalId){
        return proposalVote.redemption(proposalId);
    }*/

    /**
     * 将执行中的提案设置为已完成状态, 只能由现任理事会成员来执行
     * @param proposalId
     * @return
     */
    public boolean setProposalCompleted(int proposalId){
        /**
         * 是否是理事
         */
        String address = Msg.sender().toString();
        require(councilVote.isCouncilMember(address), "Permission Denied");
        return proposalVote.setProposalCompleted(proposalId);
    }
}