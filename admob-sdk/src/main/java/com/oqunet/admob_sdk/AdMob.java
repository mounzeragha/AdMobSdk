package com.oqunet.admob_sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.oqunet.admob_sdk.receiver.PhoneStateReceiver;
import com.oqunet.admob_sdk.utils.AppUtils;
import com.oqunet.admob_sdk.utils.PermissionsDelegate;

public class AdMob {
    private static final String BROADCAST = "com.oqunet.admob_sdk.android.intent.action.PHONE_STATE";


    public static void showAdAfterCall(Activity activity) {


        Intent intent = new Intent(BROADCAST);
        activity.sendBroadcast(intent);

        /**
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        context.registerReceiver(new PhoneStateReceiver(), intentFilter);
         */



    //    Intent broadcast = new Intent(context, PhoneStateReceiver.class);
    //    context.sendBroadcast(broadcast, "com.oqunet.admob_sdk.android.intent.action.PHONE_STATE");

    }


}
