package com.oqunet.mobad_sdk.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oqunet.mobad_sdk.DisplayAd;
import com.oqunet.mobad_sdk.R;
import com.oqunet.mobad_sdk.database.AppDatabase;
import com.oqunet.mobad_sdk.database.entity.CarouselAdItem;
import com.oqunet.mobad_sdk.retrofit.ApiClient;
import com.oqunet.mobad_sdk.retrofit.ApiService;
import com.oqunet.mobad_sdk.retrofit.HandelErrors;
import com.oqunet.mobad_sdk.retrofit.entity.Ad;
import com.oqunet.mobad_sdk.utils.ImageUtil;
import com.oqunet.mobad_sdk.utils.MobAdUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


public class SyncJobMultipleAds extends Job {
    private static final String LOG_TAG = SyncJobMultipleAds.class.getSimpleName();
    public static final String TAG = "multiple_ad_job_tag";
    private WindowManager windowManager;
    private RelativeLayout removeView;

    private ImageView removeAdHeadImage;

    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;
    ApiClient apiClient;
    ApiService apiService;
    Call<Ad> adCall;
    HandelErrors handelErrors;
    LayoutInflater inflater;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e(LOG_TAG, "onRunJob");
        getAd();
        return Result.SUCCESS;
    }

    private void resetPosition(final RelativeLayout adHeadView, int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true;
            moveToLeft(adHeadView, x_cord_now);

        } else {
            isLeft = false;
            moveToRight(adHeadView, x_cord_now);

        }

    }

    private void moveToLeft(final RelativeLayout adHeadView, final int x_cord_now) {
        final int x = szWindow.x - x_cord_now;

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) adHeadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = 0 - (int) (double) bounceValue(step, x);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (adHeadView.isAttachedToWindow())
                        windowManager.updateViewLayout(adHeadView, mParams);
                }
            }

            public void onFinish() {
                mParams.x = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (adHeadView.isAttachedToWindow())
                        windowManager.updateViewLayout(adHeadView, mParams);
                }
            }
        }.start();
    }

    private void moveToRight(final RelativeLayout adHeadView, final int x_cord_now) {
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) adHeadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - adHeadView.getWidth();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (adHeadView.isAttachedToWindow())
                        windowManager.updateViewLayout(adHeadView, mParams);
                }
            }

            public void onFinish() {
                mParams.x = szWindow.x - adHeadView.getWidth();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (adHeadView.isAttachedToWindow())
                        windowManager.updateViewLayout(adHeadView, mParams);
                }
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
        return value;
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private void adHeadClick(final RelativeLayout adHeadView, int adId) {
        if (adHeadView != null) {
            windowManager.removeView(adHeadView);
        }

    //    if (removeView != null) {
    //        windowManager.removeView(removeView);
    //    }
        Intent intent = new Intent(getContext(), DisplayAd.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt("ad_id", adId);
        intent.putExtras(bundle);
        getContext().startActivity(intent);

    }

    private void adHeadLongClick() {
        Log.d(LOG_TAG, "Into AdHeadService.adhead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }


    public static void scheduleJob() {
        new JobRequest.Builder(SyncJobMultipleAds.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }

    private void getAd() {

        apiService = ApiClient.getClient().create(ApiService.class);
        handelErrors = new HandelErrors(getContext());
        String deviceId = MobAdUtils.getDeviceID(getContext());
        Log.e(LOG_TAG, " Android ID: " + deviceId);

        adCall = apiService.getAd(deviceId);
        adCall.enqueue(new Callback<Ad>() {
            @Override
            public void onResponse(@NonNull Call<Ad> call, @NonNull Response<Ad> response) {
                int code = response.code();
                Log.i("Status Code: ", String.valueOf(code));
                if (response.isSuccessful()) {
                    // 200 OK!
                    if (response.body() != null) {
                        Log.i(LOG_TAG, "Result: " + response.body().toString());
                        final Ad adRequested = response.body();
                        if (adRequested != null) {

                            com.oqunet.mobad_sdk.database.entity.Ad adDatabase = AppDatabase.getInstance(getContext()).getAdDao().loadAd(adRequested.getAdId());
                            if (adDatabase == null) {
                                final com.oqunet.mobad_sdk.database.entity.Ad newAd = new com.oqunet.mobad_sdk.database.entity.Ad();
                                newAd.setAdvertiserName(adRequested.getAdvertiserName());
                                newAd.setAdvertiserImage(adRequested.getAdvertiserImage());
                                newAd.setFormat(adRequested.getFormat());
                                newAd.setAdTitle(adRequested.getAdTitle());
                                newAd.setAdDescription(adRequested.getAdDescription());
                                newAd.setAdPath(adRequested.getAdPath());
                                newAd.setAdId(adRequested.getAdId());
                                newAd.setButtonName(adRequested.getButtonName());
                                newAd.setButtonLink(adRequested.getButtonLink());
                                newAd.setButtonDestination(adRequested.getButtonDestination());
                                AppDatabase.getInstance(getContext()).getAdDao().insertAd(newAd);

                                if (adRequested.getFormat().equals("Carousel")) {

                                    for (int c = 0; c < adRequested.getCarouselAdItems().size(); c++) {
                                        CarouselAdItem carouselAdItem = new CarouselAdItem();
                                        carouselAdItem.setAdId(adRequested.getAdId());
                                        carouselAdItem.setTitle(adRequested.getCarouselAdItems().get(c).getTitle());
                                        carouselAdItem.setImage(adRequested.getCarouselAdItems().get(c).getImage());
                                        carouselAdItem.setDescription(adRequested.getCarouselAdItems().get(c).getDescription());
                                        carouselAdItem.setButtonName(adRequested.getCarouselAdItems().get(c).getButtonName());
                                        carouselAdItem.setButtonLink(adRequested.getCarouselAdItems().get(c).getButtonLink());
                                        carouselAdItem.setButtonDestination(adRequested.getCarouselAdItems().get(c).getButtonDestination());
                                        AppDatabase.getInstance(getContext()).getCarouselAdItemDao().insertCarouselItem(carouselAdItem);

                                    }

                                }

                                Log.i("New Ad: ", newAd.toString());

                            }

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    displayAds();
                                }
                            });

                        }

                    }

                } else {
                    handelErrors.handleStatusCodeErrors(code, adCall, LOG_TAG);

                }

            }

            @Override
            public void onFailure(Call<Ad> call, Throwable t) {
                handelErrors.onFailureCall(call, t, LOG_TAG);
            }

        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void displayAds() {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);

        inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        /**
        assert inflater != null;
        removeView = (RelativeLayout) inflater.inflate(R.layout.remove_ad, null);

        final WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.BOTTOM | Gravity.CENTER;

        removeView.setVisibility(View.GONE);
        removeAdHeadImage = (ImageView) removeView.findViewById(R.id.remove_img);

        windowManager.addView(removeView, paramRemove);
         */

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        List<com.oqunet.mobad_sdk.database.entity.Ad> adsList = AppDatabase.getInstance(getContext()).getAdDao().loadAds();
        Log.i("ADS LIST SIZE: ", String.valueOf(adsList.size()));
        RelativeLayout[] adHeadView = new RelativeLayout[adsList.size()];
        for (int i = 0; i < adHeadView.length; i++) {
            com.oqunet.mobad_sdk.database.entity.Ad ad = adsList.get(i);
            assert inflater != null;
            adHeadView[i] = (RelativeLayout) inflater.inflate(R.layout.ad_head, null);
            ImageView adHeadImage = (ImageView) adHeadView[i].findViewById(R.id.ad_head_img);
            ImageUtil.displayRoundImage(adHeadImage, "http://" + ad.getAdvertiserImage(), null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                windowManager.getDefaultDisplay().getSize(szWindow);
            } else {
                int w = windowManager.getDefaultDisplay().getWidth();
                int h = windowManager.getDefaultDisplay().getHeight();
                szWindow.set(w, h);
            }

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 0;
            if (i == 0) {
                params.y = 200;
            } else if ( i == 1) {
                params.y = 300;
            } else if ( i == 2) {
                params.y = 400;
            } else if ( i == 3) {
                params.y = 500;
            } else {
                params.y = 600;
            }

            windowManager.addView(adHeadView[i], params);
            adHeadView[i].setOnTouchListener(adTouchListener);
            adHeadView[i].setId(ad.getAdId());


        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener adTouchListener = new View.OnTouchListener() {
        long time_start = 0, time_end = 0;
        boolean isLongclick = false, inBounded = false;
        int remove_img_width = 0, remove_img_height = 0;

        Handler handler_longClick = new Handler();
        Runnable runnable_longClick = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.d(LOG_TAG, "Into runnable_longClick");

                isLongclick = true;
            //    removeView.setVisibility(View.VISIBLE);
                adHeadLongClick();
            }
        };


        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (view instanceof RelativeLayout) {
                RelativeLayout adHeadView = (RelativeLayout) view;
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) adHeadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 200);

                        createRemoveView();

                        remove_img_width = removeAdHeadImage.getLayoutParams().width;
                        remove_img_height = removeAdHeadImage.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongclick) {
                            int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

                                if (removeAdHeadImage.getLayoutParams().height == remove_img_height) {
                                    removeAdHeadImage.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeAdHeadImage.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - adHeadView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - adHeadView.getHeight())) / 2;

                                windowManager.updateViewLayout(adHeadView, layoutParams);
                                break;
                            } else {
                                inBounded = false;
                                removeAdHeadImage.getLayoutParams().height = remove_img_height;
                                removeAdHeadImage.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                windowManager.updateViewLayout(removeView, param_remove);
                            }

                        }

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(adHeadView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        isLongclick = false;
                    //    removeView.setVisibility(View.GONE);
                        windowManager.removeView(removeView);
                        removeAdHeadImage.getLayoutParams().height = remove_img_height;
                        removeAdHeadImage.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if (inBounded) {
                            windowManager.removeView(adHeadView);
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300) {
                                int adId = adHeadView.getId();
                                   adHeadClick(adHeadView, adId);

                            }
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int BarHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (adHeadView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (adHeadView.getHeight() + BarHeight);
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        resetPosition(adHeadView, x_cord);

                        break;
                    default:
                        Log.d(LOG_TAG, "adHeadView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }

            }
            return true;
        }
    };

    private void createRemoveView() {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
    //    windowManager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);

    //    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        removeView = (RelativeLayout) inflater.inflate(R.layout.remove_ad, null);

        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.BOTTOM | Gravity.CENTER;

        removeView.setVisibility(View.VISIBLE);
        removeAdHeadImage = (ImageView) removeView.findViewById(R.id.remove_img);

        windowManager.addView(removeView, paramRemove);
    }


}
