package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

//ReserveAlarmAdapter - MyViewModel
data class ReserveItem (
    var hospital_name : String, //예약한 병원이름
    var date : String //예약한 날짜
)


//Retrofit - Model
data class UserInfo (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String,
    @SerializedName("name") var name: String
)