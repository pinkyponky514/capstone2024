package com.example.reservationapp.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("/jwt-login/user/join")
    fun postSignUp(@Body user: UserSignUpInfo): Call<UserSignUpInfo>

    @POST("/jwt-login/login")
    fun postLogin(@Body user: UserLoginInfo): Call<String>
}