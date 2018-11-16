package com.oqunet.admob_sdk;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oqunet.admob_sdk.adapters.CarouselAdItemsListAdapter;
import com.oqunet.admob_sdk.models.CarouselAd;
import com.oqunet.admob_sdk.models.CarouselAdItem;
import com.oqunet.admob_sdk.models.Advertiser;
import com.oqunet.admob_sdk.service.AdHeadService;
import com.oqunet.admob_sdk.utils.AppUtils;
import com.oqunet.admob_sdk.utils.ImageUtil;

import java.util.ArrayList;


public class DisplayAd extends AppCompatActivity {
    private static final String LOG_TAG = DisplayAd.class.getSimpleName();
    private ImageView closeAd;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private View parent_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_main_layout);

        parent_view = findViewById(android.R.id.content);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        Intent intent = new Intent(DisplayAd.this, AdHeadService.class);
        stopService(intent);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        if (Advertiser.getAdvertiser().equals("adidas")) {
            showImageAdDialog("https://media.journeys.com/images/c9/1_1100.jpg", "adidas", "Be Faster", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod.", "https://sneakernews.com/wp-content/uploads/2018/01/adidas-ad-twinstrike-workshop-2.jpg");
        } else if (Advertiser.getAdvertiser().equals("real-estate")) {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.ad_video;
            showVideoAdDialog("https://pbs.twimg.com/profile_images/497813473166770176/IleP2eO8_400x400.png", "ERA Real Estate", "Finding your next home", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod.", videoPath);
        } else if (Advertiser.getAdvertiser().equals("hm")) {
            CarouselAd carouselAd = getCarouselAd();
            showCarouselAdDialog("https://pbs.twimg.com/profile_images/793885039360610304/9AfPCAm9_400x400.jpg", "H&M Lebanon", carouselAd.getText(), carouselAd.getCarouselAdItemsList());
        } else {
            CarouselAd carouselAd = getCarouselAd();
            showCarouselAdDialog("https://pbs.twimg.com/profile_images/793885039360610304/9AfPCAm9_400x400.jpg", "H&M Lebanon", carouselAd.getText(), carouselAd.getCarouselAdItemsList());
        }


    }

    private void showImageAd(String advertiserIcon, String advertiserName, String adHeadLine, String adDescription, String adImage) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.ad_image_layout, null);
        ImageView advertiserBrandIcon = view.findViewById(R.id.advertiser_icon);
        ImageView advertiserAdImage = view.findViewById(R.id.ad_image);
        ImageUtil.displayImage(advertiserBrandIcon, advertiserIcon, null);
        ImageUtil.displayImage(advertiserAdImage, adImage, null);
        ((TextView) view.findViewById(R.id.advertiser_name)).setText(advertiserName);
        ((TextView) view.findViewById(R.id.title)).setText(adHeadLine);
        ((TextView) view.findViewById(R.id.description)).setText(adDescription);
        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
                finish();
            }
        });

        (view.findViewById(R.id.btn_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.hide();
                finish();
                AppUtils.openWebUrlExternal(DisplayAd.this, "https://www.mena.adidas.com/");

            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }

    private void showVideoAd(String advertiserIcon, String advertiserName, String adHeadLine, String adDescription, String adVideo) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.ad_video_layout, null);
        ImageView advertiserBrandIcon = view.findViewById(R.id.advertiser_icon);
        final VideoView advertiserAdVideo = view.findViewById(R.id.ad_video);
        ImageUtil.displayImage(advertiserBrandIcon, advertiserIcon, null);
        advertiserAdVideo.setVideoURI(Uri.parse(adVideo));
        advertiserAdVideo.start();

        ((TextView) view.findViewById(R.id.advertiser_name)).setText(advertiserName);
        ((TextView) view.findViewById(R.id.title)).setText(adHeadLine);
        ((TextView) view.findViewById(R.id.description)).setText(adDescription);
        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
                finish();
            }
        });

        (view.findViewById(R.id.btn_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.hide();
                finish();
                AppUtils.openWebUrlExternal(DisplayAd.this, "https://www.realestate.com.au/buy");

            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }

    private void showCarouselAd(String advertiserIcon, String advertiserName, String adText, ArrayList<CarouselAdItem> carouselAdItems) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.ad_carousel_layout, null);
        ImageView advertiserBrandIcon = view.findViewById(R.id.advertiser_icon);
        ImageUtil.displayImage(advertiserBrandIcon, advertiserIcon, null);
        ((TextView) view.findViewById(R.id.advertiser_name)).setText(advertiserName);
        RecyclerView carouselAdRecyclerView = view.findViewById(R.id.carousel_ad_recyclerView);
        LinearLayoutManager layoutManagerForCarouselAdRecyclerView = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carouselAdRecyclerView.setLayoutManager(layoutManagerForCarouselAdRecyclerView);
        carouselAdRecyclerView.setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.text)).setText(adText);

        CarouselAdItemsListAdapter carouselAdItemsListAdapter = new CarouselAdItemsListAdapter(this);
        carouselAdItemsListAdapter.setCarouselAdItemsList(carouselAdItems);
        carouselAdRecyclerView.setAdapter(carouselAdItemsListAdapter);
        carouselAdItemsListAdapter.setOnItemClickListener(new CarouselAdItemsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CarouselAdItem carouselAdItem, int position) {
                mBottomSheetDialog.hide();
                finish();
                //    AppUtils.openWebUrlExternal(AdsActivity.this, "https://www.hm.com/lb");
                AppUtils.startNewActivity(DisplayAd.this, "com.oqunet.ipaycards");

            }

        });

        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
                finish();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }

    private void showImageAdDialog(String advertiserIcon, String advertiserName, String adHeadLine, String adDescription, String adImage) {
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
        ImageUtil.displayImage(advertiserBrandIcon, advertiserIcon, null);
        ImageUtil.displayImage(advertiserAdImage, adImage, null);
        ((TextView) dialog.findViewById(R.id.advertiser_name)).setText(advertiserName);
        ((TextView) dialog.findViewById(R.id.title)).setText(adHeadLine);
        ((TextView) dialog.findViewById(R.id.description)).setText(adDescription);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                finish();
            }
        });

        (dialog.findViewById(R.id.btn_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                finish();
                AppUtils.openWebUrlExternal(DisplayAd.this, "https://www.mena.adidas.com/");

            }
        });


        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(DisplayAd.this, R.drawable.dialog_background_color));


        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void showVideoAdDialog(String advertiserIcon, String advertiserName, String adHeadLine, String adDescription, String adVideo) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_video_layout);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        ImageUtil.displayImage(advertiserBrandIcon, advertiserIcon, null);
        VideoView advertiserAdVideo = dialog.findViewById(R.id.ad_video);
        advertiserAdVideo.setVideoURI(Uri.parse(adVideo));
        advertiserAdVideo.start();

        ((TextView) dialog.findViewById(R.id.advertiser_name)).setText(advertiserName);
        ((TextView) dialog.findViewById(R.id.title)).setText(adHeadLine);
        ((TextView) dialog.findViewById(R.id.description)).setText(adDescription);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                finish();
            }
        });

        (dialog.findViewById(R.id.btn_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                finish();
                AppUtils.openWebUrlExternal(DisplayAd.this, "https://www.realestate.com.au/buy");

            }
        });

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(DisplayAd.this, R.drawable.dialog_background_color));

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void showCarouselAdDialog(String advertiserIcon, String advertiserName, String adText, ArrayList<CarouselAdItem> carouselAdItems) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_carousel_layout);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        ImageUtil.displayImage(advertiserBrandIcon, advertiserIcon, null);
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
            public void onItemClick(View view, CarouselAdItem carouselAdItem, int position) {
                dialog.hide();
                finish();
                //    AppUtils.openWebUrlExternal(AdsActivity.this, "https://www.hm.com/lb");
                AppUtils.startNewActivity(DisplayAd.this, "com.oqunet.ipaycards");

            }

        });

        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(DisplayAd.this, R.drawable.dialog_background_color));

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private CarouselAd getCarouselAd() {
        CarouselAd carouselAd = new CarouselAd();
        carouselAd.setText("Let's make this holiday magical with H&M! Discover the new Holiday collection for Ladies, Men and Kids!");

        ArrayList<CarouselAdItem> carouselAdItems = new ArrayList<>();
        CarouselAdItem carouselAdItem1 = new CarouselAdItem();
        carouselAdItem1.setImage("http://photo.mensfashionforless.com/m/h-m-dark-green-hoodie-jacket.jpg");
        carouselAdItem1.setHeadline("Relaxed & Easy Glamour this season!");
        carouselAdItem1.setDescription("Shop the effortlessly elegant pieces!");

        CarouselAdItem carouselAdItem2 = new CarouselAdItem();
        carouselAdItem2.setImage("http://media.fashionblog.it/3/3cd/hm-abiti-party-1.jpg");
        carouselAdItem2.setHeadline("Relaxed & Easy Glamour this season!");
        carouselAdItem2.setDescription("Shop the effortlessly elegant pieces!");

        CarouselAdItem carouselAdItem3 = new CarouselAdItem();
        carouselAdItem3.setImage("https://www.fashiongonerogue.com/wp-content/uploads/2018/03/HM-Colorful-Spring-2018-Outfits01.jpg");
        carouselAdItem3.setHeadline("Relaxed & Easy Glamour this season!");
        carouselAdItem3.setDescription("Shop the effortlessly elegant pieces!");

        CarouselAdItem carouselAdItem4 = new CarouselAdItem();
        carouselAdItem4.setImage("https://i.dailymail.co.uk/i/pix/2016/02/17/23/3153B0DF00000578-0-image-a-2_1455753156010.jpg");
        carouselAdItem4.setHeadline("Relaxed & Easy Glamour this season!");
        carouselAdItem4.setDescription("Shop the effortlessly elegant pieces!");

        CarouselAdItem carouselAdItem5 = new CarouselAdItem();
        carouselAdItem5.setImage("https://www.parisinfo.com/var/otcp/sites/images/elements-communs/el%C3%A9ments-sugar/shopping/h-m-%7C-630x405-%7C-%C2%A9-dr/10880199-1-fre-FR/H-M-%7C-630x405-%7C-%C2%A9-DR.jpg");
        carouselAdItem5.setHeadline("Relaxed & Easy Glamour this season!");
        carouselAdItem5.setDescription("Shop the effortlessly elegant pieces!");

        carouselAdItems.add(carouselAdItem1);
        carouselAdItems.add(carouselAdItem2);
        carouselAdItems.add(carouselAdItem3);
        carouselAdItems.add(carouselAdItem4);
        carouselAdItems.add(carouselAdItem5);

        carouselAd.setCarouselAdItemsList(carouselAdItems);

        return carouselAd;

    }
}
