package com.example.reservationapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Retrofit.Prefs
import com.example.reservationapp.Retrofit.RetrofitClient


class App :Application(){


    companion object{
        lateinit var retrofitClient: RetrofitClient
        lateinit var apiService: APIService
        lateinit var prefs: Prefs
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()

        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()
    }
}