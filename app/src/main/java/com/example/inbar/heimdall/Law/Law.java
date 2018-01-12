package com.example.inbar.heimdall.Law;

import android.graphics.drawable.Drawable;

/**
 * Created by Eilon on 05/01/2018.
 */

public class Law {
    public String name;
    public VoteStatus voteStat;
    public String description;
    public Drawable image;

    public Law(){}

    public Law(String name, VoteStatus status, String description, Drawable image) {
        this.name = name;
        this.voteStat = status;
        this.description = description;
        this.image = image;
    }

}

