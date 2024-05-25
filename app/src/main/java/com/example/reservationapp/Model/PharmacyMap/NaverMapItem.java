package com.example.reservationapp.Model.PharmacyMap;

import com.google.gson.annotations.SerializedName;

public class NaverMapItem {
    @SerializedName("response")
    private NaverMapResponse response;

    public NaverMapResponse getResponse() {
        return response;
    }

}
