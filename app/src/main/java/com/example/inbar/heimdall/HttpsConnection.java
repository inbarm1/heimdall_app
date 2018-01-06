package com.example.inbar.heimdall;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

public class HttpsConnection extends NevActivity {

    private HttpsURLConnection conn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void connect(final int id_layer){
        try {
            URL url = new URL("https://google.com");
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Host", "android.schoolportal.gr");
            conn.connect();
            onConnectionSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            onConnectionFailed(id_layer);
        }
    }

    protected void sendFirstConnection(final int id_layer) {
        JSONObject message = new JSONObject();
        try {
            message.put("name", "b");
        } catch (JSONException e) {
            onConnectionFailed(id_layer);
        }
        sendJson(id_layer, message);
    }

    protected void sendJson(final int id_layer, JSONObject message) {
        try {
            // Send POST output.
            DataOutputStream printout = new DataOutputStream(conn.getOutputStream ());
            printout.writeBytes(URLEncoder.encode(message.toString(),"UTF-8"));
            printout.flush ();
            printout.close ();
        } catch (Exception e) {
            onConnectionFailed(id_layer);
        }
    }
    private void onConnectionFailed(final int id_layer) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        findViewById(id_layer).post(new Runnable() {
            public void run() {
                popupWindow.showAtLocation(findViewById(id_layer), Gravity.CENTER, 0, 0);
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                connect(id_layer);
                return true;
            }
        });
    }

    protected void onConnectionSuccess(){
        //Do nothing
    }

    @Override
    protected void onStop() {
        super.onStop();
//        conn.disconnect();
    }
}
