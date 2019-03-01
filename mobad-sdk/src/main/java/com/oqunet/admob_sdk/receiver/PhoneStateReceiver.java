package com.oqunet.admob_sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.oqunet.admob_sdk.models.Advertiser;
import com.oqunet.admob_sdk.models.Job;
import com.oqunet.admob_sdk.service.JobCreator;
import com.oqunet.admob_sdk.service.SyncJob;

import java.util.Random;



public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = PhoneStateReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Log.d(LOG_TAG, "Receiver start");
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Log.d(LOG_TAG, "Incoming Call State");
                Log.d(LOG_TAG, "Ringing State Number is -" + incomingNumber);

                }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                Log.d(LOG_TAG, "Call Received State");
                }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Log.d(LOG_TAG, "Call Idle State");

                JobManager.create(context).addJobCreator(new JobCreator());
                int jobId = new JobRequest.Builder(SyncJob.TAG)
                        .startNow()
                        .build()
                        .schedule();
                Job.setJobId(jobId);

                if(state.equals(TelephonyManager.CALL_STATE_OFFHOOK)){
                    //Answered Call which is ended


                }
                if(state.equals(TelephonyManager.CALL_STATE_RINGING)){
                    //Rejected or Missed call

                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}


