package io.nuls.contract.func.vote;

public interface VoteStatus {
    int STATUS_WAIT_INIT = 0;
    int STATUS_WAIT_VOTE = 1;
    int STATUS_VOTEING = 2;
    int STATUS_CLOSE = 3;
}