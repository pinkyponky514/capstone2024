package com.example.reservationapp.Model;

import com.example.reservationapp.Model.PharmacyMap.NaverMapItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NaverMapApiInterface {
    @GET("getParmacyBassInfoInqire")
    Call<NaverMapItem> getMapData(
            @Query(value = "serviceKey", encoded = true) String serviceKey,
            @Query("_type") String type,
            @Query("numOfRows") int numOfRows
    );
}
