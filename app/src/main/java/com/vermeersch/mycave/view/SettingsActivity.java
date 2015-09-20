package com.vermeersch.mycave.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.vermeersch.mycave.R;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //load username & password into the fields
        SharedPreferences pref = getSharedPreferences(getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
        String ipaddress = pref.getString(getString(R.string.ipaddress), "");
        String username = pref.getString(getString(R.string.username), "");
        String password = pref.getString(getString(R.string.password), "");
        ((EditText)findViewById(R.id.txtIpaddress)).setText(ipaddress);
        ((EditText)findViewById(R.id.txtPassword)).setText(password);
        ((EditText)findViewById(R.id.txtUsername)).setText(username);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onSave(View view) {
        //load the SharedPreferences file and save the data
        SharedPreferences pref = getSharedPreferences(getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(getString(R.string.ipaddress), ((EditText)findViewById(R.id.txtIpaddress)).getText().toString());
        edit.putString(getString(R.string.username), ((EditText)findViewById(R.id.txtUsername)).getText().toString());
        edit.putString(getString(R.string.password), ((EditText)findViewById(R.id.txtPassword)).getText().toString());
        edit.commit();
        //leave the screen
        finish();


    }
}
