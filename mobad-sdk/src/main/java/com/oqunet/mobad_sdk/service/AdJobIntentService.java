package com.oqunet.mobad_sdk.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.oqunet.mobad_sdk.receiver.PhoneCallReceiver;

public class AdJobIntentService extends JobIntentService {
    private static final String LOG_TAG = AdJobIntentService.class.getSimpleName();
    private PhoneCallReceiver phoneCallReceiver;
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AdJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Log.i(LOG_TAG, "OnCreate AdJobService");

        phoneCallReceiver = new PhoneCallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        this.registerReceiver(phoneCallReceiver, intentFilter);

        Log.i(LOG_TAG, "PhoneCallReceiver Created....");

    }

    @Override
    public void onDestroy() {
        if (phoneCallReceiver != null) {
            this.unregisterReceiver(phoneCallReceiver);
            Log.i(LOG_TAG, "PhoneCallReceiver Destroyed....");
        }

    }
}
