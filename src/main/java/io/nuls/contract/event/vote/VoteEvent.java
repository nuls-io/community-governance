package io.nuls.contract.event.vote;

import io.nuls.contract.sdk.Event;

import java.util.List;

public class VoteEvent implements Event {

    private Long voteId;
    private String voterAddress;
    private List<Long> itemIds;

    public VoteEvent(Long voteId, String voterAddress, List<Long> itemIds) {
        this.voteId = voteId;
        this.voterAddress = voterAddress;
        this.itemIds = itemIds;
    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public String getVoterAddress() {
        return voterAddress;
    }

    public void setVoterAddress(String voterAddress) {
        this.voterAddress = voterAddress;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }
}