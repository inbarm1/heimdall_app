package com.example.inbar.heimdall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.inbar.heimdall.Law.LawActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public abstract class AbsRegisterActivity extends APIRequest {
    String job, party, residency;
    JSONObject cat=null;
    HashSet<String> residencySet = new HashSet<>();
    InvolvementLevel involvementLevel;
    DatePicker dateFragment = new DatePicker();

    ExecutorService es = Executors.newSingleThreadExecutor();

    public Future<Boolean> register() {
        return es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return register(R.layout.activity_register, dateFragment.getYear(), job, residency, party, involvementLevel);
            }
        });
    }

    protected void setRegisterBtnView() {
        Button regBtn = (Button) findViewById(R.id.register_btn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateRegisterForm()){
                    return;
                };
                Future<Boolean> register = register();
                while(!register.isDone()){}
                Intent intent = null;
                try {
                    if (register.get()) {
                        intent = new Intent(AbsRegisterActivity.this, LawActivity.class);
                    }
                    else{
                        Log.d("error","register failed");
                        intent = new Intent(AbsRegisterActivity.this, SignInActivity.class);
                        onConnectionFailed(R.layout.activity_register);
                    }
                } catch (Exception e) {
                    intent = new Intent(AbsRegisterActivity.this, SignInActivity.class);
                    onConnectionFailed(R.layout.activity_register);
                }finally {
                    startActivity(intent);
                }
            }
        });
    }

    protected void createResidencySet(JSONArray residencyArray) throws JSONException {
        for (int i = 0; i < residencyArray.length(); i++) {
            residencySet.add((String) residencyArray.get(i));
        }
    }

    protected void setDatePickerView() {
        LinearLayout dateLayout = findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                dateFragment.show(fragmentManager, "pick your birth date");
            }
        });
    }

    protected void setInvolvementLevelView() throws JSONException {
        ArrayList<String> invovmentLevelOptions = new ArrayList<>();
        invovmentLevelOptions.add("גבוהה");
        invovmentLevelOptions.add("בינונית");
        invovmentLevelOptions.add("נמוכה");
        setSpinnerContent(R.id.involvment_spinner, invovmentLevelOptions, "בחר רמת מעורבות", false);
    }


    protected void setSpinnerContent(int layout_id, JSONArray j_values, String defaultOption, boolean doSort) throws JSONException {
        Spinner s = (Spinner) findViewById(layout_id);
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < j_values.length(); i++) {
            String value = (String) j_values.get(i);
            if (value.isEmpty()) continue;
            values.add(value);
        }
        setSpinnerContent(layout_id, values, defaultOption, doSort);

    }

    protected void setAutoCompleteContent(int layout_id, JSONArray j_values, String defaultOption, boolean doSort) throws JSONException {
        final AutoCompleteTextView a = (AutoCompleteTextView) findViewById(layout_id);
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < j_values.length(); i++) {
            String value = (String) j_values.get(i);
            if (value.isEmpty()) continue;
            values.add(value);
        }
        if (doSort)
            Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AbsRegisterActivity.this, android.R.layout.simple_list_item_1, values);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        a.setThreshold(1);
        a.setAdapter(adapter);
        a.setCursorVisible(false);
        a.setTextColor(Color.BLACK);
        a.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                a.dismissDropDown();
                InputMethodManager inputManager = (InputMethodManager) AbsRegisterActivity.this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                View v = AbsRegisterActivity.this.getCurrentFocus();

                if (v != null) {

                    AbsRegisterActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });

    }

    protected void setSpinnerContent(int layout_id, ArrayList<String> values, String defaultOption, boolean doSort) throws JSONException {
        Spinner s = (Spinner) findViewById(layout_id);
        if (doSort)
            Collections.sort(values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AbsRegisterActivity.this, android.R.layout.simple_list_item_1, values);
        s.setPrompt(defaultOption);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        s.setAdapter(adapter);

    }

    private boolean validateDate() {
        TextView dateTV = (TextView) findViewById(R.id.birth_date_selection);
        String dateString = (String) dateTV.getText();
        java.text.DateFormat format = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
        try {
            Date date = format.parse(dateString);
        } catch (Exception e) {
            Log.d("error", dateString);
            showBar("בחר תאריך לידה",Snackbar.LENGTH_LONG);
            return false;
        }
        return true;
    }

    public boolean validatejob() {
        Spinner jobSpn = (Spinner) findViewById(R.id.job);
        job = jobSpn.getSelectedItem().toString();
        return true;
    }

    public boolean validateResidency() {
        AutoCompleteTextView residencyAc = (AutoCompleteTextView) findViewById(R.id.city);
        residency = residencyAc.getText().toString();
        if (!residencySet.contains(residency)) {
            showBar("בחר עיר מגורים מבין הערים ברשימה",Snackbar.LENGTH_LONG);
            return false;
        }
        return true;
    }

    public boolean validateInvolvement() {
        Spinner invSpn = (Spinner) findViewById(R.id.involvment_spinner);
        String involvementStr = invSpn.getSelectedItem().toString();
        switch (involvementStr) {
            case "גבוהה":
                involvementLevel = InvolvementLevel.HIGH;
                break;
            case "בינונית":
                involvementLevel = InvolvementLevel.MEDIUM;
                break;
            default:
                involvementLevel = InvolvementLevel.LOW;
                break;
        }
        return true;
    }
    public InvolvementLevel engInvolvementToInt(String involvementStr) {
        switch (involvementStr) {
            case "HIGH":
                return InvolvementLevel.HIGH;
            case "MEDIUM":
                return InvolvementLevel.MEDIUM;
            default:
                return InvolvementLevel.LOW;
        }
    }

    public String getHebInvolvement(InvolvementLevel hebInvolvement){
        switch (hebInvolvement){
            case HIGH:
                return "גבוהה";
            case MEDIUM:
                return "בינונית";
            default:
                return "נמוכה";
        }
    }
    public boolean validateParty() {
        Spinner partySpn = (Spinner) findViewById(R.id.party);
        party = partySpn.getSelectedItem().toString();
        return true;
    }

    private boolean validateRegisterForm() {
        return validateParty() && validatejob() && validateDate() && validateResidency() && validateInvolvement();
    }

    public void showBar(String message,int length){
        Snackbar.make(findViewById(R.id.bottomLayout), message,
                length)
                .show();
    }

    CallBack<Void,JSONObject> public_load_callback=new CallBack<Void, JSONObject>() {
        @Override
        public Void call(JSONObject val) {
            try {
                createResidencySet(cat.getJSONArray("residencies"));
                setSpinnerContent(R.id.party, cat.getJSONArray("parties"), "בחר מפלגה", true);
                setSpinnerContent(R.id.job, cat.getJSONArray("job_categories"), "בחר עיסוק", true);
                setAutoCompleteContent(R.id.city, cat.getJSONArray("residencies"), "בחר עיר מגורים", true);
                setInvolvementLevelView();
                setDatePickerView();
                setRegisterBtnView();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };
    Callable public_get_data=new Callable<JSONObject>() {
        @Override
        public JSONObject call() throws Exception {
            cat=getCategoryNames(R.layout.activity_profile);
            return null;
        }
    };
}
