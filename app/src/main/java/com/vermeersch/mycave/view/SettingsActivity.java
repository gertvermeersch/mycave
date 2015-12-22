package com.vermeersch.mycave.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

        ((EditText)findViewById(R.id.txtPassword)).setText(password);
        ((EditText)findViewById(R.id.txtUsername)).setText(username);

        
        ((EditText)findViewById(R.id.edtOutlet1)).setText(pref.getString(getString(R.string.outlet1), getString(R.string.outlet1)));
        ((EditText)findViewById(R.id.edtOutlet2)).setText(pref.getString(getString(R.string.outlet2), getString(R.string.outlet2)));
        ((EditText)findViewById(R.id.edtOutlet3)).setText(pref.getString(getString(R.string.outlet3), getString(R.string.outlet3)));
        ((EditText)findViewById(R.id.edtOutlet4)).setText(pref.getString(getString(R.string.outlet4), getString(R.string.outlet4)));
        ((EditText)findViewById(R.id.edtOutlet5)).setText(pref.getString(getString(R.string.outlet5), getString(R.string.outlet5)));
        ((EditText)findViewById(R.id.edtOutlet6)).setText(pref.getString(getString(R.string.outlet6), getString(R.string.outlet6)));

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

        edit.putString(getString(R.string.username), ((EditText)findViewById(R.id.txtUsername)).getText().toString());
        edit.putString(getString(R.string.password), ((EditText)findViewById(R.id.txtPassword)).getText().toString());
        edit.putString(getString(R.string.outlet1), ((EditText)findViewById(R.id.edtOutlet1)).getText().toString());
        edit.putString(getString(R.string.outlet2), ((EditText)findViewById(R.id.edtOutlet2)).getText().toString());
        edit.putString(getString(R.string.outlet3), ((EditText)findViewById(R.id.edtOutlet3)).getText().toString());
        edit.putString(getString(R.string.outlet4), ((EditText)findViewById(R.id.edtOutlet4)).getText().toString());
        edit.putString(getString(R.string.outlet5), ((EditText)findViewById(R.id.edtOutlet5)).getText().toString());
        edit.putString(getString(R.string.outlet6), ((EditText)findViewById(R.id.edtOutlet6)).getText().toString());
        edit.commit();
        //leave the screen
        finish();


    }
}
