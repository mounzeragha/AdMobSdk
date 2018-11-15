package com.oqunet.admob_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class AdMob {
    private static final String BROADCAST = "com.oqunet.admob_sdk.android.intent.action.PHONE_STATE";


    public static void showAdAfterCall(Context context) {

        Intent intent = new Intent(BROADCAST);
        context.sendBroadcast(intent);

        /**
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        context.registerReceiver(new PhoneStateReceiver(), intentFilter);
         */



    //    Intent broadcast = new Intent(context, PhoneStateReceiver.class);
    //    context.sendBroadcast(broadcast, "com.oqunet.admob_sdk.android.intent.action.PHONE_STATE");

    }


}
