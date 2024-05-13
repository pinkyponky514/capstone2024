package com.example.reservationapp.Retrofit

import com.example.reservationapp.Model.APIService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var instance: RetrofitClient ?= null
    private var apiService: APIService ?= null
    private val baseUrl = "http://10.0.2.2:8080" //사용하고 있는 서버 BASE 주소


    init {
        val gson = GsonBuilder().setLenient().create() //setLenient() 추가
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson)) //Gson 변환기 생성 시 적용
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

//
}