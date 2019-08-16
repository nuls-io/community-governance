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

package io.nuls.contract.func.proxy;

import java.util.Set;

/**
 * 委托代理票权
 * @author: Charlie
 * @date: 2019/8/13
 */
public interface ProxyAgent {
    /**
     * 委托人设置代理
     * @param agentAddress
     * @return
     */
    boolean setAgent(String agentAddress);
    /**
     * 委托人更改代理地址
     * @param agentAddress
     * @return
     */
    boolean modifyAgent(String agentAddress);
    /**
     * 委托人撤销代理地址
     * @return
     */
    boolean revokeAgent();
    /**
     * 代理人撤销委托人地址
     * @return
     */
    boolean revokeMandator(String mandatorAddress);
    /**
     * 账户开启不接受委托的功能
     */
    boolean closeAgent();
    /**
     * 账户关闭不接受委托的功能
     */
    boolean openAgent();
    /**
     * 查询委托人的代理地址 - view方法
     * @return
     */
    String getAgent(String mandatorAddress);
    /**
     * 查询代理人的委托地址列表 - view方法
     * @return
     */
    Set<String> getMandators(String agentAddress);

}
