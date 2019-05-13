package com.oqunet.mobad_sdk;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.evernote.android.job.JobManager;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oqunet.mobad_sdk.adapters.CarouselAdItemsListAdapter;
import com.oqunet.mobad_sdk.database.AppDatabase;
import com.oqunet.mobad_sdk.database.entity.Ad;
import com.oqunet.mobad_sdk.fragments.AdsFragmentDialog;
import com.oqunet.mobad_sdk.models.Job;
import com.oqunet.mobad_sdk.retrofit.ApiClient;
import com.oqunet.mobad_sdk.retrofit.ApiService;
import com.oqunet.mobad_sdk.retrofit.HandelErrors;
import com.oqunet.mobad_sdk.retrofit.entity.Action;
import com.oqunet.mobad_sdk.utils.Constants;
import com.oqunet.mobad_sdk.utils.MobAdUtils;
import com.oqunet.mobad_sdk.utils.ImageUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DisplayAd extends AppCompatActivity implements AdsFragmentDialog.ShowingAdInterface {
    private static final String LOG_TAG = DisplayAd.class.getSimpleName();
    private ImageView closeAd;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private View parent_view;
    private int adId;
    private Ad ad;
    List<com.oqunet.mobad_sdk.database.entity.CarouselAdItem> carouselAdItems;
    ApiClient apiClient;
    ApiService apiService;
    Call<Action> adActionCall;
    HandelErrors handelErrors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_main_layout);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        parent_view = findViewById(android.R.id.content);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        adId = getIntent().getIntExtra("ad_id", 0);
        ad = AppDatabase.getInstance(this).getAdDao().loadAd(adId);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        AdsFragmentDialog adsFragmentDialog = new AdsFragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("ad", ad);
        adsFragmentDialog.setArguments(bundle);
        adsFragmentDialog.show(getSupportFragmentManager(), AdsFragmentDialog.ARG_ITEM_ID);

        /**
        if (ad.getFormat().equals("Image")) {
            showImageAdDialog("https://" + ad.getAdvertiserImage(), ad.getAdvertiserName(),
                    ad.getAdTitle(), ad.getAdDescription(), "https://" + ad.getAdPath(),
                    ad.getButtonLink(), ad.getButtonName());

        } else if (ad.getFormat().equals("Video")) {
            showVideoAdDialog("https://" + ad.getAdvertiserImage(), ad.getAdvertiserName(),
                    ad.getAdTitle(), ad.getAdDescription(), "https://admob.azurewebsites.net/content/ad_videos/" + ad.getAdPath(), ad.getButtonLink(),
                    ad.getButtonName());
        } else if (ad.getFormat().equals("Text")) {
            showTextAdDialog("https://" + ad.getAdvertiserImage(), ad.getAdvertiserName(),
                    ad.getAdTitle(), ad.getAdDescription(), ad.getButtonLink(), ad.getButtonName());
        } else if (ad.getFormat().equals("Carousel")) {
            carouselAdItems = AppDatabase.getInstance(this).getCarouselAdItemDao().loadCarouselItems();
            showCarouselAdDialog("https://" + ad.getAdvertiserImage(), ad.getAdvertiserName(), ad.getAdTitle(), carouselAdItems);
            Toast.makeText(DisplayAd.this, "Text Carousel!", Toast.LENGTH_SHORT).show();
        }
         */

        //    sendAdAction(Constants.KEY_VIEWED);


    }

    private void showImageAdDialog(String advertiserIcon, String advertiserName, String adHeadLine,
                                   String adDescription, String adImage, final String path,
                                   String buttonName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_image_layout);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        ImageView advertiserAdImage = dialog.findViewById(R.id.ad_image);
        ImageUtil.displayRoundImage(advertiserBrandIcon, advertiserIcon, null);
        ImageUtil.displayImage(advertiserAdImage, adImage, null);
        ((TextView) dialog.findViewById(R.id.advertiser_name)).setText(advertiserName);
        ((TextView) dialog.findViewById(R.id.title)).setText(adHeadLine);
        ((TextView) dialog.findViewById(R.id.description)).setText(adDescription);
        ((TextView) dialog.findViewById(R.id.btn_cta)).setText(buttonName);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);
                dialog.hide();
                finish();
            }
        });

        (dialog.findViewById(R.id.btn_cta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAdAction(Constants.KEY_CLICKED);
                dialog.hide();
                finish();
                MobAdUtils.openWebUrlExternal(DisplayAd.this, path);
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);

            }
        });

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(DisplayAd.this, R.drawable.dialog_background_color));

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void showTextAdDialog(String advertiserIcon, String advertiserName, String adHeadLine,
                                  String adDescription, final String path, String buttonName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_text_layout);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        ImageUtil.displayRoundImage(advertiserBrandIcon, advertiserIcon, null);

        ((TextView) dialog.findViewById(R.id.advertiser_name)).setText(advertiserName);
        ((TextView) dialog.findViewById(R.id.title)).setText(adHeadLine);
        ((TextView) dialog.findViewById(R.id.description)).setText(adDescription);
        ((TextView) dialog.findViewById(R.id.btn_cta)).setText(buttonName);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);
                dialog.hide();
                finish();
            }
        });

        (dialog.findViewById(R.id.btn_cta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAdAction(Constants.KEY_CLICKED);
                dialog.hide();
                finish();
                MobAdUtils.openWebUrlExternal(DisplayAd.this, path);
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);

            }
        });

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(DisplayAd.this, R.drawable.dialog_background_color));

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void showVideoAdDialog(String advertiserIcon, String advertiserName, String adHeadLine,
                                   String adDescription, String adVideo, final String path,
                                   String buttonName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_video_layout);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        ImageUtil.displayRoundImage(advertiserBrandIcon, advertiserIcon, null);
        VideoView advertiserAdVideo = dialog.findViewById(R.id.ad_video);
         advertiserAdVideo.setVideoURI(Uri.parse(adVideo));
         advertiserAdVideo.start();


        ((TextView) dialog.findViewById(R.id.advertiser_name)).setText(advertiserName);
        ((TextView) dialog.findViewById(R.id.title)).setText(adHeadLine);
        ((TextView) dialog.findViewById(R.id.description)).setText(adDescription);
        ((TextView) dialog.findViewById(R.id.btn_cta)).setText(buttonName);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);
                dialog.hide();
                finish();
            }
        });

        (dialog.findViewById(R.id.btn_cta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAdAction(Constants.KEY_CLICKED);
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);
                dialog.hide();
                finish();
                MobAdUtils.openWebUrlExternal(DisplayAd.this, path);


            }
        });

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(DisplayAd.this, R.drawable.dialog_background_color));

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }


    private void showCarouselAdDialog(String advertiserIcon, String advertiserName, String adText,
                                      List<com.oqunet.mobad_sdk.database.entity.CarouselAdItem> carouselAdItems) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_carousel_layout);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        ImageUtil.displayRoundImage(advertiserBrandIcon, advertiserIcon, null);
        ((TextView) dialog.findViewById(R.id.advertiser_name)).setText(advertiserName);
        RecyclerView carouselAdRecyclerView = dialog.findViewById(R.id.carousel_ad_recyclerView);
        LinearLayoutManager layoutManagerForCarouselAdRecyclerView = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carouselAdRecyclerView.setLayoutManager(layoutManagerForCarouselAdRecyclerView);
        carouselAdRecyclerView.setVisibility(View.VISIBLE);

        ((TextView) dialog.findViewById(R.id.text)).setText(adText);

        CarouselAdItemsListAdapter carouselAdItemsListAdapter = new CarouselAdItemsListAdapter(this);
        carouselAdItemsListAdapter.setCarouselAdItemsList(carouselAdItems);
        carouselAdRecyclerView.setAdapter(carouselAdItemsListAdapter);
        carouselAdItemsListAdapter.setOnItemClickListener(new CarouselAdItemsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, com.oqunet.mobad_sdk.database.entity.CarouselAdItem carouselAdItem, int position) {
                sendAdAction(Constants.KEY_CLICKED);
                dialog.hide();
                finish();
                //    MobAdUtils.openWebUrlExternal(AdsActivity.this, "https://www.hm.com/lb");
                MobAdUtils.startNewActivity(DisplayAd.this, "com.oqunet.mobad");
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);

            }

        });

        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase.getInstance(DisplayAd.this).getAdDao().deleteAd(ad);
                dialog.hide();
                finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(DisplayAd.this, R.drawable.dialog_background_color));

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    public void sendAdAction(String action) {
        apiService = ApiClient.getClient().create(ApiService.class);
        handelErrors = new HandelErrors(this);
        String deviceId = MobAdUtils.getUniqueIMEIId(this);
        Log.e(LOG_TAG, " Android ID: " + deviceId);
        adActionCall = apiService.sendAdAction(deviceId, String.valueOf(adId), action);
        adActionCall.enqueue(new Callback<Action>() {
            @Override
            public void onResponse(@NonNull Call<Action> call, @NonNull Response<Action> response) {
                int code = response.code();
                Log.i("Status Code: ", String.valueOf(code));
                if (response.isSuccessful()) {
                    // 200 OK!
                    if (response.body() != null) {
                        Log.i(LOG_TAG, "Result: " + response.body().toString());
                        Action adActionResult = response.body();
                        assert adActionResult != null;
                        if (adActionResult.getStatus().equals("Success")) {
                            //    Toast.makeText(DisplayAd.this, adActionResult.getMessage(), Toast.LENGTH_SHORT).show();
                            toastMessageFloatingImage(adActionResult.getMessage());
                        }

                    }

                } else {
                    handelErrors.handleStatusCodeErrors(code, adActionCall, LOG_TAG);

                }

            }

            @Override
            public void onFailure(Call<Action> call, Throwable t) {
                handelErrors.onFailureCall(call, t, LOG_TAG);
            }

        });
    }

    private void toastMessageFloatingImage(String message) {
        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);

        //inflate view
        View customView = getLayoutInflater().inflate(R.layout.earned_coins_toast_floating_image, null);
        TextView messageText = customView.findViewById(R.id.message);
        messageText.setText(message);
        TextView appButton = customView.findViewById(R.id.app_button);
        appButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobAdUtils.startNewActivity(DisplayAd.this, "com.oqunet.mobad");
            }
        });
        toast.setView(customView);
        toast.show();
    }

    @Override
    public void onShownAd() {
        finish();
    }



}
