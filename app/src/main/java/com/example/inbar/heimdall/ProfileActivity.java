package com.example.inbar.heimdall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;


public class ProfileActivity extends AbsRegisterActivity {
    ExecutorService es = Executors.newSingleThreadExecutor();
    private Future<JSONObject> getUserDetails(){
        return es.submit(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                Thread.sleep(1000*10);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("bla",3);
                return jsonObject;
            }
        });
    }

    Callable profile_get_data=new Callable<JSONObject>() {
        @Override
        public JSONObject call() throws Exception {
            return getUserInfo(R.layout.profile_main);
        }
    };

    CallBack<Void,JSONObject> profile_callback=new CallBack<Void, JSONObject>() {
        @Override
        public Void call(JSONObject val) {
            try {
                job = val.getString(JOB_CATEGORY);
                residency = val.getString(RESIDENCY);
                party = val.getString(PARTY);
                involvementLevel = engInvolvementToInt(val.getString(INVOLVEMENT_LEVEL));
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            setSpinnerSelection(job,R.id.job);
            setSpinnerSelection(party,R.id.party);
            setSpinnerSelection(getHebInvolvement(involvementLevel),R.id.involvment_spinner);
            setResidency();
            setBtnLogics();
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        /*set get data and callbacks then execute task*/
        ArrayList<CallBack<Void,JSONObject>> callbacks=new ArrayList<>();
        ArrayList<Callable<JSONObject>> callables=new ArrayList<>();
        callbacks.add(public_load_callback);callbacks.add(profile_callback);
        callables.add(public_get_data);callables.add(profile_get_data);
        AsyncCallbackExecutor profileLoadTask = new AsyncCallbackExecutor(this,callables,callbacks,true);
        profileLoadTask.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);

        id_drawer_layout = R.id.drawer_layout_profile;
        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        hideByID(R.id.date_layout);
        hideByID(R.id.birth_date_text);
        hideByID(R.id.date_divider);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setText("עדכן את הפרטים על מנת לקבל את המידע המתאים ביותר עבורך",R.id.register_content_text);
        setText("עדכון פרטים",R.id.register_large_title);


    }



    private void setText(String text, int id){
        TextView tView=findViewById(id);
        tView.setText(text);
    }
    private void hideByID(int id){
        View v=findViewById(id);
        v.setVisibility(View.GONE);
    }

    private void setResidency(){
        AutoCompleteTextView v=findViewById(R.id.city);
        v.setText(residency);
        v.dismissDropDown();
    }
    private void setSpinnerSelection(String value,int spinnerID){
        Spinner spinner=findViewById(spinnerID);
        ArrayList<String> values=new ArrayList<>();
        int valueCnt=spinner.getAdapter().getCount();
        int newSelectionIndex=-1;
        for(int i=0;i<valueCnt;i++){
            if (spinner.getAdapter().getItem(i).equals(value))
                newSelectionIndex=i;
        }
        if(newSelectionIndex >= 0)
        spinner.setSelection(newSelectionIndex);
    }

    private Future<Boolean> updateUserDetails(){
        return es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return updatePersonalInfo(R.layout.profile_main,job,residency,party,involvementLevel);
            }
        });
    }
    private boolean validateUpdate(){
        return validateInvolvement()&&validatejob()&&validateParty()&&validateResidency();
    }
    private void setBtnLogics(){
        Button btn=findViewById(R.id.register_btn);
        btn.setText("עדכן");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateUpdate())return;
                Future<Boolean> booleanFuture = updateUserDetails();
                try {
                    if(booleanFuture.get()){
                        showBar("הפרטים עודכנו בהצלחה",Snackbar.LENGTH_LONG);
                    }
                    else{
                        showBar("עדכון הפרטים נכשל",Snackbar.LENGTH_LONG);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
