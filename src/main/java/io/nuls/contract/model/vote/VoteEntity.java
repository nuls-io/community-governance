package io.nuls.contract.model.vote;

import io.nuls.contract.sdk.Address;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class VoteEntity {

    private Long id;
    private String title;
    private String desc;
    private VoteConfig config;
    private int status;
    private Address owner;
    private BigInteger recognizance;
    private List<VoteItem> items;
    private Set<Long> itemIdSet;
    private Integer proposalId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VoteConfig getConfig() {
        return config;
    }

    public void setConfig(VoteConfig config) {
        this.config = config;
    }

    public List<VoteItem> getItems() {
        return items;
    }

    public void setItems(List<VoteItem> items) {
        this.items = items;
    }

    public Set<Long> getItemIdSet() {
        return itemIdSet;
    }

    public void setItemIdSet(Set<Long> itemIdSet) {
        this.itemIdSet = itemIdSet;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Address getOwner() {
        return owner;
    }

    public void setOwner(Address owner) {
        this.owner = owner;
    }

    public BigInteger getRecognizance() {
        return recognizance;
    }

    public void setRecognizance(BigInteger recognizance) {
        this.recognizance = recognizance;
    }

    public Integer getProposalId() {
        return proposalId;
    }

    public void setProposalId(Integer proposalId) {
        this.proposalId = proposalId;
    }
}