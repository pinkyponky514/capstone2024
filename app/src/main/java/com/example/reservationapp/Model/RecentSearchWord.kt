package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime

data class RecentSearchWordResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<RecentSearchWord>
)

data class RecentSearchWord(
    @SerializedName("keyword") val keyword: String, //검색어
    @SerializedName("searchDate") val searchDate: LocalDate, //검색날짜
    @SerializedName("searchTime") val searchTime: LocalTime, //검색시간
    @SerializedName("user") val user: Patient
)

data class RecentSearchWordResponseData(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: String
)