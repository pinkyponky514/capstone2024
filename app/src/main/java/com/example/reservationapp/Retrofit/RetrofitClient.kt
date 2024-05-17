package com.example.reservationapp.Retrofit

import com.example.reservationapp.Model.APIService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var instance: RetrofitClient? = null
    private var apiService: APIService? = null
    private var retrofit: Retrofit
    private const val baseUrl = "http://192.168.35.240:8080"

    private val gson = GsonBuilder().setLenient().create()

    init {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(APIService::class.java)
    }

    @Synchronized
    fun getInstance(): RetrofitClient {
        if (instance == null) {
                instance = RetrofitClient
        }
        return instance as RetrofitClient
    }

    fun getRetrofitInterface(): APIService {
        return apiService as APIService
    }

}




