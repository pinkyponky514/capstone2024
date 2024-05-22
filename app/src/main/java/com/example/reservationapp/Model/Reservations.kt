package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime


data class Reservations(
    @SerializedName("reservationId") val reservationId: Long,
    @SerializedName("reservationDate") val reservationDate: LocalDate, //String으로 바꾸기
    @SerializedName("reservationTime") val reservationTime: LocalTime, //String으로 바꾸기
    @SerializedName("status") val status: String, //예약상태
    @SerializedName("user") val user: PatientSignUpInfoRequest
    //@SerializedName("userid") val user: Patient, //환자 회원가입 정보
    //@SerializedName("hospitalid") val hospital: Hospital, //병원 회원가입 정보
)

data class ReservationRequest(
    @SerializedName("reservationDate") val reservationDate: String, //예약 날짜
    @SerializedName("reservationTime") val reservationTime: String, //예약 시간
    @SerializedName("hospitalid") val hospitalId: Long, //병원 레이블 번호
)

data class ReservationResponse(
    @SerializedName("hospitalId") val hospitalId: Long, //병원 레이블 번호
    @SerializedName("userName") val userName: String, //유저이름
    @SerializedName("date") val reservationDate: LocalDate, //예약날짜
    @SerializedName("time") val reservationTime: LocalTime, //예약시간
): Serializable