package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime


data class Reservations(
    @SerializedName("reservationId") val reservationId: Long,
    @SerializedName("reservationDate") val reservationDate: LocalDate, //String으로 바꾸기
    @SerializedName("reservationTime") val reservationTime: LocalTime, //String으로 바꾸기
    @SerializedName("status") val status: String, //예약상태

    @SerializedName("userid") val user: Patient, //환자 회원가입 정보
    @SerializedName("hospitalid") val hospital: Hospital, //병원 회원가입 정보
)