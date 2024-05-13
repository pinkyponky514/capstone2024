package com.example.reservationapp.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("/jwt-login/user/join") //환자 회원가입
    fun postPatientSignUp(@Body user: UserSignUpInfoRequest): Call<PatientSignupInfoResponse>

    @POST("/jwt-hospital-login/hospital/join") //병원 회원가입
    fun postHospitalSignUp(@Body user: UserSignUpInfoRequest): Call<HospitalSignupInfoResponse>

    @POST("/jwt-login/login") //환자 로그인
    fun postPatientLogin(@Body user: UserLoginInfoRequest): Call<String>

    @POST("/jwt-hospital-login/login")
    fun postHospitalLogin(@Body user: UserLoginInfoRequest): Call<String>
}