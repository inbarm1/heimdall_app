package com.example.inbar.heimdall;

/**
 * Created by oferh_000 on 06-Jan-18.
 */

public enum UserVote {
    VOTED_FOR("voted_for"),
    VOTED_AGAINST("voted_against"),
    NO_VOTE("no_vote");

    private final String name;

    UserVote(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
