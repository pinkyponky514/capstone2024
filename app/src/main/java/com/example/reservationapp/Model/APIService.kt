package com.example.reservationapp.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @POST("/jwt-login/user/join") //환자 회원가입
    fun postPatientSignUp(@Body user: PatientSignUpInfoRequest): Call<PatientSignupInfoResponse> //환자 정보 받음

    @POST("/jwt-hospital-login/hospital/join") //병원 회원가입
    fun postHospitalSignUp(@Body user: HospitalSignUpInfoRequest): Call<HospitalSignupInfoResponse> //병원 정보 받음

    @POST("/jwt-login/login") //환자 로그인
    fun postPatientLogin(@Body user: UserLoginInfoRequest): Call<String> //Token 받음

    @POST("/jwt-hospital-login/login") //병원 로그인
    fun postHospitalLogin(@Body user: UserLoginInfoRequest): Call<String> //Token 받음

    @GET("/hospitals") //병원 검색
    fun getSearchHospital(
        @Query("query") query: String ?= null,
        @Query("department") className: String ?= null,
        @Query("mapx") mapx: Double = 37.5,
        @Query("mapy") mapy: Double = 126.9
    ): Call<List<SearchHospital>>

    @GET("/hospitals/findhospital/{hospitalid}") //병원 상세정보 검색(가져오기)
    fun getHospitalDetail(@Path(value = "hospitalid") hospitalId: Long ?= null): Call<HospitalSignupInfoResponse>
}