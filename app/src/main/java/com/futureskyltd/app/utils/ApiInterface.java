package com.futureskyltd.app.utils;

import com.futureskyltd.app.utils.District.DistrictList;
import com.futureskyltd.app.utils.Upazila.UpazilatList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers("accept: application/json, content-type: application/json")
    @GET("district-list")
    Call<DistrictList> getByDistrict();

    @Headers("accept: application/json, content-type: application/json")
    @FormUrlEncoded
    @POST("upazilas-selected-by-district")
    Call<UpazilatList> postByUpazila(
            @Field("district_id") int district_id);
}
