package com.example.reservationapp.Model.PharmacyMap;

import com.google.gson.annotations.SerializedName;

public class NaverMapHeader {
    @SerializedName("resultCode")
    private String resultCode;
    @SerializedName("resultMsg")
    private String resultMsg;

    public String getResultCode() {
        return resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }
}
