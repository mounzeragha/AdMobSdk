package com.oqunet.mobad_sdk.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oqunet.mobad_sdk.R;

import java.util.UUID;


public class MobAdUtils {
    public static String EXTRA_MSG = "extra_msg";
    private static Context context;

    public MobAdUtils(Context context) {
        this.context = context;
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            return Settings.canDrawOverlays(context);
        }


    }

    public static void openWebUrlExternal(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the play store to install the app.
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * @SuppressLint("HardwareIds") public static String getDeviceID(Context context) {
     * return Settings.Secure.getString(context.getContentResolver(),
     * Settings.Secure.ANDROID_ID);
     * }
     */
    @SuppressLint("HardwareIds")
    public static String getUniqueIMEIId(Context context) {
        /**
         final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
         if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         //    ActivityCompat#requestPermissions
         // here to request the missing permissions, and then overriding
         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
         //                                          int[] grantResults)
         // to handle the case where the user grants the permission. See the documentation
         // for ActivityCompat#requestPermissions for more details.
         return "";
         }

         final String tmDevice, tmSerial, androidId;
         tmDevice = "" + tm.getDeviceId();
         tmSerial = "" + tm.getSimSerialNumber();
         androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

         UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
         */

        String deviceId = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            assert telephonyManager != null;
            String uniqueId = telephonyManager.getDeviceId();
            if (uniqueId != null && !uniqueId.isEmpty()) {
                deviceId = uniqueId;
            } else {
                uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                deviceId = uniqueId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceId;
    }

    public static void displaySuccessToast(Activity activity, String message) {
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = activity.getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(message);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_done);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(ContextCompat.getColor(activity, R.color.green_500));

        toast.setView(custom_view);
        toast.show();
    }

    public static void displayInfoToast(Activity activity, String message) {
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = activity.getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(message);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_info);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(ContextCompat.getColor(activity, R.color.blue_500));

        toast.setView(custom_view);
        toast.show();
    }

    public static void displayErrorToast(Activity activity, String message) {
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = activity.getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(message);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(ContextCompat.getColor(activity, R.color.red_600));

        toast.setView(custom_view);
        toast.show();
    }
}
