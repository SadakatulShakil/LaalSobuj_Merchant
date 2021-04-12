package com.futureskyltd.app.utils;

import com.futureskyltd.app.utils.District.DistrictList;
import com.futureskyltd.app.utils.EditMerchant.EditMerchant;
import com.futureskyltd.app.utils.MyOrder.MyOrder;
import com.futureskyltd.app.utils.OrderDetails.OrderDetails;
import com.futureskyltd.app.utils.Profile.Profile;
import com.futureskyltd.app.utils.Status.ChangeStatus;
import com.futureskyltd.app.utils.TodayNewOrder.TodayNewOrder;
import com.futureskyltd.app.utils.Upazila.UpazilatList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Headers("accept: application/json, content-type: application/json")
    @GET("district-list")
    Call<DistrictList> getByDistrict();

    @Headers("accept: application/json, content-type: application/json")
    @FormUrlEncoded
    @POST("upazilas-selected-by-district")
    Call<UpazilatList> postByUpazila(
            @Field("district_id") int district_id);

   @Headers("accept: application/json, content-type: application/json")
   @FormUrlEncoded
    @POST("merchantprofile")
    Call<Profile> getByProfile(
            @Header("Authorization") String token,
            @Field("user_id") String user_id
    );

    @Headers("accept: application/json, content-type: multipart/form-data")
    @Multipart
    @POST("editmerchantprofile")
    Call<EditMerchant> postByEditMerchant(
            @Header("Authorization") String token,
            @Part("user_id") RequestBody user_id,
            @Part("first_name") RequestBody first_name,
            @Part("phone_no") RequestBody phone_no,
            @Part("phone_one") RequestBody phone_one,
            @Part("nid") RequestBody nid,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("shop_name") RequestBody shop_name,
            @Part("user_address") RequestBody user_address,
            @Part("profile_image\"; filename=\"myProfile.jpg\" ") RequestBody profile_image,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("district") RequestBody district,
            @Part("upazila") RequestBody upazila,
            @Part("zip") RequestBody zip,
            @Part("payment_method") RequestBody payment_method,
            @Part("account_name") RequestBody account_name,
            @Part("bank_name") RequestBody bank_name,
            @Part("account_number") RequestBody account_number
    );
    @Headers("accept: application/json, content-type: application/json")
    @FormUrlEncoded
    @POST("orderdetails")
    Call<OrderDetails> getByOrderDetails(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id
    );

    @Headers("accept: application/json, content-type: application/json")
    @FormUrlEncoded
    @POST("orderstatus")
    Call<ChangeStatus> getChangeStatus(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("orderid") String order_id,
            @Field("chstatus") String chstatus
    );

    @Headers("accept: application/json, content-type: application/json")
    @FormUrlEncoded
    @POST("todayneworders")
    Call<TodayNewOrder> getTodayOrder(
            @Header("Authorization") String token,
            @Field("user_id") String user_id
    );
    @Headers("accept: application/json, content-type: application/json")
    @FormUrlEncoded
    @POST("myorders")
    Call<MyOrder> getMyOrder(
            @Header("Authorization") String token,
            @Field("user_id") String user_id
    );
}


