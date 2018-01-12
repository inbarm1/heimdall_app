package com.example.inbar.heimdall.Law;

import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eilon on 05/01/2018.
 */

public class Law {
    public static final String LINK = "link";
    public static final String DESC = "description";
    public static final String TAGS = "tags";
    public static final String USER_VOTED = "user_voted";
    public static final String JOB_FOR = "job_for";
    public static final String JOB_AGAINST = "job_against";
    public static final String RESIDENT_FOR = "resident_for";
    public static final String RESIDENT_AGAINST = "resident_against";
    public static final String AGE_FOR = "age_for";
    public static final String AGE_AGAINST = "age_against";



    private String name;
    private VoteStatus voteStat;
    private String description;
    private String link;
    private ArrayList<String> tags;
    private HashMap<String, HashMap<String, Float>> userDist;

    public Law(String name, JSONObject lawObject) {
        this.name = name;
        try {
            this.voteStat = VoteStatus.valueOf(lawObject.getString(USER_VOTED).toUpperCase());
            this.description = lawObject.getString(DESC);
            this.link = lawObject.getString(LINK);
            this.tags = getTagsAsArray(lawObject.getJSONArray(TAGS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public VoteStatus getVoteStat() {
        return voteStat;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    private ArrayList<String> getTagsAsArray(JSONArray jArray) throws JSONException {
        ArrayList<String> tags = new ArrayList<>();
        if (jArray == null) return tags;
        for (int i = 0; i < jArray.length(); i++) {
            tags.add(jArray.getString(i));
        }
        return tags;
    }


    public void setUserDist(JSONObject distObject) {
        Type type = new TypeToken<HashMap<String, HashMap<String, Float>>>(){}.getType();
        userDist = new Gson().fromJson(distObject.toString(), type);
    }

    public HashMap<String, HashMap<String, Float>> getUserDist() {
        return userDist;
    }

}

