package com.oqunet.admob_sdk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class PermissionsDelegate {

    private static final int REQUEST_CODE = 10;
    private final Activity activity;

    public PermissionsDelegate(Activity activity) {
        this.activity = activity;
    }

    public boolean hasCallPermission() {
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
        );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCallPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUEST_CODE
        );
    }

    public boolean resultGranted(int requestCode,
                          String[] permissions,
                          int[] grantResults) {

        if (requestCode != REQUEST_CODE) {
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

        requestCallPermission();
        Toast.makeText(activity, "Please enable Phone State permission.", Toast.LENGTH_SHORT).show();
        return false;
    }
}
