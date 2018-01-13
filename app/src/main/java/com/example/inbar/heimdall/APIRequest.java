package com.example.inbar.heimdall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.inbar.heimdall.Law.LawActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.rgb;

public class APIRequest extends HttpsConnection{
    protected int number_of_notification = 0;
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

    private final static int NOTIFICATION = 0;


    protected Map<String, Integer> rate = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rate.put("1",R.drawable.rank1d);
        rate.put("2",R.drawable.rank2d);
        rate.put("3",R.drawable.rank3d);
        rate.put("4",R.drawable.rank4d);
        rate.put("5",R.drawable.rank5d);
        rate.put("6",R.drawable.rank6d);
        rate.put("7",R.drawable.rank7d);
    }

    public boolean isRegistered(int idLayer){
        String resp = sendJson(idLayer, new JSONObject(), "/isRegistered");
        return resp.contains(SUCCESS);
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
        if (sendJson(idLayer, request, "/register").contains(SUCCESS))
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
        if(electedOfficial == null || electedOfficial.isEmpty()) {
            onConnectionFailed(idLayer);
            return null;
        }
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

    private Handler handler_ = new Handler(){
        @Override
        public void handleMessage(Message msg){
            try {
                switch (msg.what) {
                    case NOTIFICATION:
                        JSONArray data = new JSONArray(readFromMessage(msg));
                        getAllNotification(data);
                        break;
                }
            }catch(IOException e){
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


    };

    protected void getNotificationsData(final View view) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONArray json = lawNotification(view.getId());
                if(json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, NOTIFICATION, is));
            }
        });
        thread.start();
    }

    private void getAllNotification(JSONArray notifications) {
        number_of_notification += notifications.length();
        number_of_notification = 20;
        sendNotification();
        buildCounterDrawable();
    }


    public void sendNotification() {
        String CHANNEL_ID = "my_channel_01";
        String CHANNEL_NAME = "my Channel Name";
        int NOTIFICATION_ID = 1;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLightColor(rgb(135,206,235));
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        // prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, LawActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Context mContext = getApplicationContext();
        Notification myNotification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("You have " + + number_of_notification + " unreviewed new laws")
//                .setContentText("This is your Notifiaction Text")
                .setNumber(20)
                .setColor(rgb(135,206,235))
                .setSmallIcon(R.drawable.zynga_logotype)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .build();

        notificationManager.notify(NOTIFICATION_ID, myNotification);
    }

    private void buildCounterDrawable() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_main, null);

        if (number_of_notification == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + number_of_notification);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(objElement, options);
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

//        return new BitmapDrawable(getResources(), bitmap);
    }

}
