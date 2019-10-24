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

package io.nuls.contract.func.proxy.impl;

import io.nuls.contract.event.proxy.ModifyAgentEvent;
import io.nuls.contract.event.proxy.RevokeAgentEvent;
import io.nuls.contract.event.proxy.RevokeMandatorEvent;
import io.nuls.contract.event.proxy.SetAgentEvent;
import io.nuls.contract.func.proxy.ProxyAgent;
import io.nuls.contract.model.proxy.Mandator;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.Utils;
import io.nuls.contract.sdk.event.DebugEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: Charlie
 * @date: 2019/8/13
 */
public class ProxyAgentImpl implements ProxyAgent {

    protected Map<String, Mandator> mandators = new HashMap<String, Mandator>();

    protected Map<String, Set<String>> agents = new HashMap<String, Set<String>>();

    @Override
    public boolean setAgent(String agentAddress) {
        require(null != agentAddress, "Agent address can not empty");
        String sender = Msg.sender().toString();
        require(!agentAddress.equals(sender), "The agent cannot be himself");
        Mandator mandatorAgentAddress = mandators.get(agentAddress);
        if(null != mandatorAgentAddress) {
            require(!mandatorAgentAddress.getCloseAgent(), "This address does not accept proxy agent");
            //如果待设置的代理已经有代理人了就不能成为代理人
            require(null == mandatorAgentAddress.getAgentAddress(), "The agent address has an agent");
        }
        //设置者 不能是别人的代理
        Set<String> mandatorSet = getMandators(sender);
        require(mandatorSet.isEmpty(), "The sender address is an agent");

        Mandator mandatorSender = mandators.get(sender);
        Utils.emit(new DebugEvent("mandatorSender", "mandatorSender: " + mandatorSender ));
        if(null == mandatorSender){
            mandatorSender = new Mandator(sender, agentAddress, false);
        }else{
            //设置者 必须没有设置过代理人
            require(null == mandatorSender.getAgentAddress(), "The address has an agent");
            mandatorSender.setAgentAddress(agentAddress);
        }
        String mandatorAddress = mandatorSender.getAddress();
        mandators.put(mandatorAddress, mandatorSender);
        Utils.emit(new DebugEvent("委托者", "mandatorAddress: " + mandatorAddress ));
        Utils.emit(new DebugEvent("代理人", "agent: " + agentAddress));
        addMandatorToAgents(agentAddress, mandatorAddress);
        Utils.emit(new DebugEvent("代理人的委托者", "agentMAddress: " + agents.get(agentAddress).size()));
        //发送新增代理事件，包含委托人地址、代理地址
        emit(new SetAgentEvent(mandatorAddress, agentAddress));
        return true;
    }

    @Override
    public boolean modifyAgent(String agentAddress) {
        /**
         * 该账户不接受委托
         * 调用者如果没有设置过代理，则直接返回错误信息
         * 修改代理人
         * 发送事件
         */
        require(null != agentAddress, "Agent address can not empty");
        String sender = Msg.sender().toString();
        require(!agentAddress.equals(sender), "The agent cannot be himself");
        Mandator mandatorAgentAddress = mandators.get(agentAddress);
        if(null != mandatorAgentAddress) {
            require(!mandatorAgentAddress.getCloseAgent(), "This address does not accept proxy agent");
            //如果待设置的代理已经有代理人了就不能成为代理人
            require(null == mandatorAgentAddress.getAgentAddress(), "The agent address has an agent");
        }
        Mandator mandatorSender = mandators.get(sender);
        String oldAgentAddress = mandatorSender.getAgentAddress();
        require(!agentAddress.equals(oldAgentAddress),"The old and new agent addresses are the same");
        require(null != mandatorSender && null != oldAgentAddress, "The address has no agent");
        //从旧的代理的委托人列表中删除
        removeMandatorFromAgents(oldAgentAddress, mandatorSender.getAddress());
        //设置新的代理人
        mandatorSender.setAgentAddress(agentAddress);
        addMandatorToAgents(agentAddress, mandatorSender.getAddress());
        //发送更新代理事件，包含委托人地址、原代理地址、新代理地址
        emit(new ModifyAgentEvent(mandatorSender.getAddress(), oldAgentAddress, agentAddress));
        return true;
    }

