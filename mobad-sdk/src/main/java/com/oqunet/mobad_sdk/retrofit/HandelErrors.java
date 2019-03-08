package com.oqunet.mobad_sdk.retrofit;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.oqunet.mobad_sdk.utils.MobAdUtils;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.HttpException;

public class HandelErrors {

    private Context context;
    MobAdUtils appUtil;

    public HandelErrors(Context context) {
        this.context = context;
        appUtil = new MobAdUtils(context);

    }

    public void onFailureCall(Call call, Throwable t, String logTag) {
        // Log error here since request failed
        Log.e(logTag, t.toString());
        if (t instanceof Exception) {
            if (t instanceof HttpException) {
                // handle http errors
                if (!call.isCanceled()) {
                    Log.i(logTag, "HttpException Error");
                }
            } else if (t instanceof SocketTimeoutException) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "SocketTimeoutException: Timeout has occurred on a socket read or accept. You may wish to try again at a later time.");
                }
            } else if (t instanceof NetworkErrorException) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Network Error: A Network Connection error has occurred.");
                }
            } else if (t instanceof SocketException) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "SocketException: Thrown to indicate that there is an error creating or accessing a Socket.");
                }
            } else if (t instanceof NumberFormatException) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "NumberFormatException: Empty String! Unexpected data type from the server that caused Format Exception! Please try later.");
                }
            } else if (t instanceof JsonSyntaxException) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "JsonSyntaxException: Unexpected data type from the server that caused Format Exception! Please try later.");
                }
            } else {
                if (!call.isCanceled()) {
                    Log.i(logTag, "UnKnown Exception: UnKnown Exception.");
                }
            }
        } else {
            // handle connection errors
            if (!call.isCanceled()) {
                Log.i(logTag, "Please check your connection and try again.");
            }
        }
    }

    public void handleStatusCodeErrors(int code, Call call, String logTag) {
        if (appUtil.isNetworkAvailable()) {
            if (code == 404) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Non Success Result: 404 Not Found!");

                }
            } else if (code == 204) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Non Success Result: 204 No Content!");

                }
            } else if (code == 400) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Non Success Result: 400 Bad Request!");

                }
            } else if (code == 500) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Non Success Result: 500 Internal Server Error!");

                }
            } else if (code == 401) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Non Success Result: 401 Unauthorized!");

                }
            } else if (code == 405) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Non Success Result: Not Allowed! Out of Stock!");

                }
            } else if (code == 429) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Too Many Requests!");

                }
            } else if (code == 503) {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Service Unavailable!");

                }
            } else if (code == 504) {
                if (!call.isCanceled()) {

                }
            } else {
                if (!call.isCanceled()) {
                    Log.i(logTag, "Non Success Result: Unknown Error!");

                }
            }

        } else {
            Log.i(logTag, "Please check your connection and try again.");
        }

    }






}
