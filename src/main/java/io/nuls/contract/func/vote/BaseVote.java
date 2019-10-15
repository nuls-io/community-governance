package io.nuls.contract.func.vote;

import io.nuls.contract.model.vote.VoteConfig;
import io.nuls.contract.model.vote.VoteEntity;
import io.nuls.contract.sdk.Address;

import java.util.List;
import java.util.Map;

public interface BaseVote {

    VoteEntity create(String title, String desc, String[] items, Integer proposalId);

    boolean init(long voteId, VoteConfig config);

    boolean vote(long voteId, long[] itemIds);

    boolean canVote(long voteId);

    boolean redemption(long voteId);

    VoteEntity queryVote(long voteId);

    Map<Address, List<Long>> queryVoteResult(long voteId);

    boolean queryAddressHasVote(long voteId, Address address);
}