    @Override
    public boolean revokeAgent() {
        Mandator mandatorSender = mandators.get(Msg.sender().toString());
        require(null != mandatorSender && null != mandatorSender.getAgentAddress(), "The address has no agent");
        String agentAddress = mandatorSender.getAgentAddress();
        mandatorSender.setAgentAddress(null);
        //如果没有设置委托人,并且可以接受委托，则不再需要持久化委托人信息
        if(!mandatorSender.getCloseAgent()){
            mandators.remove(mandatorSender);
        }
        //从代理的委托人列表中删除
        removeMandatorFromAgents(agentAddress, mandatorSender.getAddress());
        //发送撤销代理人事件，包含委托人地址、代理人地址
        emit(new RevokeAgentEvent(mandatorSender.getAddress(), agentAddress));
        return true;
    }

    @Override
    public boolean revokeMandator(String mandatorAddress) {
        require(null != mandatorAddress, "address can not empty");
        Mandator mandator = mandators.get(mandatorAddress);
        require(null != mandator, "mandator is not exist");
        require(null != mandator.getAgentAddress(), "mandator has not agent");
        mandator.setAgentAddress(null);
        String agentAddress = Msg.sender().toString();
        removeMandatorFromAgents(agentAddress, mandatorAddress);

        //发送撤销委托人事件，包含代理人地址、委托人地址
        emit(new RevokeMandatorEvent(agentAddress, mandatorAddress));
        return true;
    }

    @Override
    public boolean closeAgent() {
        String address = Msg.sender().toString();
        Mandator mandator = mandators.get(address);
        if(null == mandator){
            mandator = new Mandator(address, null, true);
        }else{
            //如果已经有人委托则不能关闭
            Set<String> mandatorSet = agents.get(address);
            if(null != mandatorSet){
                require( mandatorSet.size() == 0, "The address has " + mandatorSet.size()
                        + " mandators, so the function of not accepting delegation cannot be opened");
            }
            require(!mandator.getCloseAgent(), "Agent closed");
            mandator.setCloseAgent(true);
        }
        mandators.put(address, mandator);
        return true;
    }

    @Override
    public boolean openAgent() {
        String address = Msg.sender().toString();
        Mandator mandator = mandators.get(address);
        if(null == mandator){
            mandator = new Mandator(address, null, false);
            mandators.put(address, mandator);
        }else{
            require(mandator.getCloseAgent(), "Agent opened");
            mandator.setCloseAgent(false);
        }
        return true;
    }

    @Override
    public String getAgent(String mandatorAddress) {
        //require(null != mandatorAddress, "address can not empty");
        if(null == mandatorAddress){
            mandatorAddress = Msg.sender().toString();
        }
        String agent = null;
        Mandator mandator = mandators.get(mandatorAddress);
        if(null != mandator){
            agent = mandator.getAgentAddress();
        }
        return agent;
    }

    @Override
    public Set<String> getMandators(String agentAddress) {
        //require(null != agentAddress, "Agent address can not empty");
        if(null == agentAddress){
            agentAddress = Msg.sender().toString();
        }
        Set<String> mandatorSet = agents.get(agentAddress);
        return mandatorSet == null ? new HashSet<>(): mandatorSet;
    }

    @Override
    public boolean suffrage(String address) {
        //如果该地址已设置代理,则不能投票, 否则可以参与合约的各项投票.
        String agent = getAgent(address);
        if(null != agent) {
            return false;
        }
        return true;
    }

    private void removeMandatorFromAgents(String agentAddress, String mandatorAddress){
        Set<String> set = agents.get(agentAddress);
        require(null != agentAddress || set.size() > 0, "Agent not exist");
        set.remove(mandatorAddress);
    }

    private void addMandatorToAgents(String agentAddress, String mandatorAddress){
        Set<String> mandatorSet = agents.get(agentAddress);
        if(null == mandatorSet){
            mandatorSet = new HashSet<>();
        }
        mandatorSet.add(mandatorAddress);
        agents.put(agentAddress, mandatorSet);
    }
}
