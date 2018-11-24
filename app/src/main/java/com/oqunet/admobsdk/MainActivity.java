package com.oqunet.admobsdk;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.oqunet.admob_sdk.AdMob;
import com.oqunet.admob_sdk.utils.AppUtils;

public class MainActivity extends RuntimePermissionsActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int ALL_REQUEST_PERMISSIONS = 20;
    public static int OVERLAY_PERMISSION_REQ_CODE_AD_HEAD = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("MainActivity", "MainActivity Started");

        if(!AppUtils.canDrawOverlays(MainActivity.this)) {
            requestPermission(OVERLAY_PERMISSION_REQ_CODE_AD_HEAD);
        }

        MainActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.READ_PHONE_STATE}, R.string
                        .runtime_permissions_txt
                , ALL_REQUEST_PERMISSIONS);


    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (requestCode == ALL_REQUEST_PERMISSIONS) {
            Log.d("onPermissionsGranted: ", "Permissions Received.");
            AdMob.registerPhoneCallsReceiver(this);

        }

    }

    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_AD_HEAD) {
            if (!AppUtils.canDrawOverlays(this)) {
                needPermissionDialog(requestCode);
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        AdMob.unregisterPhoneCallsReceiver(this);
    }
}
