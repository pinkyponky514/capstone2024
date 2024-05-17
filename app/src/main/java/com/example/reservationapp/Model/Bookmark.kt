package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

//나의 병원 즐겨찾기 요청
data class MyBookmarkResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("result") val result: MyResult
)
data class MyResult(
    @SerializedName("data") val data: Data
)
data class Data(
    @SerializedName("boards") val boards: List<HospitalSimpleData>,
    @SerializedName("pageInfoDto") val pageInfoDto: PageInfoDto
)
data class HospitalSimpleData( //즐겨찾기 한 병원 간단한 정보
    @SerializedName("id") val hospitalId: Long, //병원 레이블 번호
    @SerializedName("name") val hospitalName: String //병원이름
)
data class PageInfoDto(
    @SerializedName("totalPage") val totalPage: Int, //전체 페이지 수
    @SerializedName("nowPage") val nowPage: Int, //현재 페이지
    @SerializedName("numberOfElements") val numberOfElements: Int, //현재 페이지에 나올 데이터 수
    @SerializedName("isNext") val isNext: Boolean //다음 페이지 존재 여부
)



//즐겨찾기 등록
data class BookmarkResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("result") val result: Result
)
data class Result(
    @SerializedName("datat") val data: String
)