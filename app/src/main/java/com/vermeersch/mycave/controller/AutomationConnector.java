package com.vermeersch.mycave.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.vermeersch.mycave.Constants;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public void setRemoteValue(String path, boolean value) {
        RemoteSetter poster = new RemoteSetter();
        poster.execute(path, value?"true":"false");
    }

    public void startUpdateOutlets() {

        updaterRunnable = new Runnable() {
            @Override
            public void run() {
                StatusGetter statusGetter = new StatusGetter();
                statusGetter.setIntentType(Constants.LIGHTSUPDATE_ACTION);
                statusGetter.execute("outlets/all");
                StatusGetter statusGetter2 = new StatusGetter();
                statusGetter2.setIntentType(Constants.ATMOSPHEREUPDATE_ACTION);
                statusGetter2.execute("climate/sensors");
                updaterHandler.postDelayed(this, Constants.UPDATE_INTERVAL_MS);
            }
        };
        updaterHandler.post(updaterRunnable);

    }

    public void startSingleUpdateHeating() {
        StatusGetter getter = new StatusGetter();
        getter.setIntentType(Constants.HEATINGUPDATE_ACTION);
        getter.execute("climate/config");
    }

    public void stopUpdate() {
        updaterHandler.removeCallbacks(updaterRunnable);
    }



    private class StatusGetter extends AsyncTask<String, Void, JSONObject> {
        private String intentType = "";

        public void setIntentType(String intentType) {
            this.intentType = intentType;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return getStatusUpdate(params[0]);

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //Send intent
            Intent intent = new Intent(intentType);
            intent.putExtra(Constants.JSON_EXTRA, jsonObject.toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        private JSONObject getStatusUpdate(String path) {
            //TODO: fix hard coded credentials
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

    private class RemoteSetter extends AsyncTask<String, Void, String> {


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

    public void postPayload(String path, JSONObject json) {
        PayloadPoster poster  = new PayloadPoster();
        poster.setPath(path);
        poster.execute(json);
    }

    private class PayloadPoster extends AsyncTask<JSONObject, Void, String> {
        private String path = "";

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            postPayload(params[0]);
            return "";
        }


        private JSONObject postPayload(JSONObject payload) {

            path = Constants.baseUrl + path;
            String credentials = username + ":" + password;
            String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            try {

                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", basicAuth);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                BufferedOutputStream out = new BufferedOutputStream( connection.getOutputStream());
                connection.connect();
                out.write(payload.toString().getBytes());
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
