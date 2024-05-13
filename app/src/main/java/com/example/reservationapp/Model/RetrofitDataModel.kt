package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Retrofit - Model
//회원가입
//request
data class UserSignUpInfoRequest (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String,
    @SerializedName("name") var name: String
)
//환자 - response
data class PatientSignupInfoResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PatientSignupData
):Serializable
data class PatientSignupData(
    @SerializedName("userid") val userId: Long,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: UserRole,
    @SerializedName("reservations") val reservations: List<Any>?
):Serializable
//병원 - response
data class HospitalSignupInfoResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: HospitalSignupData
):Serializable
data class HospitalSignupData(
    @SerializedName("hospitalid") val userId: Long,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: UserRole,
    @SerializedName("reservations") val reservations: List<Any>?
):Serializable
enum class UserRole {
    @SerializedName("USER") USER,
    @SerializedName("ADMIN") ADMIN
}


//로그인
data class UserLoginInfoRequest (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String
)

