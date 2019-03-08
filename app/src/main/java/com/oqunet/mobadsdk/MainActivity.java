package com.oqunet.mobadsdk;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.oqunet.mobad_sdk.MobAd;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    MobAd mobAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "MainActivity Started");

        //Initialize mobAd.
        mobAd = new MobAd(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //request overlay permission if it has not been granted.
        mobAd.requestDrawOverAppsPermission();

        //request read phone state permission.
        if (mobAd.hasReadPhoneStatePermission()) {
            //You already have the permission, just go ahead.
            mobAd.registerPhoneCallsReceiver();
        } else {
            //request the permission.
            mobAd.requestReadPhoneStatePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mobAd.hasReadPhoneStatePermissionGranted(requestCode, permissions, grantResults)) {
            Log.i("onPermissionsGranted: ", "Read Phone State Permission Granted.");
            mobAd.registerPhoneCallsReceiver();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mobAd.unregisterPhoneCallsReceiver();
    }
}
