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

data class ReviewRequest(
    @SerializedName("grade") val starScore: Float, //별점
    @SerializedName("text") val comment: String, //내용
    @SerializedName("reservation_id") val reservationId: Long //예약 레이블 번호
)

data class HospitalReviewAllResponse(
    @SerializedName("review_id") val reviewId: Long, //리뷰 레이블 번호
    @SerializedName("hospital_id") val hospitalId: Long, //병원 레이블 번호
    @SerializedName("user_id") val userId: Long, //유저 레이블 번호
    @SerializedName("reservation_id") val reservationId: Long, //예약 레이블 번호
    @SerializedName("username") val userName: String, //유저이름
    @SerializedName("grade") val grade: Float, //별점
    @SerializedName("text") val comment: String, //내용
    @SerializedName("regDate") val registerDate: LocalDate, //등록날짜
    @SerializedName("modDate") val modifyDate: LocalDate, //수정날짜
)