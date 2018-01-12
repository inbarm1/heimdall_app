package com.example.inbar.heimdall.Law;

import android.graphics.drawable.Drawable;

import org.json.JSONObject;

/**
 * Created by Eilon on 05/01/2018.
 */

public class Law {
    public static final String LINK = "link";
    public static final String DESC = "description";
    public static final String TAGS = "tags";
    public static final String USER_VOTED = "user_voted";



    public String name;
    public VoteStatus voteStat;
    public String description;
    public String link;

    public Law(){}

    public Law(JSONObject lawObject) {

    }

}

