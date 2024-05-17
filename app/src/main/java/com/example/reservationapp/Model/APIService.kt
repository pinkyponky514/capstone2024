package com.example.reservationapp.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @POST("/jwt-login/user/join") //환자 회원가입
    @Headers("Auth: false") //토큰을 헤더에 안담을 api는 이렇게 표시
    fun postPatientSignUp(@Body user: PatientSignUpInfoRequest): Call<PatientSignupInfoResponse> //환자 정보 받음

    @POST("/jwt-hospital-login/hospital/join") //병원 회원가입
    @Headers("Auth: false")
    fun postHospitalSignUp(@Body user: HospitalSignUpInfoRequest): Call<HospitalSignupInfoResponse> //병원 정보 받음

    @POST("/jwt-login/login") //환자 로그인
    @Headers("Auth: false")
    fun postPatientLogin(@Body user: UserLoginInfoRequest): Call<String> //Token 받음

    @POST("/jwt-hospital-login/login") //병원 로그인
    @Headers("Auth: false")
    fun postHospitalLogin(@Body user: UserLoginInfoRequest): Call<String> //Token 받음

    @GET("/hospitals") //병원 검색
    @Headers("Auth: false")
    fun getSearchHospital(
        @Query("query") query: String ?= null,
        @Query("department") className: String ?= null,
        @Query("mapx") mapx: Double = 37.5,
        @Query("mapy") mapy: Double = 126.9
    ): Call<List<SearchHospital>>

    @POST("/hospitals/hospitaldetail") //병원 상세정보 입력
    fun postHospitalDetail(@Body hospital: HospitalDetail2): Call<HospitalDetailResponse>

    @GET("/hospitals/findhospital/{hospitalid}") //병원 상세정보 검색(가져오기)
    fun getHospitalDetail(@Path(value="hospitalid") hospitalId: Long ?= null): Call<HospitalSignupInfoResponse>

    @POST("/bookmarks/{hospitalid}") //병원 즐겨찾기 등록
    fun postHospitalBookmark(@Path(value="hospitalid") hospitalId: Long ?= null): Call<String>

    @GET("/bookmarks") //나의 즐겨찾기 병원 가져오기
    fun getMyHospitalBookmarkList(@Header("token") token: String): Call<MyBookmarkResponse>
}