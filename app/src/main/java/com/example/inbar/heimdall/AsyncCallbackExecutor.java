package com.example.inbar.heimdall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.inbar.heimdall.ProfileActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
/*
* This class will allow you to perform async calls to our api and then call to a callback on
* the jsonObject returned.
* Arguments to constructor:
* 1.the activity the main thread is running
* 2. list of callables each one returnes a JsonObject
* 3. list of callbacks each one receives a JsonObject and returns Void
* 4. showDialog boolean
* The callables will run one after another and a List containing the results will passed to the callback
* that in turn process them on the same order.
* at the end the loading dialog is closed (if shown)
* */
public class AsyncCallbackExecutor extends AsyncTask<Void,Void,List<JSONObject>> {
    ProgressDialog dialog;
    List<Callable<JSONObject>> m_api_callables;
    List<CallBack<Void,JSONObject>> m_callbacks;
    String m_message="טוען ...";
    boolean m_showDialog;

    public void setM_message(String m_message) {
        this.m_message = m_message;
    }


    public AsyncCallbackExecutor(Activity activity, List<Callable<JSONObject>> callable_list, List<CallBack<Void,JSONObject>> callback_list, boolean showLoadDialog){
        dialog=new ProgressDialog(activity);
        m_api_callables=callable_list;
        m_callbacks=callback_list;
        m_showDialog=showLoadDialog;
    }

    @Override
    protected List<JSONObject> doInBackground(Void... voids) {
        ArrayList<JSONObject> objects=new ArrayList<>();
        try {
            for (Callable<JSONObject> c:m_api_callables){
                objects.add(c.call());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (m_showDialog) {
            dialog.setMessage(m_message);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }
    }


    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
        for(int i=0;i<m_callbacks.size();i++){
            m_callbacks.get(i).call(jsonObjects.get(i));
        }
        if(m_showDialog)dialog.dismiss();
    }
}
