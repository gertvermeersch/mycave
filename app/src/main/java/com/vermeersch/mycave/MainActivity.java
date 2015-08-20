package com.vermeersch.mycave;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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


}
