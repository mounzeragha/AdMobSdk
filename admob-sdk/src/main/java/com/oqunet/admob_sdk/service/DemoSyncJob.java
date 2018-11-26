package com.oqunet.admob_sdk.service;

import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;


public class DemoSyncJob extends Job {
    private static final String LOG_TAG = DemoSyncJob.class.getSimpleName();
    public static final String TAG = "ad_job_tag";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e(LOG_TAG, "onRunJob");
        getContext().startService(new Intent(getContext(), AdHeadService.class));
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(DemoSyncJob.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }

}
