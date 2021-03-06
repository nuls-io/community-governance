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

package io.nuls.contract.event.proposal;

import io.nuls.contract.sdk.Event;

/**
 * @author: Charlie
 * @date: 2019/8/15
 */
public class AuditProposalEvent implements Event {
    private int id;
    private String address;
    private int state;
    private String reason;
    /** 如果审核通过则提案状态会改变，如果审核拒绝则不需要改变提案状态 值为null */
    private Byte proposalStatus;
    private Long startTime;
    private Long endTime;

    public AuditProposalEvent(int id, String address, int state, String reason) {
        this.id = id;
        this.address = address;
        this.state = state;
        this.reason = reason;
    }

    public AuditProposalEvent(int id, String address, int state, String reason, Byte proposalStatus) {
        this.id = id;
        this.address = address;
        this.state = state;
        this.reason = reason;
        this.proposalStatus = proposalStatus;
    }

    public AuditProposalEvent(int id, String address, int state, String reason, Byte proposalStatus, Long startTime, Long endTime) {
        this.id = id;
        this.address = address;
        this.state = state;
        this.reason = reason;
        this.proposalStatus = proposalStatus;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Byte getProposalStatus() {
        return proposalStatus;
    }

    public void setProposalStatus(Byte proposalStatus) {
        this.proposalStatus = proposalStatus;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
