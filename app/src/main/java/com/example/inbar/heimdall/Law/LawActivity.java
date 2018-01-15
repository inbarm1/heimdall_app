package com.example.inbar.heimdall.Law;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.inbar.heimdall.APIRequest;
import com.example.inbar.heimdall.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.graphics.Color.rgb;

public class LawActivity extends APIRequest {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LawListAdapter mAdapter;
    public JSONArray TAGS;
    public final static  int RECIEVE_TAGS = 2;
    public final static int RATE_HANDLER = 4;


    public TextView fromDateText;
    LawDatePicker fromDatePicker = new LawDatePicker();
    LawDatePicker toDatePicker = new LawDatePicker();

    private PopupWindow mStatisticsPopupWindow;
    View mStatisticscustomView;
    boolean mStatisticsBlocking = false;

    private Handler generalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECIEVE_TAGS:
                    try {
                        setSpinnerContent(R.id.chooseTagSpinner, LawActivity.this.TAGS, null, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case RATE_HANDLER:
                    JSONObject data4 = null;
                    try {
                        data4 = new JSONObject(readFromMessage(msg));
                        String rank = data4.getString("user_rank");
                        ImageView img= (ImageView) findViewById(R.id.rate);
                        img.setImageResource(rate.get(rank));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.law_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadTags();
        getRate();
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
                generalHandler.sendMessage(Message.obtain(generalHandler, RATE_HANDLER, is));
            }
        });
        thread.start();
    }

    private void loadTags() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){

                JSONObject json = LawActivity.this.getCategoryNames(R.id.lawLayout);
                try {
                    LawActivity.this.TAGS = (JSONArray) json.get(TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                generalHandler.sendMessage(Message.obtain(generalHandler, RECIEVE_TAGS));
            }
        });

        thread.start();
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

    public void setSpinnerContent(int layout_id, JSONArray j_values, String defaultOption, boolean doSort) throws JSONException {
        Spinner s = (Spinner) findViewById(layout_id);
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < j_values.length(); i++) {
            String value = (String) j_values.get(i);
            if (value.isEmpty()) continue;
            values.add(value);
        }
        if (doSort)
            Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        s.setPrompt(defaultOption);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        s.setAdapter(adapter);
    }

}





