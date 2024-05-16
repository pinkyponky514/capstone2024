package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Review(
    @SerializedName("hospitalid") val hospital: Hospital, //병원 회원가입 정보
    @SerializedName("userid") val user: Patient, //환자 회원가입 정보
    @SerializedName("id") val id: Long, //리뷰 레이블 번호
    @SerializedName("grade") val starScore: Float, //별점
    @SerializedName("text") val comment: String, //내용
    @SerializedName("regDate") val registerDate: LocalDate, //리뷰 등록 날짜
    @SerializedName("modDate") val modifyDate: LocalDate, //리뷰 수정 날짜
)