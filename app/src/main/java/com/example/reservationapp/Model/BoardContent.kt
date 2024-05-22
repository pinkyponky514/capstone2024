package com.example.reservationapp.Model

import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class BoardContent(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("writer") val writer: String?
)

data class BoardResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<BoardContent>
)
