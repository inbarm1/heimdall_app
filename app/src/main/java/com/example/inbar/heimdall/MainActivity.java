package com.example.inbar.heimdall;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
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

import com.example.inbar.heimdall.Law.LawActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.graphics.Color.rgb;

public class MainActivity extends APIRequest {
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
    private static final String REMOVE_PARTY = "ח”כ יחיד – אורלי לוי-אבקסיס";
    private static final String REMOVE_PARTY2 = "אינם חברי כנסת";
    public static final String GENERAL      = "כללי";
    Map<String, JSONObject> chart1 = new HashMap<>();
    Map<String, JSONObject> chart2 = new HashMap<>();
    Map<String, JSONObject> chart3 = new HashMap<>();

    private CoordinatorLayout mainLayout;
    private PopupWindow mPopupWindow;
    View customView;
    boolean blocking = false;

    private final static int CHART_1 = 0;
    private final static int CHART_2 = 1;
    private final static int CHART_3 = 2;
    private final static int TAGS_HANDLER = 3;
    private final static int RATE_HANDLER = 4;
    private static String userPartyName;


    private Handler handler_ = new Handler(){
        @Override
        public void handleMessage(Message msg){
            try {
                switch (msg.what) {
                    case CHART_1:
                        JSONObject data1 = new JSONObject(readFromMessage(msg));
                        createBarChart(R.id.chart1, data1,userPartyName,  EFFICIENCY, EFFICIENCY_M, chart1);
                        break;
                    case CHART_2:
                        JSONObject data2 = new JSONObject(readFromMessage(msg));
                        createBarChart(R.id.chart2, data2,userPartyName,  PROPOSALS, PROPOSALS_M, chart2);
                        break;
                    case CHART_3:
                        JSONObject data3 = new JSONObject(readFromMessage(msg));
                        createBarChart(R.id.chart3, data3,userPartyName, MISSING, MISSING_M, chart3);
                        break;
                    case TAGS_HANDLER:
                        JSONObject category = new JSONObject(readFromMessage(msg));
                        analyzeTags(category);
                        break;
                    case RATE_HANDLER:
                        JSONObject data4 = new JSONObject(readFromMessage(msg));
                        String rank = data4.getString("user_rank");
                        ImageView img= (ImageView) findViewById(R.id.rate);
                        img.setImageResource(rate.get(rank));

                }
            }catch(IOException e){
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


    };

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainLayout =  findViewById(R.id.mainLayout);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scrollLayer);

        final FloatingActionButton image = (FloatingActionButton) findViewById(R.id.img_arrow);
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scrollView.scrollBy(0, 1500);
            }
        });

        final FloatingActionButton image_up = (FloatingActionButton) findViewById(R.id.img_arrow_up);
        image_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scrollView.scrollBy(0, -1500);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    image.setVisibility(View.GONE);
                } else if (scrollY == 0) {
                    image_up.setVisibility(View.GONE);
                }
                else {
                    image_up.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String str = getUserAssociatedParty(R.id.mainLayout);
                userPartyName = str;
            }
        });
        ImageView img = (ImageView) findViewById(R.id.counterBackground);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LawActivity.class);
                startActivity(intent);
            }
        });
        thread.start();
        createTags();
    }

    private void createCharts(String tag){
        getRate();
        createEfficiencyChar(tag);
        createMissingChar(tag);
        createProposalsChar(tag);
    }

    private void getRate() {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONObject json = getUserRank(R.id.mainLayout);
                if(json == null) {
                    return;
                }
                String str = json.toString();
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
                JSONObject json = getCategoryNames(R.id.mainLayout);
                if(json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, TAGS_HANDLER, is));
            }
        });
        thread.start();
    }

    private void createFirstView(final ArrayList<String> tags) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tags);
        Spinner spinTag = (Spinner)findViewById(R.id.tagp);
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

    private void createEfficiencyChar(final String tag) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONObject json = getAllPartiesEfficiencyByTag(R.id.mainLayout, tag);
                if(json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, CHART_1, is));
            }
        });
        thread.start();
    }

    private void createProposalsChar(final String tag) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONObject json = getAllLawProposalsByTag(R.id.mainLayout, tag);
                if(json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, CHART_2, is));
            }
        });
        thread.start();
    }



    private void createMissingChar(final String tag) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONObject json = getAllAbsentFromVotesByTag(R.id.mainLayout, tag);
                if(json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, CHART_3, is));
            }
        });
        thread.start();


    }

    private void createBarChart(int char_id, JSONObject parties, String userPartyName, String key, String keyMember, final Map<String, JSONObject> map) {

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
                if (name.equals(REMOVE_PARTY) || name.equals(REMOVE_PARTY2)) {
                    continue;
                }
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
                        map.put(name,dataForShow);
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
                if (e == null || blocking)
                    return;


                // Get the application context
                Context mContext = getApplicationContext();
                String name = xAxis.get(e.getXIndex());
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                customView = inflater.inflate(R.layout.activity_pop_piechart,null);

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

                mPopupWindow.showAtLocation(mainLayout, Gravity.CENTER,0,0);

                creatingChart(map.get(name), customView);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        BarDataSet dataSet = new BarDataSet(valueSet, "מפלגות");
        dataSet.setColors(colors);
        BarData data = new BarData(xAxis, dataSet);
        barChart.setData(data);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraTopOffset(40);

        XAxis xAxistemp = barChart.getXAxis();
        xAxistemp.setSpaceBetweenLabels(0);
        xAxistemp.setLabelsToSkip(0);
        xAxistemp.setTextSize(8);
        xAxistemp.setLabelRotationAngle(-45);

        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    protected void creatingChart(final JSONObject data, View pop){
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
