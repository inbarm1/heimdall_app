package com.example.inbar.heimdall.Law;

public enum VoteStatus {
    FOR("for"),
    AGAINST("against"),
    NO_VOTE("no_vote");

    private final String str;

    VoteStatus(String str) {
        this.str = str;
    }
}
