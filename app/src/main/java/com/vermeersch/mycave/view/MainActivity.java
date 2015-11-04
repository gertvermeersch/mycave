package com.vermeersch.mycave.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.vermeersch.mycave.Constants;
import com.vermeersch.mycave.R;
import com.vermeersch.mycave.controller.AutomationConnector;
import com.vermeersch.mycave.model.LightingStates;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {
    private LightingStates lightingStates;
    private AutomationConnector automationConnector;
    private BroadcastReceiver lightingUpdateReceiver;
    private BroadcastReceiver atmosphereUpdateReceiver;


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
                    JSONObject values = new JSONObject(intent.getStringExtra(Constants.JSON_EXTRA));
                    lightingStates.loadFromJson(values);
                    //update screen
                    ((TextView)findViewById(R.id.tvDesklightState)).setText(lightingStates.isDesk_light()?R.string.on:R.string.off);
                    ((TextView)findViewById(R.id.tvUpligherState)).setText(lightingStates.isUplighter()?R.string.on:R.string.off);
                    ((TextView)findViewById(R.id.tvStandingState)).setText(lightingStates.isStanding_lamp()?R.string.on:R.string.off);
                    ((TextView)findViewById(R.id.tvTwilightState)).setText(lightingStates.isTwilights()?R.string.on:R.string.off);
                } catch (JSONException e) {
                    Log.e("Backed", e.getLocalizedMessage());
                }

            }
        };

        this.atmosphereUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    JSONObject values = new JSONObject(intent.getStringExtra(Constants.JSON_EXTRA));
                    Log.d("Backend", values.toString());
                    ((TextView)findViewById(R.id.tvTemperatureValue)).setText(values.getDouble("currentTemperature") + " °C");
                    ((TextView)findViewById(R.id.tvHumidityValue)).setText(values.getDouble("currentHumidity") + "%");
                    ((TextView)findViewById(R.id.tvTargetTemperatureValue)).setText(String.format("%.1f °C", values.getDouble("targetTemperature")));
                    if(values.getBoolean("heating")) {
                        ((TextView)findViewById(R.id.tvTargetTemperature)).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    } else {
                        int color = ((TextView)findViewById(R.id.tvTargetTemperature)).getTextColors().getDefaultColor();
                        ((TextView)findViewById(R.id.tvTargetTemperature)).setTextColor(color);
                    }
                    if(values.getBoolean("override_athome") || ( values.getBoolean("athome") && !values.getBoolean("override_away"))) {
                        ((Switch)findViewById(R.id.swPresence)).setChecked(true);
                    } else {
                        ((Switch)findViewById(R.id.swPresence)).setChecked(false);
                    }
                }catch(JSONException e) {

                }
            }
        };
    }



    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(lightingUpdateReceiver, new IntentFilter(Constants.LIGHTSUPDATE_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(atmosphereUpdateReceiver, new IntentFilter(Constants.ATMOSPHEREUPDATE_ACTION));
        automationConnector.startUpdateOutlets();


    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(lightingUpdateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(atmosphereUpdateReceiver);
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
           case R.id.action_heating:
               openHeating();
               return true;
           default:
               return super.onOptionsItemSelected(item);
       }

    }

    private void openHeating() {
        startActivity(new Intent(this, HeatingActivity.class));
    }

    private void openSearch() {

    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));


    }


    public void onSwitch(View view) {
        String selection = (String)view.getTag();
        Log.d("Outlets", selection);
        switch (selection) {
            case("standing_on"):
                automationConnector.setRemoteValue("outlets/twilight", true);
                break;
            case("standing_off"):
                automationConnector.setRemoteValue("outlets/twilight", false);
                break;
            case("twilights_on"):
                automationConnector.setRemoteValue("outlets/dual_twilight", true);
                break;
            case("twilights_off"):
                automationConnector.setRemoteValue("outlets/dual_twilight", false);
                break;
            case("desklight_on"):
                automationConnector.setRemoteValue("outlets/desklight", true);
                break;
            case("desklight_off"):
                automationConnector.setRemoteValue("outlets/desklight", false);
                break;
            case("uplighter_on"):
                automationConnector.setRemoteValue("outlets/uplighter", true);
                break;
            case("uplighter_off"):
                automationConnector.setRemoteValue("outlets/uplighter", false);
                break;
            default:
                break;
        }

    }

    public void onPresenceClick(View view) {
        boolean value = ((Switch)view).isChecked();
        automationConnector.setRemoteValue("/climate/at_home", value);
    }
}
