package com.example.inbar.heimdall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.inbar.heimdall.Law.LawActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static android.graphics.Color.rgb;
public class PersonalStatisticsActivity extends APIRequest {

    protected CoordinatorLayout personalLayout;
    protected PieChart pChart;
    protected String currTag = null;
    protected String currElected = null;
    private ArrayList<String> tags = new ArrayList<String>();
    private ArrayList<String> tags2 = new ArrayList<String>();
    private static int currentTagPosition = 0;
    private static int currentElectedPosition = 0;
    private final static int CHART_1 = 0;
    private final static int CHART_2 = 1;
    private final static int CHART_3 = 2;
    private final static int TAGS_HANDLER = 3;
    private final static int TAGS_HANDLER2 = 5;
    private final static int RATE_HANDLER = 4;
    private static String userPartyName;
    private static final String EFFICIENCY   = "party_efficiency";
    private static final String MISSING      = "party_missing";
    private static final String EFFICIENCY_M = "memeber_efficiency";
    private static final String PROPOSALS_M  = "elected_proposals";
    private static final String MISSING_M    = "member_missing";
    private static final String EFFICIENCY_T = "יעילות המפלגות";
    private static final String PROPOSALS_T  = "הצעות חוק";
    private static final String MISSING_T    = "העדרויות";
    private static final String TITLE_PIE    = "התפלגות";
    private static final String GENERAL      = "כללי";
    private static final String NONE_TAG     = "בחר חבר כנסת";
    private static final String ABSENT_PRE   = "נמנע";
    private static final String DIFF_PRE     = "הצבעות שונות";
    private static final String SAME_PRE     = "הצבעות זהות";
    private static final String REMOVE_PARTY = "ח”כ יחיד – אורלי לוי-אבקסיס";
    private static final String REMOVE_PARTY2 = "אינם חברי כנסת";


