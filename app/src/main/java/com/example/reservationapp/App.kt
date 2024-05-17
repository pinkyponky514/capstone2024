package com.example.reservationapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.reservationapp.Retrofit.Prefs

//싱글톤 패턴을 사용
class App :Application(){
    companion object{
        lateinit var prefs: Prefs
    }
    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}