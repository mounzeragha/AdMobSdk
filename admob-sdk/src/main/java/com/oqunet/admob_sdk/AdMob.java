package com.oqunet.admob_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.oqunet.admob_sdk.receiver.PhoneStateReceiver;

public class AdMob {
    private static PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();


    public static void registerPhoneCallsReceiver(Context context) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        context.registerReceiver(phoneStateReceiver, intentFilter);

    }

    public static void unregisterPhoneCallsReceiver(Context context) {
        context.unregisterReceiver(phoneStateReceiver);
    }


}
