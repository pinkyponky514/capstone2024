package com.example.reservationapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
data class Repo(val id: String,
                val password: String,
                val name: String,
                val birthday: String)

interface RetrofitAPI {
    @POST("/jwt-login/user/join")
    fun getLoginResponse(@Body user: Repo): Call<String>

}
