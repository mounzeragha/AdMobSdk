package com.oqunet.mobad_sdk.service;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceID extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.d(TAG, "Refreshing FCM Registration Token");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, RegistrationIntentService.class));
        }
        else {
            startService(new Intent(this, RegistrationIntentService.class));
        }
    }
}
