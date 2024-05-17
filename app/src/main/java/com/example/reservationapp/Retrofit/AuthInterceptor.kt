package com.example.reservationapp.Retrofit

import com.example.reservationapp.App
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req =
            chain.request().newBuilder().addHeader("Authorization", App.prefs.token ?: "").build()
        return chain.proceed(req)
    }
}