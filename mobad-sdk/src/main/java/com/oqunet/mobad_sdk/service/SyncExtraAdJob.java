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
import com.oqunet.mobad_sdk.DisplayExtraAd;
import com.oqunet.mobad_sdk.R;
import com.oqunet.mobad_sdk.database.AppDatabase;
import com.oqunet.mobad_sdk.database.entity.ExtraAd;
import com.oqunet.mobad_sdk.utils.ImageUtil;
import com.oqunet.mobad_sdk.utils.ViewAnimation;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


public class SyncExtraAdJob extends Job {
    private static final String LOG_TAG = SyncExtraAdJob.class.getSimpleName();
    public static final String TAG = "extra_ad_job_tag";
    private WindowManager windowManager;
    private RelativeLayout adHeadView, removeView;

    private ImageView adHeadImage, removeAdHeadImage;

    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;
    List<ExtraAd> extraAds;


    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e(LOG_TAG, "onRunJob");
        extraAds = AppDatabase.getInstance(getContext()).getExtraAdDao().loadExtraAds();
        if (!extraAds.isEmpty()) {
            final ExtraAd extraAd = extraAds.get(0);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    startDisplayAd(extraAd);
                }
            });
        }
        return Result.SUCCESS;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void startDisplayAd(final ExtraAd ad) {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        windowManager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        removeView = (RelativeLayout) inflater.inflate(R.layout.remove_ad, null);

        final WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        removeView.setVisibility(View.GONE);
        removeAdHeadImage = (ImageView) removeView.findViewById(R.id.remove_img);

        windowManager.addView(removeView, paramRemove);

        adHeadView = (RelativeLayout) inflater.inflate(R.layout.ad_head, null);
        adHeadImage = (ImageView) adHeadView.findViewById(R.id.ad_head_img);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
        ImageUtil.displayRoundImage(adHeadImage, "https://" + ad.getAdPoster(), null);

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
        params.y = 200;

        windowManager.addView(adHeadView, params);

        playAdHeadAnimation();

        adHeadView.setOnTouchListener(new View.OnTouchListener() {
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
                    removeView.setVisibility(View.VISIBLE);
                    adHeadLongClick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) adHeadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 200);

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

                        if(isLongclick){
                            int x_bound_left = szWindow.x / 2 - (int)(remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 +  (int)(remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int)(remove_img_height * 1.5);

                            if((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top){
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight() ));

                                if(removeAdHeadImage.getLayoutParams().height == remove_img_height){
                                    removeAdHeadImage.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeAdHeadImage.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - adHeadView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - adHeadView.getHeight())) / 2 ;

                                windowManager.updateViewLayout(adHeadView, layoutParams);
                                break;
                            }else{
                                inBounded = false;
                                removeAdHeadImage.getLayoutParams().height = remove_img_height;
                                removeAdHeadImage.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

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
                        removeView.setVisibility(View.GONE);
                        removeAdHeadImage.getLayoutParams().height = remove_img_height;
                        removeAdHeadImage.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if(inBounded){
                            if(adHeadView != null){
                                windowManager.removeView(adHeadView);
                            }
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if(Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5){
                            time_end = System.currentTimeMillis();
                            if((time_end - time_start) < 300){
                                adHeadClick(ad);
                            }
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int BarHeight =  getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (adHeadView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (adHeadView.getHeight() + BarHeight );
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        resetPosition(x_cord);

                        break;
                    default:
                        Log.d(LOG_TAG, "adHeadView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }
                return true;
            }
        });
    }

    private void resetPosition(int x_cord_now) {
        if(x_cord_now <= szWindow.x / 2){
            isLeft = true;
            moveToLeft(x_cord_now);

        } else {
            isLeft = false;
            moveToRight(x_cord_now);

        }

    }
    private void moveToLeft(final int x_cord_now){
        final int x = szWindow.x - x_cord_now;

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) adHeadView.getLayoutParams();
            public void onTick(long t) {
                long step = (500 - t)/5;
                mParams.x = 0 - (int)(double)bounceValue(step, x );
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
    private  void moveToRight(final int x_cord_now){
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) adHeadView.getLayoutParams();
            public void onTick(long t) {
                long step = (500 - t)/5;
                mParams.x = szWindow.x + (int)(double)bounceValue(step, x_cord_now) - adHeadView.getWidth();
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

    private double bounceValue(long step, long scale){
        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
        return value;
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private void adHeadClick(ExtraAd ad){
        if(adHeadView != null){
            windowManager.removeView(adHeadView);
        }

        if(removeView != null){
            windowManager.removeView(removeView);
        }
        Intent intent = new Intent(getContext(), DisplayExtraAd.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt("ad_id", ad.getAdId());
        intent.putExtras(bundle);
        getContext().startActivity(intent);

    }

    private void adHeadLongClick(){
        Log.d(LOG_TAG, "Into AdHeadService.adhead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }



    public static void scheduleJob() {
        new JobRequest.Builder(SyncExtraAdJob.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }


    private void playAdHeadAnimation() {
        ViewAnimation.showIn(adHeadView);
    }


}
