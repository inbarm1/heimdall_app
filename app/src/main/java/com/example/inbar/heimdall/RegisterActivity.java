package com.example.inbar.heimdall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class RegisterActivity extends APIRequest {
    String job,party,residency;
    InvolvementLevel involvementLevel;
    com.example.inbar.heimdall.DatePicker dateFragment=new com.example.inbar.heimdall.DatePicker();

    ExecutorService es= Executors.newSingleThreadExecutor();
    public Future<JSONObject> getCategories(){
        return es.submit(new Callable<JSONObject>(){

            @Override
            public JSONObject call() throws Exception {
                return getCategoryNames(R.layout.activity_register);
            }
        });
    }
    public Future<Boolean> register(){
        return es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                return register(R.layout.activity_register,dateFragment.getYear(),job,residency,party,involvementLevel);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        JSONObject cat=null;
        Future<JSONObject> categories = getCategories();
        try{
            cat=categories.get();
        }
        catch (Exception  e){
            e.printStackTrace();
        }
        //set content to all spinners
        try{
            setSpinnerContent(R.id.party,cat.getJSONArray("parties"),"בחר מפלגה",true);
            setSpinnerContent(R.id.job,cat.getJSONArray("job_categories"),"בחר עיסוק",true);
            setAutoCompleteContent(R.id.city,cat.getJSONArray("residencies"),"בחר עיר מגורים",true);
            setInvolvementLevelView();
            setDatePickerView();
            setRegisterBtnView();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void setRegisterBtnView() {
        Button regBtn=(Button)findViewById(R.id.register_btn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateRegisterForm()){
                    return;
                };
                Future<Boolean> register = register();
                try {
                    if (register.get()) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    }
                    else{
                        Log.d("error","register failed");
                        onConnectionFailed(R.layout.activity_register);
                    }
                } catch (Exception e) {
                    onConnectionFailed(R.layout.activity_register);
                }
            }
        });
    }

    private void setDatePickerView() {
        LinearLayout dateLayout=findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                dateFragment.show(fragmentManager,"pick your birth date");
            }
        });
    }

    private void setInvolvementLevelView() throws JSONException {
        ArrayList<String> invovmentLevelOptions = new ArrayList<>();
        invovmentLevelOptions.add("גבוהה");
        invovmentLevelOptions.add("בינונית");
        invovmentLevelOptions.add("נמוכה");
        setSpinnerContent(R.id.involvment_spinner,invovmentLevelOptions,"בחר רמת מעורבות",false);
    }


    private void setSpinnerContent(int layout_id, JSONArray j_values, String defaultOption,boolean doSort) throws JSONException {
        Spinner s=(Spinner) findViewById(layout_id);
        ArrayList<String> values=new ArrayList<>();
        for (int i=0;i<j_values.length();i++){
            String value = (String) j_values.get(i);
            if (value.isEmpty())continue;
            values.add(value);
        }
        setSpinnerContent(layout_id,values,defaultOption,doSort);

    }
    private void setAutoCompleteContent(int layout_id, JSONArray j_values, String defaultOption,boolean doSort) throws JSONException {
        AutoCompleteTextView a=(AutoCompleteTextView) findViewById(layout_id);
        ArrayList<String> values=new ArrayList<>();
        for (int i=0;i<j_values.length();i++){
            String value = (String) j_values.get(i);
            if (value.isEmpty())continue;
            values.add(value);
        }
        if (doSort)
            Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_list_item_1,values);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        a.setAdapter(adapter);
        a.setThreshold(1);
        a.setTextColor(Color.BLACK);;
    }
    private void setSpinnerContent(int layout_id, ArrayList<String> values, String defaultOption,boolean doSort) throws JSONException {
        Spinner s=(Spinner) findViewById(layout_id);
        if (doSort)
        Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_list_item_1,values);
        s.setPrompt(defaultOption);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        s.setAdapter(adapter);

    }

    private boolean validateDate(){
        TextView dateTV=(TextView)findViewById(R.id.birth_date_selection);
        String dateString=(String)dateTV.getText();
        java.text.DateFormat format = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
        try {
            Date date = format.parse(dateString);
        }catch (Exception e){
            Log.d("error",dateString);
            return false;
        }
        return true;
    }
    private boolean validatejob(){
        Spinner jobSpn=(Spinner)findViewById(R.id.job);
        job=jobSpn.getSelectedItem().toString();
        return true;
    }
    private boolean validateResidency(){
        AutoCompleteTextView residencyAc=(AutoCompleteTextView) findViewById(R.id.city);
        residency=residencyAc.getText().toString();
        return true;
    }
    private boolean validateInvolvement(){
        Spinner invSpn=(Spinner) findViewById(R.id.involvment_spinner);
        String involvementStr=invSpn.getSelectedItem().toString();
        if (involvementStr.equals("גבוהה"))involvementLevel=InvolvementLevel.HIGH;
        else if (involvementStr.equals("בינונית"))involvementLevel=InvolvementLevel.MEDIUM;
        else involvementLevel=InvolvementLevel.LOW;
        return true;
    }
    private boolean validateParty(){
        Spinner partySpn=(Spinner) findViewById(R.id.party);
        party=partySpn.getSelectedItem().toString();
        return true;
    }
    private boolean validateRegisterForm(){
        validateParty();
        validatejob();
        validateDate();
        validateResidency();
        validateInvolvement();
        return true;
    }


}
