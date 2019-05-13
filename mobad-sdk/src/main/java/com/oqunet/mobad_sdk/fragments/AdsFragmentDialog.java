package com.oqunet.mobad_sdk.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.oqunet.mobad_sdk.R;
import com.oqunet.mobad_sdk.adapters.CarouselAdItemsListAdapter;
import com.oqunet.mobad_sdk.database.AppDatabase;
import com.oqunet.mobad_sdk.database.entity.Ad;
import com.oqunet.mobad_sdk.database.entity.CarouselAdItem;
import com.oqunet.mobad_sdk.retrofit.ApiClient;
import com.oqunet.mobad_sdk.retrofit.ApiService;
import com.oqunet.mobad_sdk.retrofit.HandelErrors;
import com.oqunet.mobad_sdk.retrofit.entity.Action;
import com.oqunet.mobad_sdk.retrofit.entity.AdServiceSetting;
import com.oqunet.mobad_sdk.utils.Constants;
import com.oqunet.mobad_sdk.utils.ImageUtil;
import com.oqunet.mobad_sdk.utils.MobAdUtils;
import com.oqunet.mobad_sdk.utils.ViewAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdsFragmentDialog extends DialogFragment {
    private static final String LOG_TAG = AdsFragmentDialog.class.getSimpleName();
    public static final String ARG_ITEM_ID = "image_ad";
    ImageView advertiserBrandIcon, adImage;
    TextView advertiserName, adTitle, adDescription, earnedCoinsMessage;
    TextView ctaButton;
    ImageButton closeButton;
    CardView earnedCoinsLayout;
    ProgressBar progressBarForVideo;
    Ad ad;
    ShowingAdInterface showingAdInterface;
    ApiService apiService;
    Call<Action> adActionCall;
    Call<AdServiceSetting> deactivateAdServiceCall;
    HandelErrors handelErrors;
    private Runnable runnable = null;
    private Handler handler = new Handler();
    private VideoView videoView;
    List<CarouselAdItem> carouselAdItems = new ArrayList<CarouselAdItem>();
    boolean playedAll = false;
    private int mCurrentPosition = 0;
    int seconds = 6;
    int timeCounter;
    Timer videoTimer;
    MediaController controller;
    TextView videoDuration;
    private static CountDownTimer countDownTimer;
    FrameLayout mediaControllerLayout;
    ImageButton menuButton;


    public AdsFragmentDialog() {
        // Required empty public constructor
    }

    public interface ShowingAdInterface {
        void onShownAd();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof ShowingAdInterface) {
            showingAdInterface = (ShowingAdInterface) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ShowingAdInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        assert bundle != null;
        ad = bundle.getParcelable("ad");

    }


    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // set the layout for the ad dialog
        if (ad != null) {
            if (ad.getFormat().equals("Image")) {
                dialog.setContentView(R.layout.ad_image_layout);
                initializeImageAdViews(dialog);
                setImageAdDataAndListeners();
                sendAdAction(Constants.KEY_VIEWED);
            } else if (ad.getFormat().equals("Video")) {
                dialog.setContentView(R.layout.ad_video_layout);
                initializeVideoAdViews(dialog);
                setVideoAdDataAndListeners();
            } else if (ad.getFormat().equals("Text")) {
                dialog.setContentView(R.layout.ad_text_layout);
                initializeTextAdViews(dialog);
                setTextAdDataAndListeners();
                sendAdAction(Constants.KEY_VIEWED);
            } else if (ad.getFormat().equals("Carousel")) {
                dialog.setContentView(R.layout.ad_carousel_layout);
                initializeCarouselAdViews(dialog);
                sendAdAction(Constants.KEY_VIEWED);
            }
        }

        //    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dialog_background_color));

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }


    private void initializeImageAdViews(Dialog dialog) {
        advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        adImage = dialog.findViewById(R.id.ad_image);
        advertiserName = dialog.findViewById(R.id.advertiser_name);
        adTitle = dialog.findViewById(R.id.title);
        adDescription = dialog.findViewById(R.id.description);
        ctaButton = dialog.findViewById(R.id.btn_cta);
        closeButton = dialog.findViewById(R.id.bt_close);
        menuButton = dialog.findViewById(R.id.menu_button);
        earnedCoinsLayout = dialog.findViewById(R.id.earned_coins_layout);
        earnedCoinsMessage = dialog.findViewById(R.id.message);
        earnedCoinsLayout.setVisibility(View.INVISIBLE);
    }

    private void initializeVideoAdViews(Dialog dialog) {
        advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        advertiserName = dialog.findViewById(R.id.advertiser_name);
        adTitle = dialog.findViewById(R.id.title);
        adDescription = dialog.findViewById(R.id.description);
        videoView = dialog.findViewById(R.id.ad_video);
        ctaButton = dialog.findViewById(R.id.btn_cta);
        closeButton = dialog.findViewById(R.id.bt_close);
        earnedCoinsLayout = dialog.findViewById(R.id.earned_coins_layout);
        earnedCoinsMessage = dialog.findViewById(R.id.message);
        progressBarForVideo = dialog.findViewById(R.id.progress_bar);
        videoDuration = dialog.findViewById(R.id.duration);
        mediaControllerLayout = dialog.findViewById(R.id.media_controller_layout);
        menuButton = dialog.findViewById(R.id.menu_button);
        earnedCoinsLayout.setVisibility(View.INVISIBLE);
        progressBarForVideo.setVisibility(View.VISIBLE);
        videoDuration.setVisibility(View.INVISIBLE);
    }

    private void initializeTextAdViews(Dialog dialog) {
        advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        advertiserName = dialog.findViewById(R.id.advertiser_name);
        adTitle = dialog.findViewById(R.id.title);
        adDescription = dialog.findViewById(R.id.description);
        ctaButton = dialog.findViewById(R.id.btn_cta);
        closeButton = dialog.findViewById(R.id.bt_close);
        menuButton = dialog.findViewById(R.id.menu_button);
        earnedCoinsLayout = dialog.findViewById(R.id.earned_coins_layout);
        earnedCoinsMessage = dialog.findViewById(R.id.message);
        earnedCoinsLayout.setVisibility(View.INVISIBLE);
    }

    private void initializeCarouselAdViews(Dialog dialog) {
        advertiserBrandIcon = dialog.findViewById(R.id.advertiser_icon);
        advertiserName = dialog.findViewById(R.id.advertiser_name);
        adTitle = dialog.findViewById(R.id.text);
        ctaButton = dialog.findViewById(R.id.btn_cta);
        closeButton = dialog.findViewById(R.id.bt_close);
        menuButton = dialog.findViewById(R.id.menu_button);
        earnedCoinsLayout = dialog.findViewById(R.id.earned_coins_layout);
        earnedCoinsMessage = dialog.findViewById(R.id.message);
        earnedCoinsLayout.setVisibility(View.INVISIBLE);
        earnedCoinsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobAdUtils.startNewActivity(getActivity(), "com.oqunet.mobad");
            }
        });

        RecyclerView carouselAdRecyclerView = dialog.findViewById(R.id.carousel_ad_recyclerView);
        LinearLayoutManager layoutManagerForCarouselAdRecyclerView = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        carouselAdRecyclerView.setLayoutManager(layoutManagerForCarouselAdRecyclerView);
        carouselAdRecyclerView.setVisibility(View.VISIBLE);

        if (ad != null) {
            ImageUtil.displayRoundImage(advertiserBrandIcon, "https://" + ad.getAdPoster(), null);
            advertiserName.setText(ad.getAdvertiserName());
            adTitle.setText(ad.getAdTitle());

            List<CarouselAdItem> allCarouselAdItems = AppDatabase.getInstance(getActivity()).getCarouselAdItemDao().loadCarouselItems();
            for (CarouselAdItem carouselAdItem : allCarouselAdItems) {
                if (carouselAdItem.getAdId() == ad.getAdId()) {
                    carouselAdItems.add(carouselAdItem);
                } else {
                    break;
                }
            }
        }

        CarouselAdItemsListAdapter carouselAdItemsListAdapter = new CarouselAdItemsListAdapter(getActivity());
        carouselAdItemsListAdapter.setCarouselAdItemsList(carouselAdItems);
        carouselAdRecyclerView.setAdapter(carouselAdItemsListAdapter);
        carouselAdItemsListAdapter.setOnItemClickListener(new CarouselAdItemsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, com.oqunet.mobad_sdk.database.entity.CarouselAdItem carouselAdItem, int position) {
                sendAdAction(Constants.KEY_CLICKED);
                dismiss();
                MobAdUtils.openWebUrlExternal(getActivity(), carouselAdItem.getButtonLink());


            }

        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_deactivate) {
                            deactivateAdService();
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.menu);
                popupMenu.show();
            }
        });
    }


    private void setImageAdDataAndListeners() {
        if (ad != null) {
            ImageUtil.displayRoundImage(advertiserBrandIcon, "https://" + ad.getAdPoster(), null);
            ImageUtil.displayImage(adImage, "https://" + ad.getAdPath(), null);
            advertiserName.setText(ad.getAdvertiserName());
            adTitle.setText(ad.getAdTitle());
            adDescription.setText(ad.getAdDescription());
            ctaButton.setText(ad.getButtonName());

            ctaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendAdAction(Constants.KEY_CLICKED);
                    dismiss();
                    MobAdUtils.openWebUrlExternal(getActivity(), ad.getButtonLink());
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            earnedCoinsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobAdUtils.startNewActivity(getActivity(), "com.oqunet.mobad");
                }
            });

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.action_deactivate) {
                                deactivateAdService();
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.menu);
                    popupMenu.show();
                }
            });

        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setVideoAdDataAndListeners() {
        if (ad != null) {
            ImageUtil.displayRoundImage(advertiserBrandIcon, "https://" + ad.getAdPoster(), null);
            advertiserName.setText(ad.getAdvertiserName());
            adTitle.setText(ad.getAdTitle());
            adDescription.setText(ad.getAdDescription());

            videoView.setVideoPath("https://admob.azurewebsites.net/content/ad_videos/" + ad.getAdPath());
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(false);
                    Log.i(LOG_TAG, "Duration = " + videoView.getDuration());

                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                            // Set up the media controller widget and attach it to the video view.
                            controller = new MediaController(getActivity());
                            videoView.setMediaController(controller);
                            controller.setAnchorView(videoView);
                            ((ViewGroup) controller.getParent()).removeView(controller);
                            mediaControllerLayout.addView(controller);
                            mediaControllerLayout.setVisibility(View.VISIBLE);
                            controller.setEnabled(false);

                        }
                    });

                }

            });
            videoView.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Log.i(LOG_TAG, "Video Completed: " + String.valueOf(mediaPlayer.getCurrentPosition()));
                            progressBarForVideo.setVisibility(View.GONE);
                            mediaControllerLayout.setVisibility(View.GONE);
                            playedAll = true;
                            sendAdAction(Constants.KEY_PLAYED_ALL);
                        }
                    });
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                    if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                        Log.i(LOG_TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                        progressBarForVideo.setVisibility(View.GONE);
                    //    videoDuration.setVisibility(View.VISIBLE);
                    //    setVideoDuration(videoView.getDuration());
                        setVideoTimer();
                        return true;
                    }
                    return false;
                }
            });

            videoView.seekTo(1);
            videoView.start();

            ctaButton.setText(ad.getButtonName());
            ctaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendAdAction(Constants.KEY_CLICKED);
                    dismiss();
                    MobAdUtils.openWebUrlExternal(getActivity(), ad.getButtonLink());
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();

                }
            });

            earnedCoinsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobAdUtils.startNewActivity(getActivity(), "com.oqunet.mobad");
                }
            });

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.action_deactivate) {
                                deactivateAdService();
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.menu);
                    popupMenu.show();
                }
            });

        }


    }

    private void setTextAdDataAndListeners() {
        if (ad != null) {
            ImageUtil.displayRoundImage(advertiserBrandIcon, "https://" + ad.getAdPoster(), null);
            advertiserName.setText(ad.getAdvertiserName());
            adTitle.setText(ad.getAdTitle());
            adDescription.setText(ad.getAdDescription());
            ctaButton.setText(ad.getButtonName());

            ctaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendAdAction(Constants.KEY_CLICKED);
                    dismiss();
                    MobAdUtils.openWebUrlExternal(getActivity(), ad.getButtonLink());
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();

                }
            });

            earnedCoinsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobAdUtils.startNewActivity(getActivity(), "com.oqunet.mobad");
                }
            });

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.action_deactivate) {
                                deactivateAdService();
                            }
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.menu);
                    popupMenu.show();
                }
            });
        }

    }


    public void sendAdAction(String action) {
        apiService = ApiClient.getClient().create(ApiService.class);
        handelErrors = new HandelErrors(getActivity());
        String deviceId = MobAdUtils.getUniqueIMEIId(getActivity());
        Log.e(LOG_TAG, " Android ID: " + deviceId);
        adActionCall = apiService.sendAdAction(deviceId, String.valueOf(ad.getAdId()), action);
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
                            setEarnedCoinsMessage(adActionResult.getMessage());
                            earnedCoinsLayout.setVisibility(View.VISIBLE);
                            ViewAnimation.showIn(earnedCoinsLayout);
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    ViewAnimation.showOut(earnedCoinsLayout);
                                }
                            };
                            handler.postDelayed(runnable, 5000);
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


    private void setEarnedCoinsMessage(String message) {
        earnedCoinsMessage.setText(message);
    }

    private void toastMessageFloatingImage(String message) {
        final Toast toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_SHORT);

        //inflate view
        View customView = getLayoutInflater().inflate(R.layout.earned_coins_toast_floating_image, null);
        TextView messageText = customView.findViewById(R.id.message);
        messageText.setText(message);
        TextView appButton = customView.findViewById(R.id.app_button);
        appButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobAdUtils.startNewActivity(getActivity(), "com.oqunet.mobad");
            }
        });
        toast.setView(customView);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppDatabase.getInstance(getActivity()).getAdDao().deleteAd(ad);
        showingAdInterface.onShownAd();
        Log.i(LOG_TAG, "onDestroy: Delete Ad... Finish Display Ad Activity...");
        if(videoView != null) {
            videoView.stopPlayback();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(videoView != null) {
            if (mCurrentPosition == 5) {
                if (!playedAll) {
                    Log.i(LOG_TAG, "PLAYED 5 SEC");
                    sendAdAction(Constants.KEY_PLAYED_5SEC);
                }
            } else {
                if (videoTimer != null) {
                    videoTimer.cancel();
                }

            }

        }
    }

    public void setVideoTimer() {
        videoTimer = new Timer();
        videoTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (timeCounter == seconds) {
                            videoTimer.cancel();
                            return;
                        }

                        mCurrentPosition = timeCounter;
                        Log.i(LOG_TAG, "Current Position: " + String.valueOf(mCurrentPosition));
                        timeCounter++;
                    }
                });
            }
        }, 0, 1000);
    }

    private void setVideoDuration(final int noOfSeconds) {
        countDownTimer = new CountDownTimer(noOfSeconds, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String time = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                videoDuration.setText(time);
                if (videoDuration.getText().equals("01")) {
                    videoDuration.setVisibility(View.INVISIBLE);
                }
            }

            public void onFinish() {
                videoDuration.setVisibility(View.INVISIBLE);
            }
        }.start();

    }

    private void deactivateAdService() {
        String deviceId = MobAdUtils.getUniqueIMEIId(getActivity());
        Log.e(LOG_TAG, " Android ID: " + deviceId);
        deactivateAdServiceCall = apiService.deactivateAdStatus(deviceId);
        deactivateAdServiceCall.enqueue(new Callback<AdServiceSetting>() {
            @Override
            public void onResponse(@NonNull Call<AdServiceSetting> call, @NonNull Response<AdServiceSetting> response) {
                int code = response.code();
                Log.i("Status Code: ", String.valueOf(code));
                if (response.isSuccessful()) {
                    // 200 OK!
                    if (response.body() != null) {
                        Log.i(LOG_TAG, "Result: " + response.body().toString());
                        String status = response.body().getStatus();
                        String message = response.body().getMessage();
                        if (status != null) {
                            if (status.equals("200")) {
                                MobAdUtils.displaySuccessToast(getActivity(), "Ad Service has been deactivated successfully!");
                                dismiss();
                            } else {
                                MobAdUtils.displayErrorToast(getActivity(), message);
                            }
                        }

                    }


                } else {
                    handelErrors.handleStatusCodeErrors(code, deactivateAdServiceCall, LOG_TAG);

                }

            }

            @Override
            public void onFailure(Call<AdServiceSetting> call, Throwable t) {
                handelErrors.onFailureCall(call, t, LOG_TAG);
            }

        });
    }


}
