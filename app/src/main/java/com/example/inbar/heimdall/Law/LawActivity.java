package com.example.inbar.heimdall.Law;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.inbar.heimdall.APIRequest;
import com.example.inbar.heimdall.HttpsConnection;
import com.example.inbar.heimdall.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.rgb;

public class LawActivity extends APIRequest {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LawListAdapter mAdapter;
    private Date startDate;
    private Date endDate;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private boolean isStartDate;
        LawActivity lawActivity;


        public void setDateToChange(LawActivity lawActivity, boolean isStartDate) {
            this.isStartDate = isStartDate;
            this.lawActivity = lawActivity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Date date = new GregorianCalendar(year, month, day).getTime();

            if (isStartDate) {
                lawActivity.startDate = date;
            } else lawActivity.endDate = date;
        }
    }

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
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        startDate = cal.getTime();
        endDate = new Date();
        mAdapter.getLaws(startDate, endDate);
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

//    public void showDatePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getFragmentManager(), "datePicker");
//    }

    public void setStartDate(View v){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateToChange(this, true);
        newFragment.show(getFragmentManager(), "Start Date");
    }

    public void setEndDate(View v){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateToChange(this, false);
        newFragment.show(getFragmentManager(), "End Date");
    }

    public void refreshLawsForAdapter(View v) {
        mAdapter.getLaws(startDate, endDate);
    }

//    public static Law createLaw(int i) throws JSONException {
//        String jsonS = String.format("{'fuck law %s': {'link' : 'www.pornhub.com, 'description' : 'bla bla', 'tags' : ['eilon','ram'], 'user_voted' : 'for'  }",i);
//        JSONObject subJson = new JSONObject();
//        subJson.put("link","pornhub.com " + String.valueOf(i));
//        subJson.put("description","bla bla "+ String.valueOf(i));
//        List<String> tags = new ArrayList<>();
//        tags.add("eilon "+ String.valueOf(i));
//        tags.add("the king "+ String.valueOf(i));
//        subJson.put("tags",tags);
//        subJson.put("user_voted","for");
//        return new Law("law " + String.valueOf(i),subJson,  );
//    }
    public Law[] getLaws() {
        Law[] laws = new Law[30];//=  ;//Util.getPeopleList(this);
        for (int i = 0; i < 30; i++) {
            Law l1 = null;
            try {
                l1 = createLaw(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            laws[i] = (l1);
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





