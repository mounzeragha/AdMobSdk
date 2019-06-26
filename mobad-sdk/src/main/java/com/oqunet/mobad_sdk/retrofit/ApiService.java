package com.oqunet.mobad_sdk.retrofit;


import com.oqunet.mobad_sdk.retrofit.entity.Action;
import com.oqunet.mobad_sdk.retrofit.entity.Ad;
import com.oqunet.mobad_sdk.retrofit.entity.AdServiceSetting;
import com.oqunet.mobad_sdk.retrofit.entity.AdServiceStatus;
import com.oqunet.mobad_sdk.retrofit.entity.Location;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;


public interface ApiService {

    @GET("getAd")
    Call<Ad> getAd(@Query("id") String deviceId,
                   @Query("country") String countryCode);

    @FormUrlEncoded
    @PUT("UpdateProfileCTA")
    Call<Action> sendAdAction(
            @Field("IMEI") String deviceId,
            @Field("AdID") String adId,
            @Field("Action") String adAction);

    @GET("GetProfileStatus")
    Call<AdServiceStatus> getAdServiceStatus(
            @Query("IMEI") String deviceId
    );

    @FormUrlEncoded
    @PUT("OptOutProfile")
    Call<AdServiceSetting> deactivateAdStatus(
            @Field("IMEI") String deviceId
    );

    @FormUrlEncoded
    @PUT("UpdateUserCountry")
    Call<Location> updateUserLocation(
            @Field("IMEI") String deviceId,
            @Field("Country") String country);



/**
    @FormUrlEncoded
    @POST("createuser")
    Call<Ad> createAccount(
            @Field("FirstName") String firstName,
            @Field("LastName") String lastName,
            @Field("Email") String email,
            @Field("Password") String password,
            @Field("Mobile") String mobile
    );

    @FormUrlEncoded
    @POST("Login")
    Call<User> userLogin(
            @Field("Email") String email,
            @Field("Password") String password
    );

    @GET("ForgotPassword_SendValidation")
    Call<User> emailValidation(
            @Query("email") String email
    );

    @FormUrlEncoded
    @POST("ForgotPassword_ValidateCode")
    Call<User> changePassword(
            @Field("email") String email,
            @Field("newpassword") String newPassword,
            @Field("validationcode") String validationCode
    );

    @FormUrlEncoded
    @POST("ExternalLoginUser")
    Call<User> userExternalLogin(
            @Field("LoginProvider") String provider,
            @Field("ProviderKey") String providerKey,
            @Field("FirstName") String firstName,
            @Field("LastName") String lastName,
            @Field("Email") String email
    );

    @FormUrlEncoded
    @PUT("UpdateProfile")
    Call<User> updateProfile(
            @Header("Authorization") String token,
            @Field("FirstName") String firstName,
            @Field("LastName") String lastName,
            @Field("Email") String email,
            @Field("Mobile") String mobile
    );

    @FormUrlEncoded
    @PUT("UpdateProfile")
    Call<User> updateSettings(
            @Header("Authorization") String token,
            @Field("Language") String language,
            @Field("Currency") String currency,
            @Field("Country") String country

    );

    @FormUrlEncoded
    @POST("AddToWishlist")
    Call<Wishlist> addToWishList(
            @Field("ItemID") String id,
            @Header("Authorization") String token
    );

    @GET("GetWishlist")
    Call<Wishlist> getWishList(
            @Header("Authorization") String token
    );


    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "DeleteWishlist", hasBody = true)
    Call<Wishlist> deleteFromWishList(
            @Field("ItemID") String id,
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("AddToCart")
    Call<OrderUnPaid> addToCart(
            @Field("ItemID") String id,
            @Field("quantity") String quantity,
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("AddToCart")
    Call<OrderUnPaid> addBundleToCart(
            @Field("ItemID") String id,
            @Field("quantity") String quantity,
            @Field("isdeal") String isdeal,
            @Header("Authorization") String token
    );

    @GET("GetCart")
    Call<OrderUnPaid> getCart(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("UpdateCartItem")
    Call<OrderUnPaid> updateCartItem(
            @Field("ItemID") String id,
            @Field("quantity") String quantity,
            @Header("Authorization") String token,
            @Field("isdeal") String isdeal
    );

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "DeleteCart", hasBody = true)
    Call<OrderUnPaid> deleteFromCart(
            @Field("ItemID") String id,
            @Header("Authorization") String token,
            @Field("isdeal") String isdeal

    );

    @FormUrlEncoded
    @POST("IpayBalancePayment")
    Call<IPayCardsPayment> payWithIPayCardsBalance(
            @Field("amount") String amount,
            @Field("currency") String currency,
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("CreatePayment_Checkout")
    Call<CheckoutPayment> payWithCheckout(
            @Field("CheckoutToken") String cardToken,
            @Header("Authorization") String token
    );

    @GET("GetOrderHistory")
    Call<OrdersHistory> getOrdersHistory(
            @Header("Authorization") String token
    );

    @POST("ValidateCart")
    Call<OutOfStock> getAvailableQuantity(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("ValidateOTP")
    Call<PhoneConfirmation> confirmPhoneNumber(
            @Field("OTP") String codeNumber,
            @Field("Email") String email
    );

    @GET("SearchItem")
    Call<Products> getSearchResult(@Query("id") String word);

    @FormUrlEncoded
    @POST("RedeemPromoCode")
    Call<RedeemGiftCard> redeemGiftCard(
            @Header("Authorization") String token,
            @Field("PromoCode") String code

    );

    @FormUrlEncoded
    @PUT("SendToFriend")
    Call<SendToFriend> sendToFriend(
            @Header("Authorization") String token,
            @Field("ProductID") String productID,
            @Field("ToFriend") String email

    );


    @GET("FilterProducts")
    Call<Products> filterList(
            @Query("category") String category,
            @Query("brands") String brand,
            @Query("pricefrom") String fromPrice,
            @Query("priceto") String toPrice,
            @Query("currency") String currency

    );
*/
}
