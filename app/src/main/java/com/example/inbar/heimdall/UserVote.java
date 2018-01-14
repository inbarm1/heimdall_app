package com.example.inbar.heimdall;

/**
 * Created by oferh_000 on 06-Jan-18.
 */

public enum UserVote {
    VOTED_FOR("VOTED_FOR"),
    VOTED_AGAINST("VOTED_AGAINST"),
    NO_VOTE("NO_VOTE");

    private final String name;

    UserVote(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
