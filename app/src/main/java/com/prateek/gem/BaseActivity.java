package com.prateek.gem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Group;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.utils.AppSharedPreference;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.GroupsLandingActivity;

import java.io.IOException;

/**
 * Created by prateek on 18/11/14.
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected BaseActivity baseActivity = null;
    protected GoogleCloudMessaging gcm = null;
    protected String regid = null;
    protected Intent mainLandingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = this;
        mainLandingIntent = new Intent(baseActivity,GroupsLandingActivity.class);
        AppDataManager.appContext = getApplicationContext();
        AppDataManager.currentScreen = baseActivity;
        if(Utils.isConnected(baseActivity)) {
            getGCMRegId();
        }
    }

    protected void getGCMRegId() {
        String regId = AppSharedPreference.getPreferenceString(AppConstants.ADMIN_ID);
        if (regId == null || regId.equals(AppConstants.EMPTY_STRING)) {
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                registerInBackground();
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(baseActivity);
                    }
                    regid = gcm.register(AppConstants.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    //registerDevice();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                DebugLogger.method("onPostExecute");
                DebugLogger.message(msg);

            }
        }.execute(null, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugLogger.message("BaseActivity :: onActivityResult");
    }

    public void onItemSelected(Group group) {
        //Do nothing
    }
}
