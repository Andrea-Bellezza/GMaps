package com.example.amministratore.push_notification_custom_library;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;


public class PushNotificationMainActivity extends ActionBarActivity {

//  private static final String SERVER_IP = "192.168.1.100";
    private static final String SERVER_IP = "192.168.16.119";

    private static final int DOOR = 5340;
//  private static String URL = "http://posttestserver.com/post.php" ;   //URL for testing HTTP post
    private static final String URL = "http://"+SERVER_IP+":"+DOOR+"/test" ;   //URL for local server

    private static final String PROPERTY_REG_ID = "registration_id";

    //TODO: change the path where he get tha app version
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    //TODO: find better place to store it

    private final static String GCM_REG_ID = "275017101050";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM_Library";

    private static TextView mDisplay;
    private static GoogleCloudMessaging gcm;
    private static Context context;

    private static String regid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_notification_layout);

        mDisplay = (TextView) findViewById(R.id.display);

        context = getApplicationContext();

        //get String from Notification text
        mDisplay.setText("Text: " + getIntent().getStringExtra("a"));

        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
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


    public void onClick(final View view) {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {

                sendRegistrationIdToBackend();

                return "";
            }
        }.execute(null, null, null);
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("error", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(PushNotificationMainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }



    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(GCM_REG_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object msg) {
                if(msg instanceof String)
                    mDisplay.append(msg + "\n");
            }

        }.execute(null, null, null);

    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    private void sendRegistrationIdToBackend() {
        //socket_SendID();
        HTTP_sendID();
    }


    private void HTTP_sendID(){

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);

        try {
            //TODO: to substitute regid with a Json
            StringEntity entity = new StringEntity(regid);
            httppost.setEntity(entity);

            final HttpResponse response = httpclient.execute(httppost);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDisplay.setText(Integer.toString(response.getStatusLine().getStatusCode()));
                }
            });

            Log.w("HTTP_POST Response", Integer.toString(response.getStatusLine().getStatusCode()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void socket_SendID(){
        BufferedWriter buffer;
        try {
            // Open your connection to a server, at port 5432
            // localhost used here
            Socket socket = new Socket(SERVER_IP, DOOR);

            OutputStream outputStream = socket.getOutputStream();

            buffer = new BufferedWriter(new OutputStreamWriter(outputStream));
            buffer.write(regid);
            buffer.close();

            socket.close();
            Log.i("SERVER_SEND","Sended regID: " + regid);

        } catch (ConnectException connExc) {
            Log.e("EXTERNAL SERVER PROBLEM", connExc.toString());
        } catch (IOException e) {
            Log.e("EXTERNAL SERVER PROBLEM", e.toString());
        }
    }

}
