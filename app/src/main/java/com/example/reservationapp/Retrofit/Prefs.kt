package com.example.reservationapp.Retrofit

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.reservationapp.Model.RecentItem

class Prefs(context: Context) {
    private val prefNm="mPref"
    private val prefs=context.getSharedPreferences(prefNm,MODE_PRIVATE)

    var token:String?
        get() = prefs.getString("token",null)
        set(value){
            prefs.edit().putString("token",value).apply()
        }

/*
    var recentSearchWord: String?
        get() = prefs.getString("recentSearchWord", null)
        set(value){
            prefs.edit().putString("recentSearchWord", value).apply()
        }
*/

    //로그아웃
    fun clearToken(context: Context) {
        prefs.edit().remove("token").apply()
    }
}