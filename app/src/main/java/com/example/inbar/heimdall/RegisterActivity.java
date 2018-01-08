package com.example.inbar.heimdall;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RegisterActivity extends APIRequest {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        JSONObject cat=getCategoryNames(R.layout.activity_register);
        //set content to all spinners
        try{
            setSpinnerContent(R.id.party,cat.getJSONArray("parties"),"בחר מפלגה",true);
            setSpinnerContent(R.id.job,cat.getJSONArray("job_categories"),"בחר עיסוק",true);
            setSpinnerContent(R.id.city,cat.getJSONArray("residencies"),"בחר עיר מגורים",true);
            ArrayList<String> invovmentLevelOptions = new ArrayList<>();
            invovmentLevelOptions.add("גבוהה");
            invovmentLevelOptions.add("בינונית");
            invovmentLevelOptions.add("נמוכה");
            setSpinnerContent(R.id.involvment_spinner,invovmentLevelOptions,"בחר רמת מעורבות",false);
            TextView date_spinner=findViewById(R.id.birth_year_selection);
            date_spinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerFragment fragment=new DatePickerFragment();
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    fragment.show(fragmentManager,"pick your birth date");
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    private void setSpinnerContent(int layout_id, JSONArray j_values, String defaultOption,boolean doSort) throws JSONException {
        Spinner s=(Spinner) findViewById(layout_id);
        ArrayList<String> values=new ArrayList<>();
        for (int i=0;i<j_values.length();i++){
            String value = (String) j_values.get(i);
            if (value.isEmpty())continue;
            values.add(value);
        }
        if (doSort)
        Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_item,values);
        s.setPrompt(defaultOption);
        adapter.setDropDownViewResource(R.layout.right_spinner_item);
        s.setAdapter(adapter);

    }

    private void setSpinnerContent(int layout_id, ArrayList<String> values, String defaultOption,boolean doSort) throws JSONException {
        Spinner s=(Spinner) findViewById(layout_id);
        if (doSort)
        Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_item,values);
        s.setPrompt(defaultOption);
        adapter.setDropDownViewResource(R.layout.right_spinner_item);
        s.setAdapter(adapter);

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int day=c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            // Create a new instance of DatePicker and return it
            //new DatePickerDialog(getActivity(), this, year, month, day);
            return new DatePickerDialog(getActivity(),R.style.DialogTheme,this,year,month,day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            TextView dateText=getActivity().findViewById(R.id.birth_year_selection);
            dateText.setText(String.format("%d/%d/%d",datePicker.getDayOfMonth(),datePicker.getMonth(),datePicker.getYear()));
        }
    }
}
