package com.example.amministratore.gmaps;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.amministratore.library_maps.ListFragment;
import com.example.amministratore.library_maps.CustomMapsFragment;
import com.example.amministratore.library_maps.MapStateListener;
import com.google.android.gms.maps.GoogleMap;


public class TestMovingMapsListenerActivity extends ActionBarActivity {

    private ListFragment f;
    private static CustomMapsFragment customMaps;
    private static GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_moving_maps_listener_layout);

        customMaps = (CustomMapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mMap = customMaps.getMap();

        new MapStateListener(mMap, customMaps, this) {
            @Override
            public void onMapTouched() {
                getSupportActionBar().hide();
            }

            @Override
            public void onMapReleased() {
                getSupportActionBar().show();
            }

            @Override
            public void onMapUnsettled() {

            }

            @Override
            public void onMapSettled() {

            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_moving_maps_listener, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.example.amministratore.library_maps.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
