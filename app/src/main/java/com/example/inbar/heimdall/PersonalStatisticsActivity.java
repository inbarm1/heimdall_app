package com.example.inbar.heimdall;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.rgb;
public class PersonalStatisticsActivity extends APIRequest {

    protected CoordinatorLayout personalLayout;
    protected PieChart pChart;
    protected String currTag = null;
    protected String currElected = null;
    private final static int CHART_1 = 0;
    private final static int CHART_2 = 1;
    private final static int CHART_3 = 2;
    private final static int TAGS_HANDLER = 3;
    private final static int RATE_HANDLER = 4;
    private static String userPartyName;
    public static final String EFFICIENCY   = "party_efficiency";
    public static final String PROPOSALS    = "num_of_proposals";
    public static final String MISSING      = "party_missing";
    public static final String EFFICIENCY_M = "memeber_efficiency";
    public static final String PROPOSALS_M  = "elected_proposals";
    public static final String MISSING_M    = "member_missing";
    public static final String EFFICIENCY_T = "יעילות המפלגות";
    public static final String PROPOSALS_T  = "הצעות חוק";
    public static final String MISSING_T    = "העדרויות";
    public static final String TITLE_PIE    = "התפלגות";
    public static final String GENERAL      = "כללי";


    private Handler handler_ = new Handler(){
        @Override
        public void handleMessage(Message msg){
            try {
                switch (msg.what) {
                    case CHART_1:
                        JSONObject data1 = new JSONObject(readFromMessage(msg));
                        createBarChart(R.id.chart1p, data1,userPartyName,  EFFICIENCY, EFFICIENCY_M);
                        break;
//                    case CHART_2:
//                        JSONObject data2 = new JSONObject(readFromMessage(msg));
//                        createBarChart(R.id.chart2, data2,userPartyName,  PROPOSALS, PROPOSALS_M, chart2);
//                        break;
//                    case CHART_3:
//                        JSONObject data3 = new JSONObject(readFromMessage(msg));
//                        createBarChart(R.id.chart3, data3,userPartyName, MISSING, MISSING_M, chart3);
//                        break;
                    case TAGS_HANDLER:
                        JSONObject category = new JSONObject(readFromMessage(msg));
                        analyzeTags(category);
                        break;
                    case RATE_HANDLER:
                        JSONObject data4 = new JSONObject(readFromMessage(msg));
                        String rank = data4.getString("user_rank");
                        ImageView img = (ImageView) findViewById(R.id.ratep);
                        img.setImageResource(rate.get(rank));

                }
            }catch(IOException e){
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        personalLayout =  findViewById(R.id.personal_layout);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String str = getUserAssociatedParty(R.id.personal_layout);
                userPartyName = str;
            }
        });
        thread.start();
        createTags();
        PieChart mChart = (PieChart)findViewById(R.id.chart2p);
        mChart.setCenterText("בחר חבר כנסת");
        mChart.setCenterTextColor(BLACK);
        mChart.setCenterTextSize(20);
    }


    private void createCharts(String tag){
        getRate();
        createMatchChar(tag);
//        createMissingChar(tag);
//        createProposalsChar(tag);
    }
    private void createMatchChar(final String tag) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONObject json = getUserPartiesVotesMatchByTag(R.id.personal_layout, tag);
                if (json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, CHART_1, is));
            }
        });
        thread.start();
    }

    private void createBarChart(int char_id, JSONObject parties, String userPartyName, String key, String keyMember) {

        BarChart barChart = (BarChart) findViewById(char_id);
        final ArrayList<BarEntry> valueSet = new ArrayList<>();
        final ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        try {
            Iterator<?> partyName = parties.keys();
            int counter = 0;
            while( partyName.hasNext() ) {
                final String name = (String)partyName.next();
                // Add party name
                xAxis.add(name);
                // Get party's data
                if ( parties.get(name) instanceof JSONObject ) {
                    JSONObject data = (JSONObject)parties.get(name);
                    float val = Math.round((((Number)data.get(key)).floatValue() * 100 * 100.0) / 100.0);
                    BarEntry entry = new BarEntry(val, counter);
                    if (name.equals(userPartyName)) {
                        colors.add(rgb(70,130,180));
                    } else {
                        colors.add(rgb(135,206,235));
                    }
                    // Get value of party
                    valueSet.add(entry);
                    // Go over party members
                    if (data.get(keyMember) instanceof JSONObject ) {
                        JSONObject memData = (JSONObject)data.get(keyMember);
                        final JSONObject dataForShow = new JSONObject();
                        Iterator<?> memName = memData.keys();
                        while(memName.hasNext()) {
                            String nameM = (String)memName.next();
                            float valM = ((Number)memData.get(nameM)).floatValue() * 100;
                            dataForShow.put(nameM, valM);
                        }
                    }
                }
                counter ++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // set a chart value selected listener
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;

                Context context = getApplicationContext();
//                Toast.makeText(PersonalStatisticsActivity.this, (name + " = " + map.get(name).toString()), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

        BarDataSet dataSet = new BarDataSet(valueSet, "מפלגות");
        dataSet.setColors(colors);;
        BarData data = new BarData(xAxis, dataSet);
        barChart.setData(data);
//        XAxis rot = barChart.getXAxis();
//        rot.setLabelRotationAngle(-45);

//        barChart.setDragEnabled(true); // on by default
//        barChart.setVisibleXRange(3,3); // sets the viewport to show 3 bars
        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    private void analyzeTags(JSONObject category) {
        final ArrayList<String> tags = new ArrayList<String>();
        try {
            tags.add(GENERAL);
            JSONArray jsonTags = (JSONArray)category.get(TAG);
            for (int i=0; i< jsonTags.length(); i++) {
                final String name = (String) jsonTags.get(i);
                // Add party name
                if (name.equals(GENERAL)) {
                    continue;
                }
                tags.add(name);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        createFirstView(tags);
    }


    private void createFirstView(final ArrayList<String> tags) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tags);
        Spinner spinTag = (Spinner)findViewById(R.id.tag);
        spinTag.setAdapter(adapter);

        spinTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                createCharts(tags.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                createCharts(null);
            }

        });
    }

    private void getRate() {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String str = getUserRank(R.id.personal_layout).toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, RATE_HANDLER, is));
            }
        });
        thread.start();
    }

    private void createTags() {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String str = getCategoryNames(R.id.personal_layout).toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, TAGS_HANDLER, is));
            }
        });
        thread.start();
    }

    protected void creatingChart(final JSONObject data){
        View view = (View)findViewById(R.id.personal_layout);
        PieChart mChart = (PieChart)view.findViewById(R.id.chart2p);

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
        PieDataSet dataSet = new PieDataSet(yVals1, "התאמה לחבר כנסת");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

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


//    protected void createTags() {
//        JSONObject category = getCategoryNames(R.id.personal_layout);
//        final ArrayList<String> tags = new ArrayList<String>();
//        try {
//            tags.add(GENERAL);
//            JSONArray jsonTags = (JSONArray)category.get(TAG);
//            for (int i=0; i< jsonTags.length(); i++) {
//                final String name = (String) jsonTags.get(i);
//                // Add party name
//                if (name.equals(GENERAL)) {
//                    continue;
//                }
//                tags.add(name);
//            }
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tags);
//            Spinner spinTag = (Spinner)findViewById(R.id.tag);
//            spinTag.setAdapter(adapter);
//
//            spinTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                    currTag = tags.get(position);
//                    createCharts(currTag, currElected);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parentView) {
//                    createCharts(currTag, currElected);
//                }
//
//            });

//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


//    protected void createElectedList() {
//        JSONArray electedJson = getElectedOfficials(R.id.personal_layout);
//        final ArrayList<String> electedList = new ArrayList<String>();
//        if (electedJson != null) {
//            int len = electedJson.length();
//            for (int i=0;i<len;i++){
//                try {
//                    electedList.add(electedJson.get(i).toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, electedList);
//        Spinner spinTag = (Spinner)findViewById(R.id.tag);
//        spinTag.setAdapter(adapter);
//
//        spinTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                currElected = electedList.get(position);
//                createCharts(currTag, currElected);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                createCharts(currTag, currElected);
//            }
//
//        });
//
//
//    }

//    protected void createCharts(String tag, String elected) {
//        JSONObject electedMatchByTag;
//        if (elected != null) {
//            electedMatchByTag = getUserToElectedOfficialMatchByTag(R.id.personal_layout, elected, tag);
//            drawPieChart(electedMatchByTag);
//        }
//        JSONObject partiesMatchByTag = getUserPartiesVotesMatchByTag(R.id.personal_layout, tag);
//        drawPieChart(partiesMatchByTag);
//    }
//
//    protected void drawPieChart(JSONObject data) {
//        //TODO implement
//    }
}
