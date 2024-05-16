package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

//병원검색
data class SearchHospital(
    @SerializedName("id") val id: Long, //병원 table id
    @SerializedName("hospitalname") val hospitalName: String,
    @SerializedName("address") val address: String,
    @SerializedName("tel") val tel: String,
    @SerializedName("hospital") val hospital: Hospital, //회원가입 했을때 정보
    @SerializedName("distance") val distance: Double,
)