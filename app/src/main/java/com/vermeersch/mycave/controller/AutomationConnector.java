package com.vermeersch.mycave.controller;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.vermeersch.mycave.AsyncTaskOnPostExecuteHandler;
import com.vermeersch.mycave.R;
import com.vermeersch.mycave.model.LightingStates;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Gert on 18/09/2015.
 */
public class AutomationConnector {
    private String username;
    private String password;
    private LightingStates lightingStates;

    public AutomationConnector() {
        lightingStates = new LightingStates();

    }


    public AutomationConnector(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void sendPost(String url, JSONObject payload) {

    }

    public void startUpdate(AsyncTaskOnPostExecuteHandler handler) {
        StatusGetter statusGetter = new StatusGetter();
        statusGetter.setHandler(handler);
        statusGetter.execute("outlets/all");
    }

    private JSONObject getStatusUpdateLighting(String path) {
      //TODO: fix hard coded path
        path = "https://hydra.vermeers.ch:8443/" + path;
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

    private class StatusGetter extends AsyncTask<String, Void, JSONObject> {
        private AsyncTaskOnPostExecuteHandler handler;

        public void setHandler(AsyncTaskOnPostExecuteHandler handler) {
            this.handler = handler;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return getStatusUpdateLighting(params[0]);

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(handler != null) {
                handler.onPostExecute(jsonObject);
            }

        }
    }


}
