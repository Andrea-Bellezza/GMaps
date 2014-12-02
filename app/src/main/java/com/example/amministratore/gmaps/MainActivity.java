package com.example.amministratore.gmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.amministratore.library_maps.MapsManager_Activity;


public class MainActivity extends ActionBarActivity {

    private Button Button_OpenMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button_OpenMaps = (Button) findViewById(R.id.button_openMaps);
        Button_OpenMaps.setOnClickListener(new  ButtonClick());
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ButtonClick implements View.OnClickListener{

       @Override
       public void onClick(View v) {
           openMapsActivity();
       }
   }


    private void openMapsActivity(){
        Intent intent = new Intent(this, MapsManager_Activity.class);
        startActivity(intent);
    }

}
