package com.example.inbar.heimdall;

/**
 * Created by oferh_000 on 06-Jan-18.
 */

enum InvolvementLevel {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH");

    private final String name;

    InvolvementLevel(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }


}
