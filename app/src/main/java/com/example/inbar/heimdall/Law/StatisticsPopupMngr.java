package com.example.inbar.heimdall.Law;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
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
import com.numetriclabz.numandroidcharts.ChartData;
import com.numetriclabz.numandroidcharts.StackBarChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

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
    private LawActivity mLawActivity;
    private boolean isUp;
    private Boolean isPieUp;

    public static final String AGE_AGAINST = "age_against";
    public static final String AGE_FOR = "age_for";
    public static final String JOB_AGAINST = "job_against";
    public static final String JOB_FOR = "job_for";
    public static final String RESIDENT_AGAINST = "resident_against";
    public static final String RESIDNET_FOR = "resident_for";
    public static final String USER_INFO = "user_info";
    public static final String JOB = "job";
    public static final String RESIDENCY = "residency";


    public StatisticsPopupMngr(Context context, NestedScrollView lawActivityLayout, LawActivity lawActivity) {
        mContext = context;
        mLawActivityLayout = lawActivityLayout;
        isUp = false;
        isPieUp = false;
        mLawActivity = lawActivity;
    }

    public void DrawStats(Law law, ImageButton barChartCloseButton) {
        DrawElectedVotesGraph(law,barChartCloseButton);
        DrawUserDistribution(law);
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
            case VOTE_FIRST:
                resource = R.layout.activity_pop_please_vote_first;
                break;
            case DESCRIPTION:
                resource = R.layout.activity_pop_law_description;
                break;
            case VOTE:
                resource = R.layout.popup_vote_law;
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
                DrawStats(law,closeButton);
                break;
            case DESCRIPTION:
                DrawDescription(law);
                break;
            case VOTE:
                DrawVotePopUp(law);
                break;
            case VOTE_FIRST:
                break;
            default:
                return;
        }

    }

    public static enum PopUpType {
        STATS,
        VOTE_FIRST,
        DESCRIPTION,
        VOTE
    }


    public void DrawDescription(Law law) {
        TextView descriptionTextView = ((TextView) mPopupView.findViewById(R.id.lawDescriptionTextView));
        if (law.getDescription().length() < 5) {
//            descriptionTextView.setText("no description available");
        } else {
            descriptionTextView.setText(law.getDescription());
        }
    }


    public void DrawUserDistribution(Law law) {
        DrawChartDist(law, R.id.jobChart, JOB_FOR, JOB_AGAINST);
        DrawChartDist(law, R.id.cityChart, RESIDNET_FOR, RESIDENT_AGAINST);
        DrawChartDist(law, R.id.ageChart, AGE_FOR, AGE_AGAINST);
    }

    public void DrawChartDist(Law law, int layoutId, String forKey, String againstKey) {
        StackBarChart chart = (StackBarChart) mPopupView.findViewById(layoutId);
        List<String> lable = new ArrayList<>();
        List<ChartData> values = new ArrayList<>();
        try {
            values = getDistFromJson(law, lable, forKey, againstKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chart.setHorizontal_label(lable);
        chart.setData(values);
    }

    public List<ChartData> getDistFromJson(Law law, List<String> lables, String forKey, String againstKey) throws JSONException {
        ArrayList<Float> forChart = new ArrayList<>();
        ArrayList<Float> againstChart = new ArrayList<>();
        while (law.getUserDist() == null) {};
        JSONObject forDataJson = (JSONObject) law.getUserDist().get(forKey);
        JSONObject againstDataJson = (JSONObject) law.getUserDist().get(againstKey);

        Set<String> keys = fromIteratorToSet(forDataJson.keys());
        keys.addAll(fromIteratorToSet(againstDataJson.keys()));
        for (String key: keys) {
            lables.add(key);
            Float forValue = forDataJson.has(key) ? Float.valueOf(((float) forDataJson.getDouble(key))*100) : Float.valueOf(0);
            Float againstValue = againstDataJson.has(key) ? Float.valueOf(((float) againstDataJson.getDouble(key))*100) : Float.valueOf(0);
            forChart.add(forValue);
            againstChart.add(againstValue);
        }

        List<ChartData> values = new ArrayList<>();
        values.add(new ChartData(forChart.toArray(new Float[forChart.size()]), "For"));
        values.add(new ChartData(againstChart.toArray(new Float[againstChart.size()]), "Against"));
        return values;

    }

    public Set<String> fromIteratorToSet(Iterator<?> it) {

        Set<String> result = new HashSet<>();
        while (it.hasNext()) {
            result.add((String) it.next());
        }
        return result;
    }

    public void DrawVotePopUp(final Law law) {
        ImageButton upvoteButton = (ImageButton) mPopupView.findViewById(R.id.upvoteButton);
        upvoteButton.setOnClickListener(new VoteButtonListener(law, UserVote.VOTED_FOR,
                R.id.upvoteButton, mPopupView,
                R.drawable.like_small, R.drawable.like));

        ImageButton downvoteButton = (ImageButton) mPopupView.findViewById(R.id.downvoteButton);
        downvoteButton.setOnClickListener(new VoteButtonListener(law, UserVote.VOTED_AGAINST,
                R.id.downvoteButton, mPopupView,
                R.drawable.dislike_small, R.drawable.dislike));

        try {
            setSpinnerContent(R.id.tag1_spinner, mLawActivity.TAGS, null, true);
            setSpinnerContent(R.id.tag2_spinner, mLawActivity.TAGS, null, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button submitButton = (Button) mPopupView.findViewById(R.id.submitVoteButton);
        TextView lawName = (TextView) mPopupView.findViewById(R.id.lawname);
        lawName.setText(law.getName());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run(){
                        Spinner tag1Spinner = (Spinner) mPopupView.findViewById(R.id.tag1_spinner);
                        Spinner tag2Spinner = (Spinner) mPopupView.findViewById(R.id.tag2_spinner);
                        String tags = tag1Spinner.getSelectedItem().toString() + "," + tag2Spinner.getSelectedItem().toString();
                        mLawActivity.lawVoteSubmit(R.id.lawLayout, law.getName(), law.getUserVote(), tags);
                    }
                });

                thread.start();
                mPopupWindow.dismiss();
                isUp = false;
            }
        });
    }

    public void setSpinnerContent(int layout_id, JSONArray j_values, String defaultOption, boolean doSort) throws JSONException {
        Spinner s = (Spinner) mPopupView.findViewById(layout_id);
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < j_values.length(); i++) {
            String value = (String) j_values.get(i);
            if (value.isEmpty()) continue;
            values.add(value);
        }
        if (doSort)
            Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mLawActivity, android.R.layout.simple_list_item_1, values);
        s.setPrompt(defaultOption);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        s.setAdapter(adapter);
    }


    public static class VoteButtonListener implements View.OnClickListener {
        private Law mLaw;
        private final UserVote mUserVote;
        private final int mIdLayout;
        private View mPopupView;
        private final int mGrayImgId;
        private final int mColorImgId;

        public VoteButtonListener(Law law, UserVote userVote, int idLayout, View popupView, int grayId, int colorId) {
            mLaw = law;
            mUserVote = userVote;
            mIdLayout = idLayout;
            mPopupView = popupView;
            mGrayImgId = grayId;
            mColorImgId = colorId;
        }

        @Override
        public void onClick(View v) {
            ImageButton btn = (ImageButton) mPopupView.findViewById(mIdLayout);
            if (mLaw.userVote == mUserVote)
            {
                mLaw.userVote = UserVote.NO_VOTE;
                btn.setImageResource(mGrayImgId);

            }else{
                btn.setImageResource(mColorImgId);
                mLaw.userVote = mUserVote;
            }
        }
    }




    // shitty code starts here->

    public void DrawElectedVotesGraph(Law law, ImageButton barChartCloseButton ) {
        JSONObject voteJson = law.getElectedVotes();
        if (voteJson == null) {
            Log.d("DrawVotesGraph", "get elected votes from law returned null");
            return;
        }
        String[] voteTypes = {"for", "against", "abstained", "missing"};
        try {
            //build the json
            String interstedIn = "";
            if (law.userVote == UserVote.VOTED_FOR) {
                interstedIn = "for";
            } else if (law.userVote == UserVote.VOTED_AGAINST) {
                interstedIn = "against";
            } else {
                interstedIn = "for";
                //return;
            }
            String myParty = "";
            JSONObject nameToPercent = new JSONObject();
            Map<String, JSONObject> pieMap = new HashMap<>();
            int myCnt;
            int totalCnt;
            Iterator<String> partyNameIter = voteJson.keys();
            while (partyNameIter.hasNext()) {
                myCnt = 0;
                String currName = partyNameIter.next();
                JSONObject singlePartyJson = voteJson.getJSONObject(currName);
                totalCnt = singlePartyJson.getInt("number_of_members");
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
                    }
                }
                JSONObject dataJson = new JSONObject();
                for (String type : voteTypes) {
                    JSONObject singleVoteTypeJson = votesJson.getJSONObject(type);
                    int cnt = 0;
                    if (singleVoteTypeJson.has("count")) {
                        cnt = singleVoteTypeJson.getInt("count");
                    }
                    dataJson.put(type, (float) cnt / totalCnt);
                }
                pieMap.put(currName, dataJson);
                nameToPercent.put(currName, ((float) myCnt / totalCnt) * 100);
            }
            createBarChart(nameToPercent, pieMap,R.id.votedLikeMe, myParty, mPopupView, mLawActivityLayout,barChartCloseButton);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }


    private void createBarChart(JSONObject nameToPercentage, Map<String, JSONObject> pieMap, int char_id, String myName, View v,
                                View lawActivityView, ImageButton barChartCloseButton ) {

        BarChart barChart = (BarChart) v.findViewById(char_id);
        final ArrayList<BarEntry> valueList = new ArrayList<>();
        final ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        Map<String, JSONObject> map = pieMap;

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
        //set a chart value selected listener
        barChart.setOnChartValueSelectedListener(new BarPressListener(lawActivityView,isPieUp,mContext,xAxis,pieMap,barChartCloseButton));

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

    protected static void createChart(final JSONObject data, View pop) {
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

    private static void addData(PieChart mChart, JSONObject dataMem) {
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
        PieDataSet dataSet = new PieDataSet(yVals1, "אחוזי ההצבעה במפלגה");
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

    private static ArrayList<Integer> getColors() {
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


    public static class BarPressListener implements OnChartValueSelectedListener {
        private View mLawActivityView;
        private Boolean isPieUp;
        private Context mContext;
        private PopupWindow mPiePopupWindow;
        private ArrayList<String> xAxis;
        Map<String, JSONObject> mPieMap;
        ImageButton mbarChartCloseButton;
        public BarPressListener(View v, Boolean isPieUp, Context context,ArrayList<String> xAxis,
                                Map<String, JSONObject> pieMap, ImageButton barChartCloseButton){
            mLawActivityView = v;
            this.isPieUp = isPieUp;
            mContext = context;
            this.xAxis = xAxis;
            mPieMap = pieMap;
            mbarChartCloseButton = barChartCloseButton;
        }

        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
            // display msg when value selected
            if (e == null || isPieUp)
                return;

            String name = xAxis.get(e.getXIndex());
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View customView = inflater.inflate(R.layout.activity_pop_piechart, null);

            // Initialize a new instance of popup window
            mPiePopupWindow = new PopupWindow(
                    customView,
                    100,
                    180
            );

            // Set an elevation value for popup window
            // Call requires API level 21
            if (Build.VERSION.SDK_INT >= 21) {
                mPiePopupWindow.setElevation(5.0f);
            }

            // Get a reference for the custom view close button
            ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

            // Set a click listener for the popup window close button
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Dismiss the popup window
                    mPiePopupWindow.dismiss();
                    isPieUp = false;
                    mbarChartCloseButton.setVisibility(View.VISIBLE);
                    mbarChartCloseButton.setActivated(true);
                }
            });

            mPiePopupWindow.showAtLocation(mLawActivityView, Gravity.CENTER, 0, 0);

            createChart(mPieMap.get(name), customView);

            isPieUp = true;

            mbarChartCloseButton.setVisibility(View.GONE);
            mbarChartCloseButton.setActivated(false);
        }

        @Override
        public void onNothingSelected() {

        }

    }


}




