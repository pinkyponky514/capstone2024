package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate


/*회원가입*/
//환자 - request
data class PatientSignUpInfoRequest(
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String,
    @SerializedName("name") var name: String,
    @SerializedName("birthday") var birthday: String
)
//병원 - request
data class HospitalSignUpInfoRequest (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String,
    @SerializedName("name") var name: String,
    //@SerializedName("addnum") var addnum: String //우편번호
)

//환자 - response
data class PatientSignupInfoResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Patient
)
data class Patient(
    @SerializedName("userid") val userId: Long,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("birthday") var birthday: LocalDate,
    @SerializedName("role") val role: UserRole,
    @SerializedName("reservations") val reservations: List<Any>
)

//병원 - response
data class HospitalSignupInfoResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Hospital
)
//병원 - response
data class HospitalSearchResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SearchHospital
)
data class dataHospital(
    @SerializedName("id") val id: Long,
    @SerializedName("hospitalname") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("tel") val tel: String,
    @SerializedName("hospital") val hospital: Hospital,
    @SerializedName("mainImage") val mainImage: String,
    @SerializedName("mainImage") val distance: String
)

data class Hospital(
    @SerializedName("hospitalid") val hospitalId: Long,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
  //  @SerializedName("addnum") val addNum: String,
    @SerializedName("role") val role: UserRole,
    @SerializedName("hospitalDetail") val hospitalDetail: HospitalDetail, //병원 상세정보
    @SerializedName("openApiHospital") val openApiHospital: OpenApiHospital, //병원 api 정보
    @SerializedName("review") val review: List<Review>, //병원 리뷰
    @SerializedName("reservations") val reservations: List<Reservations> //병원 예약리스트
)
