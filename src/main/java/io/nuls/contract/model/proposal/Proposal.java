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

package io.nuls.contract.model.proposal;

import io.nuls.contract.func.proposal.ProposalConstant;
import io.nuls.contract.sdk.Address;

import java.util.HashMap;
import java.util.Map;

/**
 * 提案模型
 * @author: Charlie
 * @date: 2019/8/15
 */
public class Proposal {
    private Long id;
    private String name;
    private int type;
    private String desc;
    private String email;
    private Address owner;
    private ProposalConfig config;
    private int status = ProposalConstant.INREVIEW;
    private boolean recognizanceRedeemed = false;
    private Map<String, String> auditRefuseRecords = new HashMap<>();

    public Proposal() {
    }

    public Proposal(Long id, String name, int type, String desc, String email, Address owner) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.email = email;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProposalConfig getConfig() {
        return config;
    }

    public void setConfig(ProposalConfig config) {
        this.config = config;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getAuditRefuseRecords() {
        return auditRefuseRecords;
    }

    public void setAuditRefuseRecords(Map<String, String> auditRefuseRecords) {
        this.auditRefuseRecords = auditRefuseRecords;
    }

    public Address getOwner() {
        return owner;
    }

    public void setOwner(Address owner) {
        this.owner = owner;
    }

    public boolean getRecognizanceRedeemed() {
        return recognizanceRedeemed;
    }

    public void setRecognizanceRedeemed(boolean recognizanceRedeemed) {
        this.recognizanceRedeemed = recognizanceRedeemed;
    }
}
