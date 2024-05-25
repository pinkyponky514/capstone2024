package com.example.reservationapp.Model.PharmacyMap;

import com.google.gson.annotations.SerializedName;

public class NaverMapBody {
    @SerializedName("items")
    private NaverMapItems items;
    @SerializedName("numOfRows")
    private int numOfRows;
    @SerializedName("pageNo")
    private int pageNo;
    @SerializedName("totalCount")
    private int totalCount;

    public NaverMapItems getItems() {
        return items;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
