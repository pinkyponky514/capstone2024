package com.example.reservationapp.Model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

//ReserveAlarmAdapter - MyViewModel
data class ReserveItem (
    var hospital_name : String, //예약한 병원이름
    var date : String //예약한 날짜
)


//Retrofit - Model
data class UserInfo (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String,
    @SerializedName("name") var name: String
)

//RecentSearchWordAdapter - MyViewModel
data class RecentItem (
    var recent_search_word: String //최근 검색한 단어
)

//HospitalListAdapter - MyViewModel
data class HospitalItem (
    var hospitalName: String, //병원이름
    var starScore: String, //별점(4.0)
    var openingTimes: String, //영업시간
    var hospitalAddress: String //병원주소
)

//ChattingAdapter
data class ChatItem (
    var user : String, //사람
    var text : String //메세지
)

// 예약된 내역 모델 클래스
data class ReservationItem(
    val time: String, // 예약 시간
    val patientName: String, // 환자 이름
    val birthDate: String, // 생년월일
    val reservationDate: String // 예약 날짜
)

// DataModel.kt
data class CommunityItem(
    val imageResource: Int,
    val title: String,
    //val detail: String
)
