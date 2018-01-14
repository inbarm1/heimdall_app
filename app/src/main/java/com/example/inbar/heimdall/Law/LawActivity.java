package com.example.inbar.heimdall.Law;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.inbar.heimdall.APIRequest;
import com.example.inbar.heimdall.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;

import static android.graphics.Color.rgb;

public class LawActivity extends APIRequest {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LawListAdapter mAdapter;
    public JSONArray TAGS;


    public TextView fromDateText;
    LawDatePicker fromDatePicker = new LawDatePicker();
    LawDatePicker toDatePicker = new LawDatePicker();

    private PopupWindow mStatisticsPopupWindow;
    View mStatisticscustomView;
    boolean mStatisticsBlocking = false;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.law_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        number_of_notification = 0;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new LawListAdapter(new ArrayList<Law>(), this);

        //Get default dates for laws
        Calendar currDate = Calendar.getInstance();
        Calendar lastMonthDate = Calendar.getInstance();
        lastMonthDate.add(Calendar.MONTH, -1);
        mAdapter.getLaws(lastMonthDate,  currDate);
        mRecyclerView.setAdapter(mAdapter);
        setDatePickerView();
        id_drawer_layout = R.id.drawer_layout_law;
        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView img = (ImageView) findViewById(R.id.counterBackground);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LawActivity.this,LawActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadTags() {

    }


    private void setDatePickerView() {
        TextView fromTextView = findViewById(R.id.fromDateText);
        fromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fromDatePicker.show(fragmentManager, "Choose Start Date");
            }
        });

        toDatePicker.setToDate();
        TextView toTextView = findViewById(R.id.toDateText);
        toTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                toDatePicker.show(fragmentManager, "Choose Start Date");
            }
        });
    }

    public void refreshLawsForAdapter(View v) {
        mAdapter.getLaws(fromDatePicker.c, toDatePicker.c);
    }

}





