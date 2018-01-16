package com.example.inbar.heimdall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class RegisterActivity extends AbsRegisterActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //publicOnCreate();
        ArrayList<Callable<JSONObject>> callables=new ArrayList<>();
        callables.add(public_get_data);
        ArrayList<CallBack<Void,JSONObject>> callbacks=new ArrayList<>();
        callbacks.add(public_load_callback);
        AsyncCallbackExecutor profileLoadTask = new AsyncCallbackExecutor(this,callables,callbacks,true);
        profileLoadTask.execute();

    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            moveTaskToBack(true); // finish activity
        } else {
            Toast.makeText(this, "לחץ אחורה כדי לצאת...",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

}
