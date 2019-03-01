package com.oqunet.admob_sdk.models;

public class Job {
    private static int jobId;

    public Job() {
    }

    public static int getJobId() {
        return jobId;
    }

    public static void setJobId(int jobId) {
        Job.jobId = jobId;
    }
}
