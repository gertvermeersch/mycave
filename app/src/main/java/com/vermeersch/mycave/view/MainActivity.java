package com.vermeersch.mycave.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.vermeersch.mycave.AsyncTaskOnPostExecuteHandler;
import com.vermeersch.mycave.Constants;
import com.vermeersch.mycave.R;
import com.vermeersch.mycave.controller.AutomationConnector;
import com.vermeersch.mycave.model.LightingStates;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements AsyncTaskOnPostExecuteHandler {
    private LightingStates lightingStates;
    private AutomationConnector automationConnector;
    private BroadcastReceiver lightingUpdateReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lightingStates = new LightingStates();
        automationConnector = new AutomationConnector("domoticaApp", "D0m0t1c4", getApplicationContext());
        createBroadcastReceivers();
    }

    private void createBroadcastReceivers() {
        this.lightingUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    JSONObject values = new JSONObject(intent.getStringExtra(Constants.LIGHTSUPDATE_EXTRA));
                    lightingStates.loadFromJson(values);
                } catch (JSONException e) {
                    Log.e("Backed", e.getLocalizedMessage());
                }

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(lightingUpdateReceiver, new IntentFilter(Constants.LIGHTSUPDATE_ACTION));
        automationConnector.startUpdate();


    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(lightingUpdateReceiver);
        automationConnector.stopUpdate();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       switch(id) {
           case R.id.action_search:
               openSearch();
               return true;
           case R.id.action_settings:
               openSettings();
               return true;
           default:
               return super.onOptionsItemSelected(item);
       }

    }

    private void openSearch() {

    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));


    }


    public void onTest(View view) {

        automationConnector.startUpdate();
    }

    @Override
    public void onPostExecute(JSONObject values) {
        try {
            lightingStates.setDesk_light(values.getBoolean("desklight"));
            lightingStates.setStanding_lamp(values.getBoolean("twilight"));
            lightingStates.setTwilights(values.getBoolean("dual_twilight"));
            lightingStates.setUplighter(values.getBoolean("uplighter"));
            //saltlamp
            //vaporizer
        }catch(JSONException e) {
            Log.e("Backend", e.getLocalizedMessage());
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
