package com.oqunet.mobad_sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.oqunet.mobad_sdk.models.Job;
import com.oqunet.mobad_sdk.service.AdJobCreator;
import com.oqunet.mobad_sdk.service.SyncAdJob;


public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = PhoneStateReceiver.class.getSimpleName();
    int jobId;


    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Log.i(LOG_TAG, "Receiver start");
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Log.i(LOG_TAG, "Incoming Call State");
                Log.i(LOG_TAG, "Ringing State Number is -" + incomingNumber);
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.i(LOG_TAG, "Call Idle State");
            }

            PhoneStateChangeListener phoneStateChangeListener = new PhoneStateChangeListener(context);
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            assert telephonyManager != null;
            telephonyManager.listen(phoneStateChangeListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class PhoneStateChangeListener extends PhoneStateListener {
        public boolean wasRinging;
        int previousState;
        private Context context;

        private PhoneStateChangeListener(Context context) {
            this.context = context;
        }


        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(LOG_TAG, "CALL_STATE_RINGING");
                    previousState = state;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(LOG_TAG, "CALL_STATE_OFFHOOK");
                    previousState = state;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(LOG_TAG, "CALL_STATE_IDLE");
                    if ((previousState == TelephonyManager.CALL_STATE_OFFHOOK)) {
                        previousState = state;
                        // Answered Call which is ended
                        Log.i(LOG_TAG, "Answered Call which is ended");
                        startAdJob(context);
                    }
                    if ((previousState == TelephonyManager.CALL_STATE_RINGING)) {
                        previousState = state;
                        // Rejected or Missed call
                        Log.i(LOG_TAG, "Rejected or Missed call");
                        startAdJob(context);
                    }

                    break;

            }

            /**

             if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
             // Answered call which is ended
             Log.i(LOG_TAG, "CALL_STATE_OFFHOOK");
             startAdJob(context);
             } else if (state == TelephonyManager.CALL_STATE_RINGING) {
             //Rejected or Missed call
             Log.i(LOG_TAG, "CALL_STATE_RINGING");
             startAdJob(context);
             }
             */
        }
    }

    private void startAdJob(Context context) {
        JobManager.create(context).addJobCreator(new AdJobCreator());
        jobId = new JobRequest.Builder(SyncAdJob.TAG)
                .setUpdateCurrent(true)
                .startNow()
                .build()
                .schedule();
        Job.setJobId(jobId);
        Log.i("NEW JOB ID: ", String.valueOf(jobId));
    }

}


