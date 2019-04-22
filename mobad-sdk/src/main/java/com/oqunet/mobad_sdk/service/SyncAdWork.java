package com.oqunet.mobad_sdk.service;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import com.oqunet.mobad_sdk.receiver.PhoneCallReceiver;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SyncAdWork extends Worker {
    private static final String LOG_TAG = SyncAdWork.class.getSimpleName();
    private PhoneCallReceiver phoneCallReceiver = new PhoneCallReceiver();

    public SyncAdWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(LOG_TAG, "OnCreate SyncAdWork");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction("android.intent.action.QUICKBOOT_POWERON");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        getApplicationContext().registerReceiver(phoneCallReceiver, intentFilter);

        Log.i(LOG_TAG, "PhoneCallReceiver Created....");

        return Result.success();
    }

    /**
    @Override
    public void onStopped() {
        super.onStopped();
        getApplicationContext().unregisterReceiver(phoneCallReceiver);
        Log.i(LOG_TAG, "PhoneCallReceiver Destroyed....");
    }
    */

}
