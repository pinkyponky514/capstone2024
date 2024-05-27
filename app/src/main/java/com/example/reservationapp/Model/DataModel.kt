package com.example.reservationapp.Model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.Calendar

//ReserveAlarmAdapter
data class ReserveItem (
    var hospital_name : String, //예약한 병원이름
    var reservationDate: String, //예약한 날짜
    var reservationTime: String, //예약한 시간
)

/*
//RecentSearchWordAdapter
data class RecentItem (
    var recent_search_word: String //최근 검색한 단어
)
*/
data class RecentItem(
    var recent_search_word: String // 최근 검색한 단어
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(recent_search_word)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentItem> {
        override fun createFromParcel(parcel: Parcel): RecentItem {
            return RecentItem(parcel)
        }

        override fun newArray(size: Int): Array<RecentItem?> {
            return arrayOfNulls(size)
        }
    }
}

//HospitalListAdapter
data class HospitalItem(
    var hospitalId: Long, //병원 레이블 번호
    var hospitalName: String, //병원이름 ㅇ
    var starScore: String, //별점(4.0)
    var openingTimes: String, //영업시간 ㅇ
    var hospitalAddress: String, //병원주소 ㅇ
    var className: List<String>, //진료과 ㅇ
    var status: String, //병원 영업 상태
    //var favoriteCount: Int //즐겨찾기 수
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

//Filter
data class FilterItem(
    var hospitalName: String, //병원이름
    var hospitalAddress: String, //병원주소
    var className: List<String>, //진료과
    var weekTime: HashMap<String, String>, //월~금 진료시간
    var favoriteCount: Int, //병원 즐겨찾기 수
    var reviewList: MutableList<ReviewItem>, //해당 병원 리뷰 리스트, MutableList = 변경가능한 리스트
    var starScore: String, //별점 4.0
    var waitCount: Int, //대기인원
)

//PopularHospitalAdapter
data class PopularHospitalItem(
    var score: Int, //순위
    var hospitalId: Long, //병원 레이블 번호
    var hospitalName: String //병원이름
)


//년, 월, 일 해당하는 날짜의 요일 구하기
fun getDayOfWeek(year:Int, month:Int, day: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return when(dayOfWeek) {
        Calendar.SUNDAY -> "일"
        Calendar.MONDAY -> "월"
        Calendar.TUESDAY -> "화"
        Calendar.WEDNESDAY -> "수"
        Calendar.THURSDAY -> "목"
        Calendar.FRIDAY -> "금"
        Calendar.SATURDAY -> "토"
        else -> ""
    }
}