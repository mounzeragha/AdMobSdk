package com.oqunet.mobad_sdk;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oqunet.mobad_sdk.database.AppDatabase;
import com.oqunet.mobad_sdk.database.entity.ExtraAd;
import com.oqunet.mobad_sdk.fragments.ExtraAdsFragmentDialog;


public class DisplayExtraAd extends AppCompatActivity implements ExtraAdsFragmentDialog.ShowingAdInterface {
    private static final String LOG_TAG = DisplayExtraAd.class.getSimpleName();
    private int adId;
    private ExtraAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_main_layout);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        adId = getIntent().getIntExtra("ad_id", 0);
        ad = AppDatabase.getInstance(this).getExtraAdDao().loadExtraAd(adId);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        ExtraAdsFragmentDialog extraAdsFragmentDialog = new ExtraAdsFragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extra_ad", ad);
        extraAdsFragmentDialog.setArguments(bundle);
        extraAdsFragmentDialog.show(getSupportFragmentManager(), ExtraAdsFragmentDialog.ARG_ITEM_ID);


    }



    @Override
    public void onShownAd() {
        finish();
    }



}
