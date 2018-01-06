package com.example.inbar.heimdall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oferh_000 on 06-Jan-18.
 */

public class APIRequest extends HttpsConnection {
    private final String SUCCESS = "Success";

    public final String  BIRTH_YEAR = "birth_year";
    public final String  JOB_CATEGORY = "job";
    public final String  RESIDENCY = "residency";
    public final String  PARTY = "party";
    public final String  INVOLVEMENT_LEVEL = "involvement_level";

    public final String  LAW_NAME = "law_name";
    public final String  VOTE = "vote";
    public final String  TAG = "tags";

    

    public boolean register(int idLayer, int birthYear, String jobCategory, String residency, String party, InvolvementLevel involvementLevel){
        JSONObject request = new JSONObject();
        try {
            request.put(BIRTH_YEAR, birthYear);
            request.put(JOB_CATEGORY, jobCategory);
            request.put(RESIDENCY, residency);
            request.put(PARTY, party);
            request.put(INVOLVEMENT_LEVEL, involvementLevel.getName());
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
        if (sendJson(idLayer, request, "/register").equals(SUCCESS))
            return true;
        return false;
    }

    public boolean updatePersonalInfo(int idLayer, String jobCategory, String residency, String party, InvolvementLevel involvementLevel){
        JSONObject request = new JSONObject();
        try {
            request.put(JOB_CATEGORY, jobCategory);
            request.put(RESIDENCY, residency);
            request.put(PARTY, party);
            request.put(INVOLVEMENT_LEVEL, involvementLevel.getName());
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
        if (sendJson(idLayer, request, "/updatePersonalInfo").equals(SUCCESS))
            return true;
        return false;
    }

    public JSONObject lawVoteSubmit(int idLayer, String lawName, UserVote userVote, String tag){
        JSONObject request = new JSONObject();
        try {
            request.put(LAW_NAME, lawName);
            request.put(VOTE, userVote.getName());
            request.put(TAG, tag);
            return new JSONObject(sendJson(idLayer, request, "/lawVoteSubmit"));
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public String getUserRank(int idLayer){
        JSONObject request = new JSONObject();
        try {
            return (new JSONObject(sendJson(idLayer, request, "/getUserRank"))).getString("user_rank");
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public JSONArray lawNotification(int idLayer){
        JSONObject request = new JSONObject();
        try {
            return new JSONArray(sendJson(idLayer, request, "/lawNotification"));
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public String getUserAssociatedParty(int idLayer) {
        JSONObject request = new JSONObject();
        try {
            return (new JSONObject(sendJson(idLayer, request, "/getUserAssociatedParty"))).getString("user_party");
        } catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public JSONObject generalInfoPartiesEfficency(int idLayer, String) {

    }
}
