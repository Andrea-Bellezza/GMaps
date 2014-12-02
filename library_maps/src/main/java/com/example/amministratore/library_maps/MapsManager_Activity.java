package com.example.amministratore.library_maps;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MapsManager_Activity extends ActionBarActivity implements ListFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_manager_layout);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
