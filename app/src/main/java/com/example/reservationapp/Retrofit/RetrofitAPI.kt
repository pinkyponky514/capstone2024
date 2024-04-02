package com.example.reservationapp.Retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class Repo(val id: String,
                val password: String,
                val name: String,
                val birthday: String)

interface RetrofitAPI {
    @POST("/jwt-login/user/join")
    fun getLoginResponse(@Body user: Repo): Call<String>

}
