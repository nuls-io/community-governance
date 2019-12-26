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
import io.nuls.contract.model.proxy.Mandator;
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

        proxyAgent.init(baseVote, proposalVote, councilVote);
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
    public boolean queryAddressHasVote(@Required long voteId, @Required Address address) {
        return baseVote.queryAddressHasVote(voteId, address);
    }

    /**
     * 委托人设置代理
     *
     * @param agentAddress
     * @return
     */
    public boolean setAgent(@Required Address agentAddress) {
        return proxyAgent.setAgent(agentAddress.toString());
    }

    /**
     * 委托人更改代理地址
     *
     * @param agentAddress
     * @return
     */
    public boolean modifyAgent(@Required Address agentAddress) {
        return proxyAgent.modifyAgent(agentAddress.toString());
    }

    /**
     * 委托人撤销代理地址
     *
     * @return
     */
    public boolean revokeAgent() {
        return proxyAgent.revokeAgent();
    }

    /**
     * 代理人撤销委托人地址
     *
     * @return
     */
    public boolean revokeMandator(@Required Address mandatorAddress) {
        return proxyAgent.revokeMandator(mandatorAddress.toString());
    }

    /**
     * 账户开启不接受委托的功能
     */
    public boolean closeAgent() {
        return proxyAgent.closeAgent();
    }

    /**
     * 账户关闭不接受委托的功能
     */
    public boolean openAgent() {
        return proxyAgent.openAgent();
    }

    /**
     * 查询委托人的代理地址 - view方法
     *
     * @return
     */
    @View
    public String getAgent(Address mandatorAddress) {
        return proxyAgent.getAgent(mandatorAddress.toString());
    }

    /**
     * 查询代理人的委托地址列表 - view方法
     *
     * @return
     */
    @View
    public Set<String> getMandators(Address agentAddress) {
        return proxyAgent.getMandators(agentAddress.toString());
    }

    /**
     * 申请理事
     */
    public boolean apply(@Required int type, @Required String desc, @Required String email) {
        return councilVote.apply(type, desc, email);
    }

    /**
     * 获取理事信息
     */
    @View
    @JSONSerializable
    public Applicant getApplicantInfo(@Required Address address) {
        return councilVote.getApplicantInfo(address.toString());
    }

    /**
     * 给理事投票
     */
    public boolean voteDirector(String[] addresses) {
        //验证票权
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        Address[] addrs;
        if (null != addresses) {
            addrs = new Address[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                addrs[i] = new Address(addresses[i]);
            }
        } else {
            addrs = new Address[0];
        }

        return councilVote.voteDirector(addrs);
    }

    /**
     * 对单个理事投票
     *
     * @param address
     * @return
     */
    public boolean voteOneDirector(@Required Address address) {
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return councilVote.voteOneDirector(address.toString());
    }

    /**
     * 取消对单个理事的投票
     *
     * @param address
     * @return
     */
    public boolean cancelVoteOneDirector(@Required Address address) {
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return councilVote.cancelVoteOneDirector(address.toString());
    }


    /**
     * 移除一个候选人
     */
    public boolean removeApplicant(@Required Address address) {
        onlyOwner();
        return councilVote.removeApplicant(address.toString());
    }

    /**
     * 添加理事
     */
    public boolean addDirector(@Required Address address) {
        onlyOwner();
        return councilVote.addDirector(address.toString());
    }

    /**
     * 移除理事
     */
    public boolean removeDirector(@Required Address address) {
        onlyOwner();
        return councilVote.removeDirector(address.toString());
    }

    /**
     * 理事替换
     *
     * @param outAddress
     * @param inAddress
     * @return
     */
    public boolean replaceDirector(String outAddress, String inAddress) {
        onlyOwner();
        return councilVote.replaceDirector(outAddress, inAddress);
    }

    /**
     * 创建提案
     */
    @Payable
    public Proposal createProposal(@Required String name, @Required int type, @Required String desc, @Required String email, @Required boolean voteCanModify) {
        return proposalVote.createProposal(name, type, desc, email, voteCanModify);
    }

    @View
    @JSONSerializable
    public Proposal getProposal(@Required int id) {
        return proposalVote.getProposal(id);
    }

    /**
     * 为提案投票
     *
     * @param proposalId   提案id
     * @param voteOptionId 投票选项id
     * @return
     */
    public boolean voteProposal(@Required int proposalId, @Required int voteOptionId) {
        //验证票权
        require(proxyAgent.suffrage(Msg.sender().toString()), "The address has an agent, Can't vote");
        return proposalVote.voteProposal(proposalId, voteOptionId);
    }

    /**
     * 审核提案
     *
     * @param proposalId    提案id
     * @param auditOptionId 审核选项 0:拒绝, 1:同意
     */
    public void auditProposal(@Required int proposalId, @Required int auditOptionId, String reason) {
        /**
         * 是否是理事
         */
        String address = Msg.sender().toString();
        require(councilVote.isCouncilMember(address), "Permission Denied");
        proposalVote.auditProposal(proposalId, auditOptionId, reason, councilVote.getCurrentCouncilMemberCount());
    }

    /**
     * 将执行中的提案设置为已完成状态, 只能由现任理事会成员来执行
     *
     * @param proposalId
     * @return
     */
    public boolean setProposalCompleted(int proposalId) {
        /**
         * 是否是理事
         */
        String address = Msg.sender().toString();
        require(councilVote.isCouncilMember(address), "Permission Denied");
        return proposalVote.setProposalCompleted(proposalId);
    }

    @View
    @JSONSerializable
    public List<String> getVotedApplicantAddress(@Required Address address) {
        return councilVote.getVotedApplicantAddress(address.toString());
    }

    @View
    @JSONSerializable
    public List<Proposal> getVotedProposal(@Required Address address) {
        return proposalVote.getVotedProposal(address);
    }

    @View
    @JSONSerializable
    public List<VoteEntity> getVotedVotes(@Required Address address) {
        return baseVote.getVotedVotes(address);
    }

    public void councilS1(String[] keys, String[] values){
        onlyOwner();
        councilVote.setAllApplicants(keys, values);
    }
    public void councilS2(String[] keys, String[] values){
        onlyOwner();
        councilVote.setVotes(keys, values);
    }
    public void councilS3(String[] keys, String[] values){
        onlyOwner();
        councilVote.setCouncilMember(keys, values);
    }


    @View
    @JSONSerializable
    public Map<String, Applicant> getCouncilMember(){
        return councilVote.getCouncilMember();
    }

    @View
    @JSONSerializable
    public Map<String, Applicant> getAllApplicants(){
        return councilVote.getAllApplicants();
    }

    @View
    @JSONSerializable
    public Map<String, Set<String>> getVotes(){
        return councilVote.getVotes();
    }

    public void proposalS1(String[] keys, String[] values, String[] names, String[] pidAddresses, String[] reason) {
        onlyOwner();
        proposalVote.setProposals(keys, values, names, pidAddresses, reason);
    }

    public void proposalS2(String[] keys, String[] values) {
        onlyOwner();
        proposalVote.setVoteRecords(keys, values);
    }

    @View
    @JSONSerializable
    public Map<Integer, Proposal> getProposals() {
        return proposalVote.getProposals();
    }

    @View
    @JSONSerializable
    public Map<Integer, Map<Address, Integer>> getVoteRecords() {
        return proposalVote.getVoteRecords();
    }

    public void baseVoteS1(String baseInfos, String title, String configStr, String[] items) {
        onlyOwner();
        baseVote.setVoteData(baseInfos, title, configStr, items);
    }
    public void baseVoteS2(Long[] voteIdArr, String[] addressArr, String[] itemStrArr) {
        onlyOwner();
        baseVote.setVoteRecordData(voteIdArr, addressArr, itemStrArr);
    }

    @View
    @JSONSerializable
    public Map<Long, VoteEntity> getBaseVotes() {
        return baseVote.getVotes();
    }

    @View
    @JSONSerializable
    public Map<Long, Map<Address, List<Long>>> getBaseVoteRecords() {
        return baseVote.getVoteRecords();
    }


    public void proxyS1(String[] agentArray, String[] mandatorArray) {
        onlyOwner();
        proxyAgent.setProxyData(agentArray, mandatorArray);
    }

    @View
    @JSONSerializable
    public Map<String, Set<String>> getAgents() {
        return proxyAgent.getAgents();
    }

    @View
    @JSONSerializable
    public Map<String, Mandator> getMandators() {
        return proxyAgent.getMandators();
    }
}