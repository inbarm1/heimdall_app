package com.example.inbar.heimdall.Law;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.inbar.heimdall.APIRequest;
import com.example.inbar.heimdall.R;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.Color.rgb;

public class LawActivity extends APIRequest {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LawListAdapter mAdapter;
    public DatePickerDialog fromDatePicker;
    public DatePickerDialog toDatePicker;
    public EditText fromDateText;
    public EditText toDateText;
    public Date fromDate;
    public Date toDate;


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
        fromDate = cal.getTime();
        toDate = new Date();
        mAdapter.getLaws(fromDate, toDate);
        mRecyclerView.setAdapter(mAdapter);

        id_drawer_layout = R.id.drawer_layout_law;
        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fromDateText = (EditText) findViewById(R.id.fromDateText);
        toDateText = (EditText) findViewById(R.id.toDateText);

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                fromDatePicker = new DatePickerDialog(LawActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                fromDateText.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                fromDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                            }
                        }, mYear, mMonth, mDay);
                fromDatePicker.show();
            }
        });

        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                toDatePicker = new DatePickerDialog(LawActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                toDateText.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                toDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();

                            }
                        }, mYear, mMonth, mDay);
                toDatePicker.show();
            }
        });
    }


    public void refreshLawsForAdapter(View v) {
        mAdapter.getLaws(fromDate, toDate);
    }

}





