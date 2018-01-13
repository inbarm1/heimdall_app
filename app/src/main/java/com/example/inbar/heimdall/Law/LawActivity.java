package com.example.inbar.heimdall.Law;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.inbar.heimdall.APIRequest;
import com.example.inbar.heimdall.HttpsConnection;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.rgb;

public class LawActivity extends APIRequest {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.law_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new LawListAdapter(getLaws(), this);

        //Get default dates for laws
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date startDate = cal.getTime();
        Date endDate = new Date();

        try {
            mAdapter.getClass().getMethod("getLaws").invoke(startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecyclerView.setAdapter(mAdapter);

        id_drawer_layout = R.id.drawer_layout_law;
        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new Utils.DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public ArrayList<Law> getLaws() {
        ArrayList<Law> laws = new ArrayList<>();//=  ;//Util.getPeopleList(this);
        for (int i = 0; i < 30; i++) {
            Law l1 = null;
            try {
                l1 = createLaw(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            laws.add(l1);
        }
        return laws;
    }

    public Law createLaw(int i) throws JSONException {
        String jsonS = String.format("{'fuck law %s': {'link' : 'www.pornhub.com, 'description' : 'bla bla', 'tags' : ['eilon','ram'], 'user_voted' : 'for'  }", i);
        JSONObject subJson = new JSONObject();
        subJson.put("link", "pornhub.com " + String.valueOf(i));
        subJson.put("description", "bla bla " + String.valueOf(i));
        List<String> tags = new ArrayList<>();
        tags.add("eilon " + String.valueOf(i));
        tags.add("the king " + String.valueOf(i));
        subJson.put("tags", tags);
        subJson.put("user_voted", "for");
        return new Law("law " + String.valueOf(i), subJson, this);
    }



}





