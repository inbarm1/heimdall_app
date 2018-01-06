package com.example.inbar.heimdall;

/**
 * Created by oferh_000 on 06-Jan-18.
 */

enum UserVote {
    VOTED_FOR("VOTED_FOR"),
    VOTED_AGAINST("VOTED_AGAINST");

    private final String name;

    UserVote(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
