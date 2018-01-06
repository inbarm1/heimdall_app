package com.example.inbar.heimdall;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
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

import org.json.JSONException;
import org.json.JSONObject;
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
    Map<String, JSONObject> chart1 = new HashMap<>();
    Map<String, JSONObject> chart2 = new HashMap<>();
    Map<String, JSONObject> chart3 = new HashMap<>();

    private CoordinatorLayout mainLayout;
    private PopupWindow mPopupWindow;
    PieChart mChart;
    View customView;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createEfficiencyChar();
        createMissingChar();
        createProposalsChar();
    }

    private void createEfficiencyChar() {
//        JSONObject jsonObj = null;
//        try {
//           jsonObj = new JSONObject(jsonTest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        createBarChart(R.id.chart1, getAllPartiesEfficiencyByTag(R.id.main_layout, null), EFFICIENCY, EFFICIENCY_M, chart1);
    }

    private void createProposalsChar() {
        createBarChart(R.id.chart2, getAllLawProposalsByTag(R.id.main_layout, null), PROPOSALS, PROPOSALS_M, chart2);

    }

    private void createMissingChar() {
        createBarChart(R.id.chart3, getAllAbsentFromVotesByTag(R.id.main_layout, null), MISSING, MISSING_M, chart3);
    }

    private void createBarChart(int char_id, JSONObject parties, String key, String keyMember, final Map<String, JSONObject> map) {

        BarChart barChart = (BarChart) findViewById(char_id);
        String userPartyName = getUserAssociatedParty(R.id.main_layout);
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
                if (e == null)
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

                // Get a reference for the custom view close button
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                creatingChart(map.get(name));
//                Toast.makeText(MainActivity.this,
//                        (name + " = " + map.get(name).toString()), Toast.LENGTH_SHORT).show();
                mPopupWindow.showAtLocation(mainLayout, Gravity.CENTER,0,0);

            }

            @Override
            public void onNothingSelected() {

            }
        });

        BarDataSet dataSet = new BarDataSet(valueSet, "מפלגות");
        dataSet.setColors(colors);

        BarData data = new BarData(xAxis, dataSet);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    protected void creatingChart(final JSONObject data){
//        mChart = (PieChart) findViewById(R.id.piechart);

        mChart = new PieChart(this);
        // add pie chart to main layout
//        customView.(mChart, 1000, 500);
        customView.setBackgroundColor(Color.WHITE);

        // configure pie chart
        mChart.setUsePercentValues(true);
        mChart.setDescription(TITLE_PIE);

        // enable hole and configure
        mChart.setDrawHoleEnabled(true);
//        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        // add data
        addData(data);

        // customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    private void addData(JSONObject dataMem) {
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
