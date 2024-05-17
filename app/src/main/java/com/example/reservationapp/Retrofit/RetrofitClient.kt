package com.example.reservationapp.Retrofit

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.reservationapp.Model.APIService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

object RetrofitClient {

    private var instance: RetrofitClient? = null
    private var apiService: APIService? = null
    private lateinit var retrofit: Retrofit
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




