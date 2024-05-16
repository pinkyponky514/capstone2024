package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName

enum class UserRole {
    @SerializedName("USER") USER,
    @SerializedName("ADMIN") ADMIN
}