package io.nuls.contract.event.vote;

import io.nuls.contract.model.vote.VoteItem;
import io.nuls.contract.sdk.Event;

import java.math.BigInteger;
import java.util.List;

public class VoteCreateEvent implements Event {

    private Long voteId;
    private String title;
    private String desc;
    private int status;
    private String owner;
    private BigInteger recognizance;
    private List<VoteItem> items;


    public VoteCreateEvent(Long voteId, String title, String desc, int status, String owner, BigInteger recognizance, List<VoteItem> items) {
        this.voteId = voteId;
        this.title = title;
        this.desc = desc;
        this.status = status;
        this.owner = owner;
        this.recognizance = recognizance;
        this.items = items;
    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public BigInteger getRecognizance() {
        return recognizance;
    }

    public void setRecognizance(BigInteger recognizance) {
        this.recognizance = recognizance;
    }

    public List<VoteItem> getItems() {
        return items;
    }

    public void setItems(List<VoteItem> items) {
        this.items = items;
    }
}
