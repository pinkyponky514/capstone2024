package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

data class ChatBotResponse(
    @SerializedName("recommended_departments") val departments: List<String>, //추천 진료과목
    @SerializedName("fail_message") val fail_message: String
)
