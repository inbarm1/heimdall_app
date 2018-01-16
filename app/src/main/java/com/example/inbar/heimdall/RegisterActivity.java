package com.example.inbar.heimdall;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;


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
