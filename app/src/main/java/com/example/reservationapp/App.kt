package com.example.reservationapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Retrofit.Prefs
import com.example.reservationapp.Retrofit.RetrofitClient
import kotlin.properties.Delegates

//싱글톤 패턴을 사용
class App :Application(){
    companion object{
        lateinit var retrofitClient: RetrofitClient
        lateinit var apiService: APIService
        lateinit var hospitalName:String
        lateinit var prefs: Prefs
        var mylat by Delegates.notNull<Double>()
        var mylng by Delegates.notNull<Double>()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()

        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()
    }
}