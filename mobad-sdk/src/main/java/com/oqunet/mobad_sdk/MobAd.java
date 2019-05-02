package com.oqunet.mobad_sdk;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.oqunet.mobad_sdk.receiver.MyNotificationsHandler;
import com.oqunet.mobad_sdk.receiver.PhoneCallReceiver;
import com.oqunet.mobad_sdk.receiver.PhoneStateReceiver;
import com.oqunet.mobad_sdk.service.AdJobIntentService;
import com.oqunet.mobad_sdk.service.AdJobService;
import com.oqunet.mobad_sdk.service.RegistrationIntentService;
import com.oqunet.mobad_sdk.service.SyncAdWork;
import com.oqunet.mobad_sdk.settings.NotificationSettings;
import com.oqunet.mobad_sdk.utils.MobAdUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static android.content.Context.POWER_SERVICE;


public class MobAd {
    private static final String LOG_TAG = MobAd.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 10;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1234;
    private final Activity activity;


    public MobAd(Activity activity) {
        this.activity = activity;
    }

    public void startMobAdService() {


        /**
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AdJobIntentService.enqueueWork(activity, new Intent());
        } else {
            activity.startService(new Intent(activity, AdJobService.class));
        }
         */


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivity(intent);
            }
        }


        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest.Builder adWorkRequest =
                new PeriodicWorkRequest.Builder(SyncAdWork.class, 15, TimeUnit.MINUTES)
                        .addTag("periodic-work-request")
                        .setConstraints(myConstraints);
        PeriodicWorkRequest adWork = adWorkRequest.build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("periodic-work-request", ExistingPeriodicWorkPolicy.KEEP, adWork);


        /**
         OneTimeWorkRequest adWorkRequest = new OneTimeWorkRequest.Builder(SyncAdWork.class)
                 .build();
         WorkManager.getInstance().enqueue(adWorkRequest);
        */

        registerWithNotificationHubs();
    //    MyNotificationsHandler.createChannelAndHandleNotifications(activity);
        NotificationsManager.handleNotifications(activity, NotificationSettings.SenderId, MyNotificationsHandler.class);

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

    private void requestPermission(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
    }

    public void requestDrawOverAppsPermission() {
        if (!MobAdUtils.canDrawOverlays(activity)) {
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

    private void registerWithNotificationHubs() {
        Log.i(LOG_TAG, " Registering with Notification Hubs");

        if (checkPlayServices()) {
            activity.startService(new Intent(activity, RegistrationIntentService.class));

        }
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG, "This device is not supported by Google Play Services.");
            }
            return false;
        }
        return true;
    }
}
