package com.oqunet.mobad_sdk;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.oqunet.mobad_sdk.receiver.PhoneStateReceiver;
import com.oqunet.mobad_sdk.utils.MobAdUtils;


public class MobAd {
    private static PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 10;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1234;
    private final Activity activity;



    public MobAd(Activity activity) {
        this.activity = activity;
    }

    public void registerPhoneCallsReceiver() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        activity.registerReceiver(phoneStateReceiver, intentFilter);

    }

    public void unregisterPhoneCallsReceiver() {
        try {
            activity.unregisterReceiver(phoneStateReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public boolean hasReadPhoneStatePermission() {
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
        );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

    public void requestReadPhoneStatePermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                READ_PHONE_STATE_PERMISSION_REQUEST_CODE
        );
    }

    public boolean hasReadPhoneStatePermissionGranted(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode != READ_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            return false;
        }

        if (grantResults.length < 1) {
            return false;
        }
        if (!(permissions[0].equals(Manifest.permission.READ_PHONE_STATE))) {
            return false;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestReadPhoneStatePermission();
        Toast.makeText(activity, "Please enable permission.", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
    }

    public void requestDrawOverAppsPermission() {
        if(!MobAdUtils.canDrawOverlays(activity)) {
            requestPermission(OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    public boolean canDrawOverlays() {
        return MobAdUtils.canDrawOverlays(activity);
    }

    public static int getReadPhoneStatePermissionRequestCode() {
        return READ_PHONE_STATE_PERMISSION_REQUEST_CODE;
    }

    public int getOverlayPermissionRequestCode() {
        return OVERLAY_PERMISSION_REQUEST_CODE;
    }
}
