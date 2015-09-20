package com.vermeersch.mycave.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.vermeersch.mycave.AsyncTaskOnPostExecuteHandler;
import com.vermeersch.mycave.R;
import com.vermeersch.mycave.controller.AutomationConnector;
import com.vermeersch.mycave.model.LightingStates;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements AsyncTaskOnPostExecuteHandler {
    private LightingStates lightingStates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lightingStates = new LightingStates();


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
        AutomationConnector automationConnector = new AutomationConnector("domoticaApp", "D0m0t1c4");
        automationConnector.startUpdate(this);
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
