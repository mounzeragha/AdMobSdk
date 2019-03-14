package com.oqunet.mobad_sdk.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static final String LOG_TAG = ApiClient.class.getSimpleName();
    public static final String BASE_URL = "https://admob.azurewebsites.net/api/mobile/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }


}