    private Handler handler_ = new Handler(){
        @Override
        public void handleMessage(Message msg){
            try {
                switch (msg.what) {
                    case CHART_1:
                        JSONObject data1 = new JSONObject(readFromMessage(msg));
                        createBarChart(R.id.chart1p, data1,userPartyName);
                        break;
                    case CHART_2:
                        JSONObject data2 = new JSONObject(readFromMessage(msg));
                        creatingPieChart(R.id.chart2p, data2);
                        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll_personal);
                        int y = (scrollView.getChildAt(0).getMeasuredHeight() -scrollView.getMeasuredHeight());
                        scrollView.setScrollY(y);
                        break;
                    case TAGS_HANDLER2:
                        JSONArray category2 = new JSONArray(readFromMessage(msg));
                        analyzeTags2(category2);
                        break;
                    case TAGS_HANDLER:
                        JSONObject category = new JSONObject(readFromMessage(msg));
                        analyzeTags(category);
                        break;
                    case RATE_HANDLER:
                        JSONObject data4 = new JSONObject(readFromMessage(msg));
                        String rank = data4.getString("user_rank");
                        ImageView img = (ImageView) findViewById(R.id.rate);
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

        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll_personal);

        final FloatingActionButton image = (FloatingActionButton) findViewById(R.id.img_arrow2);
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scrollView.scrollBy(0, 1500);
            }
        });

        final FloatingActionButton image_up = (FloatingActionButton) findViewById(R.id.img_arrow_up2);
        image_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scrollView.scrollBy(0, -1500);
            }
        });


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

        ImageView img = (ImageView) findViewById(R.id.counterBackground);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PersonalStatisticsActivity.this,LawActivity.class);
                startActivity(intent);
            }
        });
        createTags();
        getRate();
    }

    private void createMatchChart(final String tag) {
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

    private void createLawPieChart(final String elected, final String tagElected) {
        if (elected == null || elected.equals(NONE_TAG)){
            TextView text = (TextView)findViewById(R.id.no_data);
            text.setVisibility(View.VISIBLE);
            return;
        }
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONObject json = getUserToElectedOfficialMatchByTag(R.id.personal_layout, elected, tagElected);
                if (json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, CHART_2, is));
            }
        });
        thread.start();
    }

    private void createBarChart(int char_id, JSONObject parties, String userPartyName) {

        BarChart barChart = (BarChart) findViewById(char_id);
        final ArrayList<BarEntry> valueSet = new ArrayList<>();
        final ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        try {
            Iterator<?> partyName = parties.keys();
            int counter = 0;
            while( partyName.hasNext() ) {
                final String name = (String)partyName.next();
                if (name.equals(REMOVE_PARTY) || name.equals(REMOVE_PARTY2)) {
                    continue;
                }
                // Add party name
                xAxis.add(name);
                // Get party's data
                if ( parties.get(name) instanceof JSONObject ) {
                    JSONObject data = (JSONObject)parties.get(name);
                    float val = Math.round((((Number)data.get(MATCH)).floatValue() * 100 * 100.0) / 100.0);
                    BarEntry entry = new BarEntry(val, counter);
                    if (name.equals(userPartyName)) {
                        colors.add(rgb(70,130,180));
                    } else {
                        colors.add(rgb(135,206,235));
                    }
                    // Get value of party
                    valueSet.add(entry);
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

    private void analyzeTags(JSONObject category) {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JSONArray json = getElectedOfficials(R.id.personal_layout);
                if (json == null) {
                    return;
                }
                String str = json.toString();
                InputStream is = new ByteArrayInputStream(str.getBytes());
                handler_.sendMessage(Message.obtain(handler_, TAGS_HANDLER2, is));
            }
        });
        thread.start();

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

        createFirstView();
    }

    private void analyzeTags2(JSONArray category) {
        try {
            setAutoCompleteContent(category, NONE_TAG, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createSecondView();
    }

    private void setAutoCompleteContent(JSONArray j_values, String defaultOption, boolean doSort) throws JSONException {
        AutoCompleteTextView a = (AutoCompleteTextView) findViewById(R.id.tagp2);

        for (int i=0; i< j_values.length(); i++) {
            final String name = (String) j_values.get(i);
            // Add party name
            tags2.add(name);
        }

        if (doSort)
            Collections.sort(tags2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PersonalStatisticsActivity.this, android.R.layout.simple_list_item_1, tags2);
//        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        a.setContentDescription(NONE_TAG);
        a.setAdapter(adapter);
        a.setThreshold(1);
        a.setTextColor(Color.BLACK);
        ;
    }

    private void createFirstView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tags);
        Spinner spinTag = (Spinner)findViewById(R.id.tagp);
        spinTag.setAdapter(adapter);

        spinTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                createMatchChart(tags.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                createMatchChart(null);
            }

        });
    }

    private void createSecondView() {
        final AutoCompleteTextView spinTag = (AutoCompleteTextView) findViewById(R.id.tagp2);

        spinTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {

                TextView test = (TextView)findViewById(R.id.no_data);
                test.setVisibility(View.GONE);
                currentElectedPosition = pos;
                createLawPieChart(tags2.get(pos), tags.get(currentTagPosition));

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tags);
        Spinner spinTag2 = (Spinner)findViewById(R.id.tagp3);
        spinTag2.setAdapter(adapter2);


        spinTag2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentElectedPosition = position;
                createLawPieChart(tags2.get(currentElectedPosition), tags.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                createLawPieChart(null, null);
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

    protected void creatingPieChart(int char_id, JSONObject data){
        float diff = 0;
        float absent = 0;
        float same = 0;
        try {
            diff = ((Number)data.get(PRECENT_DIFFERENT)).floatValue() * 100;
            absent = ((Number)data.get(PRECENT_ABSENT)).floatValue() * 100;
            same = ((Number)data.get(PRECENT_SAME)).floatValue() * 100;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (diff == 0 && absent == 0 && same == 0) {
            TextView test = (TextView)findViewById(R.id.no_data);
            test.setVisibility(View.VISIBLE);
            return;
        }
        View view = (View)findViewById(R.id.personal_layout);
        PieChart mChart = (PieChart)view.findViewById(char_id);

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
        addData(mChart, data, diff, absent, same);

        mChart.setVisibility(View.VISIBLE);

        // customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    private void addData(PieChart mChart, JSONObject dataMem,  float diff,  float absent, float same) {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xData = new ArrayList<>();

        Iterator<?> memName = dataMem.keys();
        xData.add(DIFF_PRE);
        yVals1.add(new Entry(diff, 0));
        xData.add(ABSENT_PRE);
        yVals1.add(new Entry(absent, 1));
        xData.add(SAME_PRE);
        yVals1.add(new Entry(same, 2));


        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "התאמה לחבר כנסת");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(rgb(197,204,232));
        colors.add(rgb(135,206,235));
        colors.add(rgb(153,204,255));

        dataSet.setColors(colors);

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
}
