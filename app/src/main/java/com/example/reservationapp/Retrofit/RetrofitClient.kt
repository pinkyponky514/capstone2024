package com.example.reservationapp.Retrofit

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reservationapp.Model.APIService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.google.gson.JsonDeserializer
import retrofit2.converter.gson.GsonConverterFactory
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object RetrofitClient {

    private var instance: RetrofitClient? = null
    private var apiService: APIService? = null
    private var retrofit: Retrofit

    private const val baseUrl = "http://223.194.135.46:8080" //"http://10.0.2.2:8080"

    init {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory()) //Gson 변환기 생성 시 적용
            .build()

        apiService = retrofit.create(APIService::class.java)
    }

    fun getInstance(): RetrofitClient {
        if (instance == null) {
                instance = RetrofitClient
        }
        return instance as RetrofitClient
    }

    fun getRetrofitInterface(): APIService {
        return apiService as APIService
    }


    // String을 LocalDate, LocalTime, LocalDateTime으로 변형하는 것을 등록
    @RequiresApi(Build.VERSION_CODES.O)
    private fun gsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            })
            .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
                LocalDate.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            })
            .registerTypeAdapter(LocalTime::class.java, JsonDeserializer { json, _, _ ->
                LocalTime.parse(json.asString, DateTimeFormatter.ofPattern("HH:mm:ss"))
            })
/*
            .registerTypeAdapter(LocalTime::class.java, JsonDeserializer { json, _, _ ->
                LocalTime.parse(json.asString, DateTimeFormatter.ofPattern("HH:mm"))
            })
*/
            .setLenient()
            .create()

        return GsonConverterFactory.create(gson)
    }
//
}
