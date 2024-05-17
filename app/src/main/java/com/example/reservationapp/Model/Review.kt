package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Review(
    @SerializedName("id") val id: Long, //리뷰 레이블 번호
    @SerializedName("user") val user: Patient, //
    @SerializedName("grade") val starScore: Float, //별점
    @SerializedName("text") val comment: String, //내용
    @SerializedName("regDate") val registerDate: LocalDate, //리뷰 등록 날짜
    @SerializedName("modDate") val modifyDate: LocalDate, //리뷰 수정 날짜
)