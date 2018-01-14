package com.example.inbar.heimdall.Law;

import com.example.inbar.heimdall.UserVote;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.example.inbar.heimdall.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.graphics.Color.rgb;

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
    private UserVote voteStat;
    private String description;
    private String link;
    private ArrayList<String> tags;
    private JSONObject userDist;
    private JSONObject electedVotes;
    LawActivity lawActivity;
    boolean blocking;
    PopupWindow mPopupWindow;


    public Law(String name, JSONObject lawObject, LawActivity lawActivity) {
        this.name = name;
        try {
            this.voteStat = lawObject.getString(USER_VOTED).equals("for") ? UserVote.VOTED_FOR :
                    lawObject.getString(USER_VOTED).equals("no_vote") ? UserVote.NO_VOTE : UserVote.VOTED_AGAINST;

            this.description = lawObject.getString(DESC);
            this.link = lawObject.getString(LINK);
            this.tags = getTagsAsArray(lawObject.getJSONArray(TAGS));
            this.lawActivity = lawActivity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUserDistAndElectedVotes(LawActivity l) {
        this.setElectedVotes(l);
        this.setUserDist(l);
    }

    private void setUserDist(LawActivity l) {
        this.userDist = l.getUserDistribution(R.id.lawLayout, name);
    }

    private void setElectedVotes(LawActivity l) {
        UserVote myVote = voteStat == UserVote.NO_VOTE ? UserVote.VOTED_AGAINST : voteStat;
        this.electedVotes = l.getLawKnessetVotes(R.id.lawLayout, name, myVote);
    }

    public String getName() {
        return name;
    }

    public UserVote getVoteStat() {
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
        return userDist;
    }

    public JSONObject getElectedVotes() {
        return electedVotes;
    }


    public void DrawVotesGraph(View fatherView, int charId) {
        JSONObject voteJson = this.getElectedVotes();
        if (voteJson == null){
            Log.d("DrawVotesGraph","get elected votes from law returned null");
            return;
        }
        String [] voteTypes = {"for" , "against", "abstained", "missing"};
        try {
            //build the json
            String interstedIn = "";
            if (this.voteStat == UserVote.VOTED_FOR ){
                interstedIn = "for";
            }else if (this.voteStat == UserVote.VOTED_AGAINST ) {
                interstedIn = "against";
            }else{
                return;
            }
            String myParty = "";
            JSONObject nameToPercent = new JSONObject();
            Map<String,JSONObject> pieMap = new HashMap<>();
            int myCnt;
            int totalCnt;
            Iterator<String> partyNameIter =  voteJson.keys();
            while (partyNameIter.hasNext()) {
                myCnt = 0;
                totalCnt = 0;
                String currName = partyNameIter.next();
                JSONObject singlePartyJson = voteJson.getJSONObject(currName);
                if (singlePartyJson.getBoolean("is_users_party")) {
                    myParty = currName;
                }
                JSONObject votesJson = singlePartyJson.getJSONObject("elected_voted");
                for (String type : voteTypes) {
                    JSONObject singleVoteTypeJson = votesJson.getJSONObject(type);
                    if (singleVoteTypeJson.has("count")) {
                        int cnt = singleVoteTypeJson.getInt("count");
                        if (type == interstedIn){
                            myCnt += cnt;
                        }
                        totalCnt += cnt;
                    }
                }
                JSONObject dataJson = new JSONObject();
                for (String type : voteTypes) {
                    JSONObject singleVoteTypeJson = votesJson.getJSONObject(type);
                    if (singleVoteTypeJson.has("count")) {
                        int cnt = singleVoteTypeJson.getInt("count");
                        dataJson.put(type,(float)cnt/totalCnt);
                    }
                }
                pieMap.put(currName,dataJson);
                nameToPercent.put(currName,((float)myCnt/totalCnt) * 100 );
            }
            createBarChart(nameToPercent,pieMap,charId,myParty, fatherView);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

    }


    private void createBarChart(JSONObject nameToPercentage,Map<String,JSONObject> pieMap, int char_id, String myName, View v) {

        BarChart barChart = (BarChart) v.findViewById(char_id);
        final ArrayList<BarEntry> valueList = new ArrayList<>();
        final ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        final View myView = v;
        final Map<String, JSONObject> map = pieMap;

        try {
            Iterator<String> names = nameToPercentage.keys();
            int counter = 0;
            while( names.hasNext() ) {
                final String name = (String)names.next();
                float val = (float) nameToPercentage.getDouble(name);
                // bar name
                xAxis.add(name);
                //bar size
                BarEntry entry = new BarEntry(val, counter);
                valueList.add(entry);
                if (name.equals(myName)) {
                    colors.add(rgb(70,130,180));
                } else {
                    colors.add(rgb(135,206,235));
                }
                counter ++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
         //set a chart value selected listener
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null || blocking)
                    return;


                // Get the application context
                Context mContext = lawActivity.getApplicationContext();
                String name = xAxis.get(e.getXIndex());
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.activity_pop_piechart,null);

                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
                );

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                blocking = true;

                // Get a reference for the custom view close button
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                        blocking = false;
                    }
                });

                mPopupWindow.showAtLocation(myView, Gravity.CENTER,0,0);

                createChart(map.get(name), customView);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        BarDataSet dataSet = new BarDataSet(valueList, "מפלגות");
        dataSet.setColors(colors);
        BarData data = new BarData(xAxis, dataSet);
        barChart.setData(data);
        barChart.getLegend().setEnabled(false);

        XAxis xAxistemp = barChart.getXAxis();
        xAxistemp.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxistemp.setSpaceBetweenLabels(6);
//        xAxistemp.setXOffset(12);
        xAxistemp.setTextSize(8);
//        XAxis rot = barChart.getXAxis();
//        rot.setLabelRotationAngle(-45);

//        barChart.setDragEnabled(true); // on by default
//        barChart.setVisibleXRange(3,3); // sets the viewport to show 3 bars
        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    protected void createChart(final JSONObject data, View pop){
        PieChart mChart = (PieChart)pop.findViewById(R.id.piechart);

//        mChart = new PieChart(this);
        // add pie chart to main layout
//        customView.(mChart, 1000, 500);
//        customView.setBackgroundColor(Color.WHITE);

        // configure pie chart
        mChart.setUsePercentValues(true);
        mChart.setDescription("");

        // enable hole and configure
        mChart.setDrawHoleEnabled(true);
//        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        // add data
        addData(mChart, data);

        // customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    private void addData(PieChart mChart, JSONObject dataMem) {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xData = new ArrayList<>();
        int counter = 0;
        Iterator<?> memName = dataMem.keys();
        try {
            while(memName.hasNext()) {
                final String nameM = (String) memName.next();
                xData.add(nameM);
                float val = ((Number)dataMem.get(nameM)).floatValue() * 100;
                yVals1.add(new Entry(val, counter));
                counter++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "התפלגות חברי כנסת");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        dataSet.setColors(getColors());

        // instantiate pie data object now
        PieData data = new PieData(xData, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);
        // undo all highlights
        mChart.highlightValues(null);
        // update pie chart
        mChart.invalidate();
    }

    private ArrayList<Integer> getColors() {
        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        return colors;
    }




}

