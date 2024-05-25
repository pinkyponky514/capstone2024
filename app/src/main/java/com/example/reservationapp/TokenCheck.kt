package com.example.reservationapp

import android.util.Base64
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.Date


fun isTokenExpired(token: String): Boolean {
    try {
        // JWT 토큰의 페이로드 추출
        val split = token.split(".")
        if (split.size != 3) {
            return true // 잘못된 토큰 형식일 경우 만료로 간주
        }

        val payload = String(Base64.decode(split[1], Base64.URL_SAFE), Charset.defaultCharset())
        val jsonObject = JSONObject(payload)

        // `exp` 클레임 추출 및 만료 여부 확인
        val exp = jsonObject.getLong("exp")
        val currentTime = System.currentTimeMillis() / 1000

        return currentTime >= exp
    } catch (e: Exception) {
        // 오류 발생 시 만료된 것으로 간주
        e.printStackTrace()
        return true
    }
}