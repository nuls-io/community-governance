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

package io.nuls.contract.event.proxy;

import io.nuls.contract.sdk.Event;

/**
 * @author: Charlie
 * @date: 2019/8/13
 */
public class ModifyAgentEvent implements Event {
    private String mandatorAddress;
    private String oldAgentAddress;
    private String newAgentAddress;

    public ModifyAgentEvent(String mandatorAddress, String oldAgentAddress, String newAgentAddress) {
        this.mandatorAddress = mandatorAddress;
        this.oldAgentAddress = oldAgentAddress;
        this.newAgentAddress = newAgentAddress;
    }

    public String getMandatorAddress() {
        return mandatorAddress;
    }

    public void setMandatorAddress(String mandatorAddress) {
        this.mandatorAddress = mandatorAddress;
    }

    public String getOldAgentAddress() {
        return oldAgentAddress;
    }

    public void setOldAgentAddress(String oldAgentAddress) {
        this.oldAgentAddress = oldAgentAddress;
    }

    public String getNewAgentAddress() {
        return newAgentAddress;
    }

    public void setNewAgentAddress(String newAgentAddress) {
        this.newAgentAddress = newAgentAddress;
    }
}
