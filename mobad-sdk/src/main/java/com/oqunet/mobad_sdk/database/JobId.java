package com.oqunet.mobad_sdk.database;

import android.content.Context;
import android.content.SharedPreferences;


public class JobId {
    Context mContext;
    SharedPreferences mSettings;
    public static final String JOB_ID = "job_id";

    public JobId(Context context, String prefsName) {
        this.mContext = context;
        this.mSettings = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    public void setJobId(int jobId) {
        commitInt(JOB_ID, jobId);
    }

    public int getJobId() {
        return mSettings.getInt(JOB_ID, 0);
    }

    private void commitInt(String id, int value) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(id, value);
        editor.apply();
    }
}
