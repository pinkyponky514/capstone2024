package com.example.reservationapp.Model.PharmacyMap;

import com.google.gson.annotations.SerializedName;

public class NaverMapResponse {
    @SerializedName("header")
    private NaverMapHeader header;
    @SerializedName("body")
    private NaverMapBody body;

    public NaverMapHeader getHeader() {
        return header;
    }

    public NaverMapBody getBody() {
        return body;
    }
}
