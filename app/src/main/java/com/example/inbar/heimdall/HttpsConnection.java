package com.example.inbar.heimdall;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;


public class HttpsConnection extends NevActivity {

    private final String USER_TOKEN="user_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected HttpURLConnection getConnection(final int id_layer, String subDomain){
        try {
            URL url = new URL("http://api.heimdall.ga"+subDomain);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.connect();
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            onConnectionFailed(id_layer);
            return null;
        }
    }

    protected String sendJson(final int idLayer, JSONObject message, String subDomain) {
        HttpURLConnection connection = null;
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            message.put(USER_TOKEN, 10);
            connection = getConnection(idLayer, subDomain);

            sendToConnection(message, connection);

            return readFromConnection(connection);

        } catch (Exception e) {
            onConnectionFailed(idLayer);
            Log.d("connection problem", e.getMessage());
            return  "";
        } finally {
            if(connection != null)
                connection.disconnect();
        }
    }

    private void sendToConnection(JSONObject message, HttpURLConnection connection) throws IOException {
        StringBuffer postData = new StringBuffer();
        try {
            for (Iterator<String> it = message.keys(); it.hasNext(); ) {
                if (it.hasNext()) {
                    String name = it.next();
                    postData.append(URLEncoder.encode(name, "UTF-8"));
                    postData.append("=");
                    Object value;
                    if(message.get(name) instanceof String)
                        value = URLEncoder.encode(message.getString(name), "UTF-8");
                    else
                        value = message.get(name);
                    postData.append(value);
                    if(it.hasNext())
                        postData.append("&");
                }

            }
        }catch (JSONException e){
            throw new RuntimeException(e);
        }
        DataOutputStream out = new DataOutputStream(connection.getOutputStream ());
        out.writeBytes(postData.toString());
        out.flush ();
        out.close ();
    }

    @NonNull
    private String readFromConnection(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    protected void onConnectionFailed(final int id_layer) {
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
                //getConnection(id_layer);
                finish();
                moveTaskToBack(true);
                return false;
            }
        });

        // dismiss the popup window when touched
        popupView.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        finish();
                        moveTaskToBack(true);
                    }
                }
        );
    }
}
