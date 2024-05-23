package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime


data class Reservations(
    @SerializedName("reservationId") val reservationId: Long,
    @SerializedName("reservationDate") val reservationDate: LocalDate,
    @SerializedName("reservationTime") val reservationTime: LocalTime,
    @SerializedName("status") val status: String, //예약상태
    @SerializedName("userid") val user: Patient, //환자 회원가입 정보
    @SerializedName("hospitalid") val hospital: Hospital, //병원 회원가입 정보
)

data class ReservationRequest( //예약 등록
    @SerializedName("reservationDate") val reservationDate: String, //예약 날짜
    @SerializedName("reservationTime") val reservationTime: String, //예약 시간
    @SerializedName("hospitalid") val hospitalId: Long, //병원 레이블 번호
)

data class ReservationResponse( //
    @SerializedName("hospitalId") val hospitalId: Long, //병원 레이블 번호
    @SerializedName("userName") val userName: String, //유저이름
    @SerializedName("date") val reservationDate: LocalDate, //예약날짜
    @SerializedName("time") val reservationTime: LocalTime, //예약시간
): Serializable

data class UserReservationResponse( //유저별 예약 조회
    @SerializedName("reservationId") val reservationId: Long, //예약 레이블 번호
    @SerializedName("reservationDate") val reservationDate: LocalDate, //예약 날짜
    @SerializedName("reservationTime") val reservationTime: LocalTime, //예약 시간
    @SerializedName("status") val status: String, //예약 상태


    @SerializedName("hospitalid") val hospitalId: Long, //병원 레이블 번호
    @SerializedName("hospitalName") val hospitalName: String, //병원 이름
    @SerializedName("userName") val userName: String, //예약한 유저 이름

    @SerializedName("department") val className: String, //병원 분과
    @SerializedName("reviewWrite") val reviewWriteBoolean: Boolean //리뷰 썼는지 확인
)

/*
[
    {
        "reservationId": 1,
        "reservationDate": "2024-05-30",
        "reservationTime": "09:00:00",
        "status": "예약신청",
        "hospitalid": 1,
        "hospitalName": "삼성서울병원",
        "userName": "한이"
    }
]
 */