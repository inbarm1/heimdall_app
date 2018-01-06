package com.example.inbar.heimdall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

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

    public final String  ELECTED_OFFICIAL = "elected_official";

    public final String START_DATE = "start_date";
    public final String END_DATE = "end_date";


    public boolean register(int idLayer, int birthYear, String jobCategory, String residency, String party, InvolvementLevel involvementLevel){
        if(jobCategory == null || jobCategory.isEmpty() ||
                party == null || party.isEmpty() ||
                residency == null || residency.isEmpty() ||
                birthYear == 0)
            throw new RuntimeException("Ileagal registration form");
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
            if(jobCategory != null && !jobCategory.isEmpty()) {
                request.put(JOB_CATEGORY, jobCategory);
            }
            if(residency != null && !residency.isEmpty()) {
                request.put(RESIDENCY, residency);
            }
            if(party != null && !party.isEmpty()) {
                request.put(PARTY, party);
            }
            if(involvementLevel != null) {
                request.put(INVOLVEMENT_LEVEL, involvementLevel.getName());
            }
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }

        return request.keys().hasNext() && sendJson(idLayer, request, "/updatePersonalInfo").equals(SUCCESS);
    }

    public JSONObject lawVoteSubmit(int idLayer, String lawName, UserVote userVote, String tag){
        JSONObject request = new JSONObject();
        if(lawName.isEmpty())
            throw new RuntimeException("Empty law name");
        try {
            request.put(LAW_NAME, lawName);
            request.put(VOTE, userVote.getName());
            if(tag != null && !tag.isEmpty()) {
                request.put(TAG, tag);
            }
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


    public JSONObject getUserDistribution(int idLayer, String lawName){
        JSONObject request = new JSONObject();
        try {
            request.put(LAW_NAME, lawName);
            return new JSONObject(sendJson(idLayer, request, "/getUserDistribution"));
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public JSONObject getUserToElectedOfficialMatchByTag(int idLayer, String electedOfficial, String tag){
        JSONObject request = new JSONObject();
        if(electedOfficial == null || electedOfficial.isEmpty())
            throw new RuntimeException("Empty elected official");
        try {
            request.put(ELECTED_OFFICIAL, electedOfficial);
            if(tag != null && !tag.isEmpty()){
                request.put(TAG, tag);
            }
            return new JSONObject(sendJson(idLayer, request, "/getUserToElectedOfficialMatchByTag"));
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public JSONObject getUserPartiesVotesMatchByTag(int idLayer, String tag){
        JSONObject request = new JSONObject();
        try {
            if(tag != null && !tag.isEmpty()){
                request.put(TAG, tag);
            }
            return new JSONObject(sendJson(idLayer, request, "/getUserPartiesVotesMatchByTag"));
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public JSONObject getLawsByDateInterval(int idLayer, String startDate, String endDate){

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        if(startDate == null || startDate.isEmpty() ||
                endDate == null || endDate.isEmpty())
            throw new RuntimeException("start/end date cant be empty/null");
        try{
            format.parse(endDate);
            format.parse(startDate);
        } catch (ParseException e){
            throw new RuntimeException("start/end date must be in format of dd/MM/yyyy",e);
        }
        JSONObject request = new JSONObject();
        try {
            request.put(START_DATE, startDate);
            request.put(END_DATE, endDate);
            return new JSONObject(sendJson(idLayer, request, "/getLawsByDateInterval"));
        }
        catch (JSONException e){
            throw new RuntimeException(e);
        }
    }







}
