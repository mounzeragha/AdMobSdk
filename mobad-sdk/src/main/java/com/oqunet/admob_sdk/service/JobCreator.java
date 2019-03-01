package com.oqunet.admob_sdk.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

public class JobCreator implements com.evernote.android.job.JobCreator {
    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncJob.TAG:
                return new SyncJob();
            default:
                return null;
        }
    }
}
