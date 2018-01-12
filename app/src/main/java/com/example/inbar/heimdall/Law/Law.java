package com.example.inbar.heimdall.Law;

import android.view.View;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.inbar.heimdall.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    public static final String USER_INFO = "user_info";
    public static final String JOB = "job";
    public static final String RESIDENCY = "residency";
    public static final String AGE = "age";


    ExecutorService es = Executors.newSingleThreadExecutor();

    String name;
    private VoteStatus voteStat;
    private String description;
    private String link;
    private ArrayList<String> tags;
    private Future<JSONObject> userDist;
    private Future<JSONObject> electedVotes;
    LawActivity lawActivity;


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

    public void setLawActivity(LawActivity lawActivity) {
        this.lawActivity = lawActivity;
    }

    public Future<JSONObject> setUserDist() {
        return es.submit(new Callable<JSONObject>() {

            @Override
            public JSONObject call() throws Exception {
                return lawActivity.getUserDistribution(R.id.lawLayout, name);
            }
        });
    }

    public Future<JSONObject> setElectedVotes() {
        return es.submit(new Callable<JSONObject>() {

            @Override
            public JSONObject call() throws Exception {
                return lawActivity.getLawKnessetVotes(R.id.lawLayout, name);
            }
        });
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

    public JSONObject getUserDist() {
        try {
            return userDist.get();
    } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getElectedVotes() {
        try {
            return electedVotes.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


}

