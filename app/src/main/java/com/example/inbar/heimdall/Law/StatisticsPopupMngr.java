package com.example.inbar.heimdall.Law;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.inbar.heimdall.R;
import com.example.inbar.heimdall.UserVote;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.graphics.Color.rgb;

/**
 * Created by Eilon on 13/01/2018.
 */

public class StatisticsPopupMngr {
    private Context mContext;
    private View mPopupView;
    private PopupWindow mPopupWindow;
    private NestedScrollView mLawActivityLayout;
    private boolean isUp;


    public StatisticsPopupMngr(Context context, NestedScrollView lawActivityLayout) {
        mContext = context;
        mLawActivityLayout = lawActivityLayout;
        isUp = false;
    }

    public void DrawStats(Law law) {
        DrawElectedVotesGraph(law);
    }

    public void openPopUp(PopUpType type, Law law) {
        if (isUp) {
            return;
        }
        int resource = 0;
        switch (type) {
            case STATS:
                resource = R.layout.activity_pop_law_stats;
                break;
            case DESCRIPTION:
                resource = R.layout.activity_pop_law_description;
                break;
            case VOTE:
                resource = R.layout.activity_pop_law_description;
                break;
            default:
                resource = R.layout.activity_pop_law_description;
                break;
        }

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        mPopupView = inflater.inflate(resource, null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                mPopupView,
                NestedScrollView.LayoutParams.WRAP_CONTENT,
                NestedScrollView.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) mPopupView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
                isUp = false;
            }
        });

        mPopupWindow.showAtLocation(mLawActivityLayout, Gravity.CENTER, 0, 0);
        isUp = true;

        switch (type) {
            case STATS:
                DrawStats(law);
                break;
            case DESCRIPTION:
                DrawDescription(law);
                break;
            case VOTE:
                break;
            default:
                return;
        }

    }

    public static enum PopUpType {
        STATS,
        DESCRIPTION,
        VOTE
    }


    public void DrawDescription(Law law) {
        TextView descriptionTextView = ((TextView) mPopupView.findViewById(R.id.lawDescriptionTextView));
        if (law.getDescription().length() < 5) {
            descriptionTextView.setText("no description available");
        } else {
            descriptionTextView.setText(law.getDescription());
        }
    }




    // shitty code starts here->

    public void DrawElectedVotesGraph(Law law) {
        JSONObject voteJson = law.getElectedVotes();
        if (voteJson == null) {
            Log.d("DrawVotesGraph", "get elected votes from law returned null");
            return;
        }
        String[] voteTypes = {"for", "against", "abstained", "missing"};
        try {
            //build the json
            String interstedIn = "";
            if (law.voteStat == UserVote.VOTED_FOR) {
                interstedIn = "for";
            } else if (law.voteStat == UserVote.VOTED_AGAINST) {
                interstedIn = "against";
            } else {
                return;
            }
            String myParty = "";
            JSONObject nameToPercent = new JSONObject();
            Map<String, JSONObject> pieMap = new HashMap<>();
            int myCnt;
            int totalCnt;
            Iterator<String> partyNameIter = voteJson.keys();
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
                        if (type == interstedIn) {
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
                        dataJson.put(type, (float) cnt / totalCnt);
                    }
                }
                pieMap.put(currName, dataJson);
                nameToPercent.put(currName, ((float) myCnt / totalCnt) * 100);
            }
            createBarChart(nameToPercent, pieMap,R.id.votedLikeMe, myParty, mPopupView);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }


    private void createBarChart(JSONObject nameToPercentage, Map<String, JSONObject> pieMap, int char_id, String myName, View v) {

        BarChart barChart = (BarChart) v.findViewById(char_id);
        final ArrayList<BarEntry> valueList = new ArrayList<>();
        final ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        final View myView = v;
        final Map<String, JSONObject> map = pieMap;

        try {
            Iterator<String> names = nameToPercentage.keys();
            int counter = 0;
            while (names.hasNext()) {
                final String name = (String) names.next();
                float val = (float) nameToPercentage.getDouble(name);
                // bar name
                xAxis.add(name);
                //bar size
                BarEntry entry = new BarEntry(val, counter);
                valueList.add(entry);
                if (name.equals(myName)) {
                    colors.add(rgb(70, 130, 180));
                } else {
                    colors.add(rgb(135, 206, 235));
                }
                counter++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        //set a chart value selected listener
//        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//
//            @Override
//            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//                // display msg when value selected
//                if (e == null || blocking)
//                    return;
//
//
//                // Get the application context
//                Context mContext = lawActivity.getApplicationContext();
//                String name = xAxis.get(e.getXIndex());
//                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                // Inflate the custom layout/view
//                View customView = inflater.inflate(R.layout.activity_pop_piechart, null);
//
//                // Initialize a new instance of popup window
//                mPopupWindow = new PopupWindow(
//                        customView,
//                        CoordinatorLayout.LayoutParams.WRAP_CONTENT,
//                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
//                );
//
//                // Set an elevation value for popup window
//                // Call requires API level 21
//                if (Build.VERSION.SDK_INT >= 21) {
//                    mPopupWindow.setElevation(5.0f);
//                }
//
//                blocking = true;
//
//                // Get a reference for the custom view close button
//                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
//
//                // Set a click listener for the popup window close button
//                closeButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // Dismiss the popup window
//                        mPopupWindow.dismiss();
//                        blocking = false;
//                    }
//                });
//
//                mPopupWindow.showAtLocation(myView, Gravity.CENTER, 0, 0);
//
//                createChart(map.get(name), customView);
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });

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

    protected void createChart(final JSONObject data, View pop) {
        PieChart mChart = (PieChart) pop.findViewById(R.id.piechart);

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
            while (memName.hasNext()) {
                final String nameM = (String) memName.next();
                xData.add(nameM);
                float val = ((Number) dataMem.get(nameM)).floatValue() * 100;
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


    public void DrawUserDistGraph(Law law) {
        JSONObject voteJson = law.getElectedVotes();
        if (voteJson == null) {
            Log.d("DrawVotesGraph", "get elected votes from law returned null");
            return;
        }
        String[] voteTypes = {"for", "against", "abstained", "missing"};
        try {
            //build the json
            String interstedIn = "";
            if (law.voteStat == UserVote.VOTED_FOR) {
                interstedIn = "for";
            } else if (law.voteStat == UserVote.VOTED_AGAINST) {
                interstedIn = "against";
            } else {
                return;
            }
            String myParty = "";
            JSONObject nameToPercent = new JSONObject();
            Map<String, JSONObject> pieMap = new HashMap<>();
            int myCnt;
            int totalCnt;
            Iterator<String> partyNameIter = voteJson.keys();
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
                        if (type == interstedIn) {
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
                        dataJson.put(type, (float) cnt / totalCnt);
                    }
                }
                pieMap.put(currName, dataJson);
                nameToPercent.put(currName, ((float) myCnt / totalCnt) * 100);
            }
            createBarChart(nameToPercent, pieMap,R.id.votedLikeMe, myParty, mPopupView);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

}




