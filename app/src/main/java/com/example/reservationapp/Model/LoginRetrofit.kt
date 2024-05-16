package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

/**/
//로그인
data class UserLoginInfoRequest (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String
)