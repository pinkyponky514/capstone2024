package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

data class HospitalUserLoginResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: HospitalLoginUser
)

data class HospitalLoginUser(
    @SerializedName("token") val token: String,
    @SerializedName("hospitalId") val hospitalId: Long
)