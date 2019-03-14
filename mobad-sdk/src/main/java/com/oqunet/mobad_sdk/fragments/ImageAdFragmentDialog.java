package com.oqunet.mobad_sdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oqunet.mobad_sdk.DisplayAd;
import com.oqunet.mobad_sdk.R;
import com.oqunet.mobad_sdk.database.AppDatabase;
import com.oqunet.mobad_sdk.database.entity.Ad;
import com.oqunet.mobad_sdk.retrofit.ApiClient;
import com.oqunet.mobad_sdk.retrofit.ApiService;
import com.oqunet.mobad_sdk.retrofit.HandelErrors;
import com.oqunet.mobad_sdk.retrofit.entity.Action;
import com.oqunet.mobad_sdk.utils.Constants;
import com.oqunet.mobad_sdk.utils.ImageUtil;
import com.oqunet.mobad_sdk.utils.MobAdUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImageAdFragmentDialog extends DialogFragment {
    private static final String LOG_TAG = ImageAdFragmentDialog.class.getSimpleName();
    public static final String ARG_ITEM_ID = "image_ad";
    ImageView advertiserBrandIcon, advertiserAdImage;
    TextView advertiserName, adTitle, adDescription;
    TextView ctaButton;
    ImageButton closeButton;
    Ad ad;
    ShowingImageAdInterface showingImageAdInterface;
    ApiService apiService;
    Call<Action> adActionCall;
    HandelErrors handelErrors;


    public ImageAdFragmentDialog() {
        // Required empty public constructor
    }

    public interface ShowingImageAdInterface {
        void onShowingImageAd();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof ShowingImageAdInterface) {
            showingImageAdInterface = (ShowingImageAdInterface) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ShowingImageAdInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        ad = bundle.getParcelable("ad");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ad_image_layout, container, false);

        setCancelable(false);

        advertiserBrandIcon = view.findViewById(R.id.advertiser_icon);
        advertiserAdImage = view.findViewById(R.id.ad_image);
        advertiserName = view.findViewById(R.id.advertiser_name);
        adTitle = view.findViewById(R.id.title);
        adDescription = view.findViewById(R.id.description);
        ctaButton = view.findViewById(R.id.btn_cta);
        closeButton = view.findViewById(R.id.bt_close);

        sendAdAction(Constants.KEY_VIEWED);

        setAdData();

        ctaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAdAction(Constants.KEY_CLICKED);
                dismiss();
                showingImageAdInterface.onShowingImageAd();
                MobAdUtils.openWebUrlExternal(getActivity(), ad.getButtonLink());
                AppDatabase.getInstance(getActivity()).getAdDao().deleteAd(ad);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase.getInstance(getActivity()).getAdDao().deleteAd(ad);
                dismiss();
                showingImageAdInterface.onShowingImageAd();

            }
        });


        return view;
    }

    private void setAdData() {
        if (ad != null) {
            ImageUtil.displayRoundImage(advertiserBrandIcon, ad.getAdvertiserImage(), null);
            ImageUtil.displayImage(advertiserAdImage, ad.getAdPath(), null);
            advertiserName.setText(ad.getAdvertiserName());
            adTitle.setText(ad.getAdTitle());
            adDescription.setText(ad.getAdDescription());
            ctaButton.setText(ad.getButtonName());
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void sendAdAction(String action) {
        apiService = ApiClient.getClient().create(ApiService.class);
        handelErrors = new HandelErrors(getActivity());
        String deviceId = MobAdUtils.getDeviceID(getActivity());
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
}
