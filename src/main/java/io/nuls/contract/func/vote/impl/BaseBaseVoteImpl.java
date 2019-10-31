package io.nuls.contract.func.vote.impl;

import io.nuls.contract.event.vote.RedemptionVoteEvent;
import io.nuls.contract.event.vote.VoteCreateEvent;
import io.nuls.contract.event.vote.VoteEvent;
import io.nuls.contract.event.vote.VoteInitEvent;
import io.nuls.contract.func.vote.BaseVote;
import io.nuls.contract.func.vote.VoteStatus;
import io.nuls.contract.model.vote.VoteConfig;
import io.nuls.contract.model.vote.VoteEntity;
import io.nuls.contract.model.vote.VoteItem;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Msg;

import java.math.BigInteger;
import java.util.*;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class BaseBaseVoteImpl implements BaseVote {
    private static final BigInteger RECOGNIZANCE = new BigInteger("1000000000");
    protected Map<Long, VoteEntity> votes = new HashMap<Long, VoteEntity>();
    protected Map<Long, Map<Address, List<Long>>> voteRecords = new HashMap<Long, Map<Address, List<Long>>>();


    @Override
    public List<VoteEntity> getVotedVotes(Address address) {
        List<VoteEntity> list = new ArrayList<VoteEntity>();
        for (Map.Entry<Long, Map<Address, List<Long>>> entry : voteRecords.entrySet()){
            if(entry.getValue().containsKey(address)){
                list.add(votes.get(entry.getKey()));
            }
        }
        return list;
    }

    @Override
    public void invalidVotes(Address address) {
        require(null != address, "address can not empty");
        List<VoteEntity> voteList = this.getVotedVotes(address);
        for (VoteEntity voteEntity : voteList){
            if(this.canVote(voteEntity.getId())){
                voteRecords.get(voteEntity.getId()).remove(address);
            }
        }
    }

    @Override
    public VoteEntity create(String title, String desc, String[] items, Integer proposalId) {
        require(items != null && items.length > 0, "items can not empty");
        require(title != null, "title can not empty");
        require(desc != null, "desc can not empty");
        BigInteger value = Msg.value();
        require(value.compareTo(RECOGNIZANCE) >= 0, "value need greater than " + RECOGNIZANCE);
        Long voteId = Long.valueOf(votes.size() + 1);

        VoteEntity voteEntity = new VoteEntity();
        voteEntity.setId(voteId);
        voteEntity.setTitle(title);
        voteEntity.setRecognizance(value);
        voteEntity.setProposalId(proposalId);

        List<VoteItem> itemList = new ArrayList<VoteItem>();
        Set<Long> itemIdSet = new HashSet<Long>();
        VoteItem item;
        Long itemIdLong;
        for(int itemId = 0 ; itemId < items.length ; itemId++) {
            item = new VoteItem();
            itemIdLong = (long) (itemId + 1);
            item.setId(itemIdLong);
            item.setContent(items[itemId]);
            itemList.add(item);
            itemIdSet.add(itemIdLong);
        }
        voteEntity.setItems(itemList);
        voteEntity.setItemIdSet(itemIdSet);
        voteEntity.setStatus(VoteStatus.STATUS_VOTEING);
        voteEntity.setOwner(Msg.sender());
        votes.put(voteId, voteEntity);
        emit(new VoteCreateEvent(voteId, title, desc, VoteStatus.STATUS_VOTEING, Msg.sender().toString(), value, itemList, proposalId));
        return voteEntity;
    }

    @Override
    public boolean init(long voteId, VoteConfig config) {
        require(voteId > 0L, "voteId error");
        require(config != null, "config can not null");
        onlyOwner(voteId);
        require(config.check(), "the config is error");
        VoteEntity voteEntity = votes.get(voteId);
        require(voteEntity != null, "vote not find");
        require(voteEntity.getStatus() == VoteStatus.STATUS_VOTEING, "vote status error");
        List<VoteItem> items = voteEntity.getItems();
        require(items != null && items.size() > 0, "vote item can not empty");
        if(config.isMultipleSelect()) {
            require(config.getMaxSelectCount() <= items.size(), "max select count can not greater then item size");
        }
        voteEntity.setConfig(config);
        emit(new VoteInitEvent(voteId, config));
        return true;
    }

    @Override
    public boolean vote(long voteId, long[] itemIds) {
        require(canVote(voteId), "can not vote, please check.");
        int itemIdsLength = itemIds.length;
        require(itemIds != null && itemIdsLength > 0, "item id can not empty");

        VoteEntity voteEntity = votes.get(voteId);
        VoteConfig config = voteEntity.getConfig();
        if(config.isMultipleSelect()) {
            require(itemIdsLength <= config.getMaxSelectCount(), "option cannot be greater than " + config.getMaxSelectCount());
        }
        if(!config.isMultipleSelect()) {
            require(itemIdsLength == 1, "only support single selection");
        }
        List<VoteItem> items = voteEntity.getItems();
        Set<Long> itemIdSet = voteEntity.getItemIdSet();
        List<Long> itemIdList = new ArrayList<Long>();

        Long itemId;
        for(int i = 0 ; i < itemIdsLength ; i++) {
            itemId = itemIds[i];
            require(itemIdSet.contains(itemId), "entered the wrong item id");
            itemIdList.add(itemId);
        }
        Map<Address, List<Long>> records = voteRecords.get(voteId);
        if(records == null) {
            records = new HashMap<Address, List<Long>>();
            voteRecords.put(voteId, records);
        }
        if(!config.isVoteCanModify()) {
            require(records.get(Msg.sender()) == null, "already voted");
        }
        records.put(Msg.sender(), itemIdList);
        emit(new VoteEvent(voteId, Msg.sender().toString(), itemIdList));
        return true;
    }

    @Override
    public boolean redemption(long voteId) {
        require(voteId > 0L, "voteId error");
        VoteEntity voteEntity = votes.get(voteId);
        require(voteEntity != null, "vote is not find");
        if(voteEntity.getStatus() != VoteStatus.STATUS_CLOSE) {
            require(!canVote(voteId), "current vote is not over yet");
        }
        require(voteEntity.getStatus() == VoteStatus.STATUS_CLOSE, "vote has not closed");
        require(voteEntity.getRecognizance().compareTo(BigInteger.ZERO) > 0, "recognizance has been redeemed");
        BigInteger balance = Msg.address().balance();
        require(balance.compareTo(voteEntity.getRecognizance()) >= 0, "The contract balance is less than the recognizance amount");
        // return amount
        voteEntity.getOwner().transfer(voteEntity.getRecognizance());
        voteEntity.setRecognizance(BigInteger.ZERO);
        emit(new RedemptionVoteEvent(voteId, voteEntity.getOwner().toString()));
        return true;
    }

    @Override
    public boolean canVote(long voteId) {
        require(voteId > 0L, "voteId error");
        VoteEntity voteEntity = votes.get(voteId);
        if(voteEntity == null) {
            return false;
        }
        VoteConfig config = voteEntity.getConfig();
        if(config == null) {
            return false;
        }
        if(config.getStartTime() > Block.timestamp()) {
            return false;
        }
        if(voteEntity.getStatus() != VoteStatus.STATUS_VOTEING) {
            return false;
        }
        if(config.getEndTime() < Block.timestamp()) {
            if(voteEntity.getStatus() != VoteStatus.STATUS_CLOSE) {
                updateStatus(voteEntity, VoteStatus.STATUS_CLOSE);
            }
            return false;
        }
        return true;
    }

    @Override
    public VoteEntity queryVote(long voteId) {
        require(voteId > 0L, "voteId error");
        VoteEntity voteEntity = votes.get(voteId);
        require(voteEntity != null, "vote is not find");
        List<VoteItem> items = voteEntity.getItems();
        voteEntity.setItems(items);
        return voteEntity;
    }

    @Override
    public Map<Address, List<Long>> queryVoteResult(long voteId) {
        require(voteId > 0L, "voteId error");
        VoteEntity voteEntity = votes.get(voteId);
        require(voteEntity != null, "vote is not find");
        Map<Address, List<Long>> records = voteRecords.get(voteId);
        if(records == null) {
            return new HashMap<Address, List<Long>>();
        }
        return records;
    }

    @Override
    public boolean queryAddressHasVote(long voteId, Address address) {
        require(voteId > 0L, "voteId error");
        require(address != null, "address is empty");
        VoteEntity voteEntity = votes.get(voteId);
        require(voteEntity != null, "vote is not find");
        Map<Address, List<Long>> records = voteRecords.get(voteId);
        require(records != null, "records is null");
        return records.get(address) != null;
    }

    private void updateStatus(VoteEntity voteEntity, int statusVoteing) {
        voteEntity.setStatus(statusVoteing);
    }

    private void onlyOwner(long voteId) {
        require(voteId > 0L, "voteId error");
        VoteEntity voteEntity = votes.get(voteId);
        require(voteEntity != null, "vote is not find");
        require(voteEntity.getOwner().equals(Msg.sender()), "is not owner");
    }
}
