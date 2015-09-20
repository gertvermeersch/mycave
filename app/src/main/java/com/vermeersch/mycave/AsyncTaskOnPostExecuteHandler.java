package com.vermeersch.mycave;


import org.json.JSONObject;

/**
 * Created by Gert on 20/09/2015.
 */
public interface AsyncTaskOnPostExecuteHandler {
    void onPostExecute(JSONObject values);
}
