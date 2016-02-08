package com.vermeersch.mycave.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.lobsterpicker.LobsterPicker;
import com.vermeersch.mycave.Constants;
import com.vermeersch.mycave.R;
import com.vermeersch.mycave.controller.AutomationConnector;
import com.vermeersch.mycave.model.LightingStates;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements ColourPickerFragment.OnColourPickerFragmentListener {
    private LightingStates lightingStates;
    private AutomationConnector automationConnector;
    private BroadcastReceiver lightingUpdateReceiver;
    private BroadcastReceiver atmosphereUpdateReceiver;
    private BroadcastReceiver errorReceiver;
    private int colour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lightingStates = new LightingStates();
        automationConnector = new AutomationConnector(getApplicationContext());
        //load custom names
        SharedPreferences pref = getSharedPreferences(getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.txtOutlet1)).setText(pref.getString(getString(R.string.outlet1), getString(R.string.outlet1)));
        ((TextView)findViewById(R.id.txtOutlet2)).setText(pref.getString(getString(R.string.outlet2), getString(R.string.outlet2)));
        ((TextView)findViewById(R.id.txtOutlet3)).setText(pref.getString(getString(R.string.outlet3), getString(R.string.outlet3)));
        ((TextView)findViewById(R.id.txtOutlet4)).setText(pref.getString(getString(R.string.outlet4), getString(R.string.outlet4)));
        ((TextView)findViewById(R.id.txtOutlet5)).setText(pref.getString(getString(R.string.outlet5), getString(R.string.outlet5)));
        ((TextView)findViewById(R.id.txtOutlet6)).setText(pref.getString(getString(R.string.outlet6), getString(R.string.outlet6)));

        createBroadcastReceivers();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
    }

    private void createBroadcastReceivers() {
        this.lightingUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    JSONObject values = new JSONObject(intent.getStringExtra(Constants.JSON_EXTRA));
                    lightingStates.loadFromJson(values);
                    //update screen
                    ((Switch)findViewById(R.id.swOutlet1)).setChecked(lightingStates.isStanding_lamp());
                    ((Switch)findViewById(R.id.swOutlet2)).setChecked(lightingStates.isTwilights());
                    ((Switch)findViewById(R.id.swOutlet3)).setChecked(lightingStates.isDesk_light());
                    ((Switch)findViewById(R.id.swOutlet4)).setChecked(lightingStates.isUplighter());
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

        this.errorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, intent.getStringExtra(Constants.ERROR_EXTRA), Toast.LENGTH_LONG).show();
            }
        };
    }



    @Override
    protected void onResume() {
        super.onResume();
        //refresh the naming
        SharedPreferences pref = getSharedPreferences(getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.txtOutlet1)).setText(pref.getString(getString(R.string.outlet1), getString(R.string.outlet1)));
        ((TextView)findViewById(R.id.txtOutlet2)).setText(pref.getString(getString(R.string.outlet2), getString(R.string.outlet2)));
        ((TextView)findViewById(R.id.txtOutlet3)).setText(pref.getString(getString(R.string.outlet3), getString(R.string.outlet3)));
        ((TextView)findViewById(R.id.txtOutlet4)).setText(pref.getString(getString(R.string.outlet4), getString(R.string.outlet4)));
        ((TextView)findViewById(R.id.txtOutlet5)).setText(pref.getString(getString(R.string.outlet5), getString(R.string.outlet5)));
        ((TextView)findViewById(R.id.txtOutlet6)).setText(pref.getString(getString(R.string.outlet6), getString(R.string.outlet6)));
        //register on broadcasts
        LocalBroadcastManager.getInstance(this).registerReceiver(lightingUpdateReceiver, new IntentFilter(Constants.LIGHTSUPDATE_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(atmosphereUpdateReceiver, new IntentFilter(Constants.ATMOSPHEREUPDATE_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(errorReceiver, new IntentFilter(Constants.ERROR_ACTION));
        automationConnector.startUpdatePeripherals();


    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(lightingUpdateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(atmosphereUpdateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(errorReceiver);
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
        boolean value = ((Switch)view).isChecked();
        Log.d("Outlets", selection);
        switch (selection) {
            case("outlet1"):
                automationConnector.setRemoteValue("outlets/outlet1", value);
                break;

            case("outlet2"):
                automationConnector.setRemoteValue("outlets/outlet2", value);
                break;

            case("outlet3"):
                automationConnector.setRemoteValue("outlets/outlet3", value);
                break;

            case("outlet4"):
                automationConnector.setRemoteValue("outlets/outlet4", value);
                break;

            case("outlet5"):
                automationConnector.setRemoteValue("outlets/outlet5", value);
                break;
            case("outlet6"):
                automationConnector.setRemoteValue("outlets/outlet6", value);
                break;

            default:
                break;
        }

    }

    public void onPresenceClick(View view) {
        boolean value = ((Switch)view).isChecked();
        automationConnector.setRemoteValue("climate/at_home", value);
    }

    public void openColourPicker(View view) {
        ColourPickerFragment colourPickerDialogFragment = new ColourPickerFragment();

        colourPickerDialogFragment.show(getFragmentManager(), "ColourPicker");
    }



    @Override
    public void onColourSelected(int colour) {
        this.colour = colour;
        try {
            JSONObject payload = new JSONObject();
            payload.put("red", Color.red(colour));
            payload.put("green", Color.green(colour));
            payload.put("blue", Color.blue(colour));
            JSONObject wrapper = new JSONObject();
            wrapper.put("values", payload);
            AutomationConnector.getInstance().postPayload("ledstrip/rgb", wrapper);
        } catch(JSONException e) {
            Log.e("Colour", e.getLocalizedMessage());
        }
    }

    @Override
    public void onLedStripStateChanged(boolean state) {
        if(state) {
            onColourSelected(this.colour);
        } else {
            try {
                JSONObject payload = new JSONObject();
                payload.put("red", 0);
                payload.put("green", 0);
                payload.put("blue", 0);
                JSONObject wrapper = new JSONObject();
                wrapper.put("values", payload);
                AutomationConnector.getInstance().postPayload("ledstrip/rgb", wrapper);
            } catch(JSONException e) {
                Log.e("Colour", e.getLocalizedMessage());
            }
        }


    }
}
