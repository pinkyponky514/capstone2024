package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime

data class ConfirmReservationResponse(
    @SerializedName("status") val status: String
)

data class ConfirmReservationRequest(
    @SerializedName("reservationDate") val date: String,
    @SerializedName("reservationTime") val time: String,
)