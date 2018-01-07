package com.example.inbar.heimdall;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.inbar.heimdall.MainActivity.GENERAL;

/**
 * Created by ramhillel on 07/01/18.
 */

public class PersonalStatisticsActivity extends APIRequest {

    protected CoordinatorLayout personalLayout;
    protected PieChart pChart;
    protected String currTag = null;
    protected String currElected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        personalLayout =  findViewById(R.id.mainLayout);

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
        createTags();
        createElectedList();
        createCharts(currTag, currElected);
    }

    protected void createTags() {
        JSONObject category = getCategoryNames(R.id.main_layout);
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tags);
            Spinner spinTag = (Spinner)findViewById(R.id.tag);
            spinTag.setAdapter(adapter);

            spinTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    currTag = tags.get(position);
                    createCharts(currTag, currElected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    createCharts(currTag, currElected);
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void createElectedList() {
        JSONArray electedJson = getElectedOfficials(R.id.main_layout);
        final ArrayList<String> electedList = new ArrayList<String>();
        if (electedJson != null) {
            int len = electedJson.length();
            for (int i=0;i<len;i++){
                try {
                    electedList.add(electedJson.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, electedList);
        Spinner spinTag = (Spinner)findViewById(R.id.tag);
        spinTag.setAdapter(adapter);

        spinTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currElected = electedList.get(position);
                createCharts(currTag, currElected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                createCharts(currTag, currElected);
            }

        });


    }

    protected void createCharts(String tag, String elected) {
        JSONObject electedMatchByTag;
        if (elected != null) {
            electedMatchByTag = getUserToElectedOfficialMatchByTag(R.id.main_layout, elected, tag);
            drawPieChart(electedMatchByTag);
        }
        JSONObject partiesMatchByTag = getUserPartiesVotesMatchByTag(R.id.main_layout, tag);
        drawPieChart(partiesMatchByTag);
    }

    protected void drawPieChart(JSONObject data) {
        //TODO implement
    }
}
