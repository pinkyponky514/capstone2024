package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class HospitalDetailResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: HospitalDetail
): Serializable

//병원 마이페이지 상세정보 입력
data class HospitalDetail2(
    @SerializedName("hospitalInfo") val hospitalInfo: String?, //병원 설명
    @SerializedName("department") val department: String?, //진료과명

    @SerializedName("mon_open") val mon_open: String?, //월요일
    @SerializedName("mon_close") val mon_close: String?,
    @SerializedName("tue_open") val tue_open: String?, //화요일
    @SerializedName("tue_close") val tue_close: String?,
    @SerializedName("wed_open") val wed_open: String?, //수요일
    @SerializedName("wed_close") val wed_close: String?,
    @SerializedName("thu_open") val thu_open: String?, //목요일
    @SerializedName("thu_close") val thu_close: String?,
    @SerializedName("fri_open") val fri_open: String?, //금요일
    @SerializedName("fri_close") val fri_close: String?,
    @SerializedName("sat_open") val sat_open: String?, //토요일
    @SerializedName("sat_close") val sat_close: String?,
    @SerializedName("sun_open") val sun_open: String?, //일요일
    @SerializedName("sun_close") val sun_close: String?,
    @SerializedName("hol_open") val hol_open: String?, //공휴일
    @SerializedName("hol_close") val hol_close: String?,
    @SerializedName("lunch_start") val lunch_start: String?, //점심시간
    @SerializedName("lunch_end") val lunch_end: String?,
    )


//병원 상세정보
data class HospitalDetail(
    @SerializedName("id") val detailId: Long,
    @SerializedName("hospitalInfo") val hospitalInfo: String, //병원 설명
    @SerializedName("department") val department: String, //진료과명
   @SerializedName("doctorInfo") val doctorInfo: String, //의사 정보

    @SerializedName("mon_open") val mon_open: String, //월요일
    @SerializedName("mon_close") val mon_close: String,
    @SerializedName("tue_open") val tue_open: String, //화요일
    @SerializedName("tue_close") val tue_close: String,
    @SerializedName("wed_open") val wed_open: String, //수요일
    @SerializedName("wed_close") val wed_close: String,
    @SerializedName("thu_open") val thu_open: String, //목요일
    @SerializedName("thu_close") val thu_close: String,
    @SerializedName("fri_open") val fri_open: String, //금요일
    @SerializedName("fri_close") val fri_close: String,
    @SerializedName("sat_open") val sat_open: String, //토요일
    @SerializedName("sat_close") val sat_close: String,
    @SerializedName("sun_open") val sun_open: String, //일요일
    @SerializedName("sun_close") val sun_close: String,
    @SerializedName("hol_open") val hol_open: String, //공휴일
    @SerializedName("hol_close") val hol_close: String,
    @SerializedName("lunch_start") val lunch_start: String, //점심시간
    @SerializedName("lunch_end") val lunch_end: String,
    @SerializedName("boardImage1") val mainImage: String //메인 이미지
    //병원이미지 추가
    )

data class SetOpenApiResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String
)