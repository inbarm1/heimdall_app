package com.example.inbar.heimdall;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.DrawableRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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

    public final String PRECENT_SAME= "same";
    public final String PRECENT_ABSENT= "member_absent";
    public final String PRECENT_DIFFERENT= "different";

    public final String IS_USER_PARTY= "is_users_party";
    public final String MATCH= "match";

    public final String USER_INFO="user_info";

    public final String JOB_FOR = "job_for";
    public final String JOB_AGAINST = "job_against";
    public final String RESIDENT_FOR = "resident_for";
    public final String RESIDENT_AGAINST = "resident_against";
    public final String AGE_FOR = "age_for";
    public final String AGE_AGAINST = "age_against";


    protected Map<String, Integer> rate = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rate.put("תייר",R.drawable.rank1d);
        rate.put("אזרח",R.drawable.rank2d);
        rate.put("עסקן",R.drawable.rank3d);
        rate.put("ראש עיר",R.drawable.rank4d);
        rate.put("חבר כנסת",R.drawable.rank5d);
        rate.put("לוביסט",R.drawable.rank6d);
        rate.put("טייקון",R.drawable.rank7d);
    }

    public boolean isRegistered(int idLayer){
        return sendJson(idLayer, new JSONObject(), "/isRegistered").equals("Success");
    }

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
            e.printStackTrace();
            onConnectionFailed(idLayer);
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
            e.printStackTrace();
            onConnectionFailed(idLayer);
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
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONObject getUserRank(int idLayer){
        JSONObject request = new JSONObject();
        try {
            return (new JSONObject(sendJson(idLayer, request, "/getUserRank")));
        }
        catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONArray lawNotification(int idLayer) {
        JSONObject request = new JSONObject();
        try {
            return new JSONArray(sendJson(idLayer, request, "/lawNotification"));
        } catch (JSONException e) {
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONObject getUserDistribution(int idLayer, String lawName){
        JSONObject request = new JSONObject();
        try {
            request.put(LAW_NAME, lawName);
            return new JSONObject(sendJson(idLayer, request, "/getUserDistribution"));
        }
        catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
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
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
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
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
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
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }


    public String getUserAssociatedParty(int idLayer) {
        JSONObject request = new JSONObject();
        try {
            return (new JSONObject(sendJson(idLayer, request, "/getUserAssociatedParty"))).getString("user_party");
        } catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONObject getLawKnessetVotes(int idLayer, String lawName) {
        JSONObject request = new JSONObject();
        try {
            request.put(LAW_NAME, lawName);
            return new JSONObject(sendJson(idLayer, request, "/getLawKnessetVotes"));
        } catch (JSONException e) {
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONObject getAllAbsentFromVotesByTag(int idLayer, String tag) {
        JSONObject request = new JSONObject();
        try {
            if(tag != null && !tag.isEmpty()) {
                request.put(TAG, tag);
            }
            return new JSONObject(sendJson(idLayer, request, "/getAllAbsentFromVotesByTag"));
        } catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONObject getAllPartiesEfficiencyByTag(int idLayer, String tag) {
        JSONObject request = new JSONObject();
        try {
            if(tag != null && !tag.isEmpty()) {
                request.put(TAG, tag);
            }
            return new JSONObject(sendJson(idLayer, request, "/getAllPartiesEfficiencyByTag"));
        } catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONArray getElectedOfficials(int idLayer) {
        JSONObject request = new JSONObject();
        try {
            return new JSONArray(sendJson(idLayer, request, "/getElectedOfficials"));
        } catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONObject getCategoryNames(int idLayer) {
        JSONObject request = new JSONObject();
        try {
            return new JSONObject(sendJson(idLayer, request, "/getCategoryNames"));
        } catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    public JSONObject getAllLawProposalsByTag(int idLayer, String tag) {
        JSONObject request = new JSONObject();
        try {
            if(tag != null && !tag.isEmpty()) {
                request.put(TAG, tag);
            }
            return new JSONObject(sendJson(idLayer, request, "/getAllLawProposalsByTag"));
        } catch (JSONException e){
            e.printStackTrace();
            onConnectionFailed(idLayer);
            return null;
        }
    }

    protected String readFromMessage(Message msg) throws IOException {
        InputStream is = (InputStream)msg.obj;
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        return responseStrBuilder.toString();
    }
}
