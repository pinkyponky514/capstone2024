package com.example.reservationapp.Model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

//ReserveAlarmAdapter
data class ReserveItem (
    var hospital_name : String, //예약한 병원이름
    var reservationDate: String, //예약한 날짜
    var reservationTime: String, //예약한 시간
)

//RecentSearchWordAdapter
data class RecentItem (
    var recentSearchWord: String //최근 검색한 단어
/*
    var searchDate: LocalDate //검색 날짜
    var searchTime: LocalTime //검색시간
*/
)

//HospitalListAdapter
data class HospitalItem(
    var hospitalId: Long, //병원 레이블 번호
    var hospitalName: String, //병원이름 ㅇ
    var starScore: String, //별점(4.0)
    var openingTimes: String, //영업시간 ㅇ
    var hospitalAddress: String, //병원주소 ㅇ
    var className: List<String>, //진료과 ㅇ
    var status: String, //병원 영업 상태
    //var bookmarkBoolean: Boolean //즐겨찾기 플래그
)

//ChattingAdapter
data class ChatItem (
    var user: String,
    var text: String? = null,
    var imageResource: Int? = null // 이미지 리소스의 ID를 저장할 수 있는 nullable Int 타입의 필드 추가
)


// 예약된 내역 모델 클래스
data class ReservationItem(
    val time: String, // 예약 시간
    val patientName: String, // 환자 이름
    val birthDate: String, // 생년월일
    val reservationDate: String, // 예약 날짜
    var status: String // 진료상태
): Comparable<ReservationItem> {
    override fun compareTo(other: ReservationItem): Int {
        return this.time.compareTo(other.time) //이 비교는 시간 문자열의 사전 순서에 따라 정렬
    }
}

data class ImageData(
    val uri: Uri
    // 필요한 경우 다른 메타데이터를 추가할 수 있습니다.
)

data class ImageItem(
    val imageResId: Int
)

//HistoryAdapter, AbleReviewWriteAdapter
data class HistoryItem (
    var reservationId: Long, //예약 레이블 번호
    var hospitalId: Long, //병원 레이블 번호
    var status: String, //진료상태
    var hospitalName: String, //병원이름
    var className: String, //진료과명
    var reserveDay: String, //예약 날짜
    var reserveTime: String, //예약 시간
    var reviewWriteBoolean: Boolean //리뷰 썼는지 확인
): Serializable

//ReviewAdapter
/*data class ReviewItem (
    val hospitalId: Long, //병원 레이블 번호
    //val reservationId: Long, //예약 레이블 번호
    val reviewId: Long, //리뷰 레이블 번호
    var starScore: String, //별점
    var comment: String, //리뷰내용
    var reviewDate: String, //날짜
    var userName: String, //유저이름
)*/
data class ReviewItem (
    val hospitalId: Long, //병원 레이블 번호
    val reviewId: Long //리뷰 레이블 번호
)

data class CommentItem(
    val content: String, // 댓글 내용
    val author: String, // 댓글 작성자
    val timestamp: String // 댓글 작성 시간
)

// DataModel.kt
data class CommunityItem(
    val imageResource: Bitmap,
    val title: String,
    val writer: String,
    val likes : String,
    val reviews : String,
    val timestamp : String,
    //val commentNumber: Int
    val boardId: Long
)

//PopularHospitalAdapter
data class PopularHospitalItem(
    var score: Int, //순위
    var hospitalId: Long, //병원 레이블 번호
    var hospitalName: String //병원이름
)

fun handleErrorResponse(response: Response<*>) {
    val errorBody = response.errorBody()?.string()
    Log.d("handleErrorResponse", "Response failed: ${response.code()}, error body: ${errorBody ?: "none"}")
    if (errorBody != null) {
        try {
            val jsonObject = JSONObject(errorBody)
            val timestamp = jsonObject.optString("timestamp")
            val status = jsonObject.optInt("status")
            val error = jsonObject.optString("error")
            val message = jsonObject.optString("message")
            val path = jsonObject.optString("path")
            Log.d("ErrorDetails", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
        } catch (e: JSONException) {
            Log.d("JSONParsingError", "Error parsing error body JSON: ${e.localizedMessage}")
        }
    }
}

