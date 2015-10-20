package com.vermeersch.mycave.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.vermeersch.mycave.Constants;
import com.vermeersch.mycave.model.LightingStates;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import android.support.v4.content.LocalBroadcastManager;




/**
 * Created by Gert on 18/09/2015.
 */
public class AutomationConnector {
    private String username;
    private String password;

    private Context context;
    private Runnable updaterRunnable;
    private Handler updaterHandler;
    private static AutomationConnector automationConnector;


    public static AutomationConnector getInstance() {
        if(automationConnector == null) {
            automationConnector = new AutomationConnector();
        }
        return automationConnector;
    }

    private AutomationConnector() {


    }


    public AutomationConnector(String username, String password, Context context) {
        this.username = username;
        this.password = password;
        this.context = context;
        updaterHandler = new Handler();
        automationConnector = this;
    }

    public void sendPost(String url, JSONObject payload) {

    }

    public void setOutlet(String path, boolean value) {
        StatusPoster poster = new StatusPoster();
        poster.execute(path, value?"true":"false");
    }

    public void startUpdate() {

        updaterRunnable = new Runnable() {
            @Override
            public void run() {
                StatusGetter statusGetter = new StatusGetter();
                statusGetter.execute("outlets/all");
                updaterHandler.postDelayed(this, Constants.UPDATE_INTERVAL_MS);
            }
        };
        updaterHandler.post(updaterRunnable);

    }

    public void stopUpdate() {
        updaterHandler.removeCallbacks(updaterRunnable);
    }



    private class StatusGetter extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... params) {
            return getStatusUpdateLighting(params[0]);

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //Send intent
            Intent intent = new Intent(Constants.LIGHTSUPDATE_ACTION);
            intent.putExtra(Constants.LIGHTSUPDATE_EXTRA, jsonObject.toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        private JSONObject getStatusUpdateLighting(String path) {
            //TODO: fix hard coded path
            path = Constants.baseUrl + path;
            String credentials = username + ":" + password;
            String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", basicAuth);
                if(connection.getResponseCode() == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String response = reader.readLine();
                    Log.d("Backend", response);
                    return new JSONObject(response);
                }
                else {
                    //TODO: implement
                }
            }
            catch(Exception e) {
                Log.e("Backend", e.getLocalizedMessage());
            }

            return new JSONObject();
        }
    }

    private class StatusPoster extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            setOutlet(params[0], params[1].contains("true"));
            return "";
        }


        private JSONObject setOutlet(String path, boolean value) {

            path = Constants.baseUrl + path;
            String credentials = username + ":" + password;
            String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            try {
                JSONObject json = new JSONObject();
                json.put("value", value);
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", basicAuth);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                BufferedOutputStream out = new BufferedOutputStream( connection.getOutputStream());
                connection.connect();
                out.write(json.toString().getBytes());
                out.close();
                Log.d("Backend", "" + connection.getResponseCode());
                if(connection.getResponseCode() == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String response = reader.readLine();
                    Log.d("Backend", response);
                    return new JSONObject(response);
                }
                else {
                    //TODO: implement
                }
            }
            catch(Exception e) {
                Log.e("Backend", e.getLocalizedMessage());
            }

            return new JSONObject();
        }
    }


}
