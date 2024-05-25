package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

data class ApiHospitalResponse (
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<OpenApiHospital>
)

//병원 api 저장 데이터 클래스
data class OpenApiHospital(
    @SerializedName("id") val id: Long, //api 레이블 번호
    @SerializedName("hospitalname") val hospitalName: String, //병원이름
    @SerializedName("address") val address: String, //병원주소
    @SerializedName("tel") val tel: String, //병원 전화번호
    @SerializedName("addnum") val addNum: String, //우편번호
    @SerializedName("mapX") val mapX: Double, //위도
    @SerializedName("mapY") val mapY: Double, //경도
    @SerializedName("hospitalId") val hospitalId:Long
)