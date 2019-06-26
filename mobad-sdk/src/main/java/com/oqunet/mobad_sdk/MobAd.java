package com.oqunet.mobad_sdk;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.oqunet.mobad_sdk.database.AppDatabase;
import com.oqunet.mobad_sdk.database.entity.User;
import com.oqunet.mobad_sdk.receiver.MyNotificationsHandler;
import com.oqunet.mobad_sdk.receiver.PhoneStateReceiver;
import com.oqunet.mobad_sdk.service.RegistrationIntentService;
import com.oqunet.mobad_sdk.service.SyncAdWork;
import com.oqunet.mobad_sdk.utils.Constants;
import com.oqunet.mobad_sdk.utils.GpsUtils;
import com.oqunet.mobad_sdk.utils.MobAdUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static android.content.Context.POWER_SERVICE;


public class MobAd {
    private static final String LOG_TAG = MobAd.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1234;
    private final Activity activity;
    private SparseIntArray mErrorString;
    private FusedLocationProviderClient mFusedLocationClient;
    private double userLatitude = 0.0, userLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private User user;


    public MobAd(Activity activity) {
        this.activity = activity;
        mErrorString = new SparseIntArray();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        user = MobAdUtils.getUser(activity);
    }

    private void startMobAdService() {

        /**
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         AdJobIntentService.enqueueWork(activity, new Intent());
         } else {
         activity.startService(new Intent(activity, AdJobService.class));
         }
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivity(intent);
            }
        }

        Constraints adWorkConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest.Builder adWorkRequest =
                new PeriodicWorkRequest.Builder(SyncAdWork.class, 15, TimeUnit.MINUTES)
                        .addTag("periodic-ad-work-request")
                        .setConstraints(adWorkConstraints);
        PeriodicWorkRequest adWork = adWorkRequest.build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("periodic-ad-work-request", ExistingPeriodicWorkPolicy.KEEP, adWork);


        /**
         OneTimeWorkRequest adWorkRequest = new OneTimeWorkRequest.Builder(SyncAdWork.class)
         .build();
         WorkManager.getInstance().enqueue(adWorkRequest);
         */

    }

    private void initializeNotifications() {
        registerWithNotificationHubs();
        MyNotificationsHandler.createChannelAndHandleNotifications(activity);
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
                Constants.READ_PHONE_STATE_PERMISSION_REQUEST
        );
    }

    public boolean hasReadPhoneStatePermissionGranted(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode != Constants.READ_PHONE_STATE_PERMISSION_REQUEST) {
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

    public void checkMobAdPermissionsAndStartService() {
        checkMobAdPermissions(new
                        String[]{android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, R.string
                        .runtime_permissions_txt
                , Constants.ALL_REQUEST_PERMISSIONS);
    }

    private void checkMobAdPermissions(final String[] requestedPermissions,
                                       final int stringId, final int requestCode) {
        mErrorString.put(requestCode, stringId);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(activity, permission);
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale) {
                Snackbar.make(activity.findViewById(android.R.id.content), stringId,
                        Snackbar.LENGTH_INDEFINITE).setAction("GRANT",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(activity, requestedPermissions, requestCode);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(activity, requestedPermissions, requestCode);
            }
        } else {
            onPermissionsGranted(requestCode);
        }
    }

    public void onRequestMobAdPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), mErrorString.get(requestCode),
                    Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + activity.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            activity.startActivity(intent);
                        }
                    }).show();
        }
    }

    private void onPermissionsGranted(int requestCode) {
        if (requestCode == Constants.ALL_REQUEST_PERMISSIONS) {
            startMobAdService();
            initializeNotifications();
            getUserLocation();
        }
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
        return Constants.READ_PHONE_STATE_PERMISSION_REQUEST;
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

    private void getUserLocation() {

        new GpsUtils(activity).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });


        if (isGPS) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });
        } else {
            Log.i(LOG_TAG, "GPS LOCATION: OFF");

        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.i(LOG_TAG, "LOCATION: null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        Log.i(LOG_TAG, "Latitude: " + userLatitude + " - " + "Longitude: " + userLongitude);
                        getUserAddressAndCountry(userLatitude, userLongitude);

                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    } else {
                        Log.i(LOG_TAG, "LOCATION: null");
                    }
                }
            }
        };


    }

    private void getUserAddressAndCountry(double userLatitude, double userLongitude) {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(userLatitude, userLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("max", " " + addresses.get(0).getMaxAddressLineIndex());

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String countryCode = addresses.get(0).getCountryCode();

        addresses.get(0).getAdminArea();
        if (countryCode != null) {
            Log.i(LOG_TAG, "COUNTRY CODE: " + countryCode);
            if (user != null) {
                user.setCountryCode(countryCode);
                AppDatabase.getInstance(activity).getUserDao().updateUser(user);
            } else {
                User newUser = new User();
                newUser.setCountryCode(countryCode);
                AppDatabase.getInstance(activity).getUserDao().insertUser(newUser);
            }
        } else {
            Log.i(LOG_TAG, "COUNTRY CODE: null");
        }

        Log.i("Addresses: ", "Address: " + address + "\n" + "City: " + city + "\n" + "State: " + state + "\n" + "Country Name: " + country + "\n" + "Country Code: " + countryCode);

    }
}
