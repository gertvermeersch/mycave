package com.vermeersch.mycave.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.vermeersch.mycave.Constants;
import com.vermeersch.mycave.R;
import com.vermeersch.mycave.controller.AutomationConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HeatingActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heating);
        LocalBroadcastManager.getInstance(this).registerReceiver(new climateConfigReceiver(), new IntentFilter(Constants.HEATINGUPDATE_ACTION));
        updateValues();
    }

    @Override
    protected void onResume() {
        updateValues();
        super.onResume();
    }

    private void updateValues() {
        AutomationConnector.getInstance().startSingleUpdateHeating();
    }

    public void saveValues(View view) {

        try {
            JSONObject payload = new JSONObject();
            payload.put("weekend_start_time", ((EditText) findViewById(R.id.etEndWeekend)).getText());
            payload.put("weekend_stop_time", ((EditText)findViewById(R.id.etBeginWeekEvening)).getText());
            payload.put("week_start_evening", ((EditText)findViewById(R.id.etBeginWeekEvening)).getText());
            payload.put("week_end_evening", ((EditText)findViewById(R.id.etEndWeekEvening)).getText());
            payload.put("week_start_morning", ((EditText)findViewById(R.id.etBeginWeekMorning)).getText());
            payload.put("week_end_morning", ((EditText)findViewById(R.id.etEndWeekMorning)).getText());
            payload.put("temperature_present", ((EditText)findViewById(R.id.etTemperatureHigh)).getText());
            payload.put("temperature_away", ((EditText)findViewById(R.id.etTemperatureLow)).getText());
            AutomationConnector.getInstance().postPayload("climate/config", payload);
        }
        catch(Exception e) {
            Log.e("Backend", e.getLocalizedMessage());
        }

    }

    private class climateConfigReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONObject configValues = new JSONObject(intent.getStringExtra(Constants.JSON_EXTRA));

                ((EditText)findViewById(R.id.etBeginWeekend)).setText(configValues.getString("weekend_start_time"));
                ((EditText)findViewById(R.id.etEndWeekend)).setText(configValues.getString("weekend_stop_time"));
                ((EditText)findViewById(R.id.etBeginWeekEvening)).setText(configValues.getString("week_start_evening"));
                ((EditText)findViewById(R.id.etEndWeekEvening)).setText(configValues.getString("week_end_evening"));
                ((EditText)findViewById(R.id.etBeginWeekMorning)).setText(configValues.getString("week_start_morning"));
                ((EditText)findViewById(R.id.etEndWeekMorning)).setText(configValues.getString("week_end_morning"));
                ((EditText)findViewById(R.id.etTemperatureHigh)).setText(configValues.getString("temperature_present"));
                ((EditText)findViewById(R.id.etTemperatureLow)).setText(configValues.getString("temperature_away"));


            }catch(Exception e) {

            }
        }
    }

}
