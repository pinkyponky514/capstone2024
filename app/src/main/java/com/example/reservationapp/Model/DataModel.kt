package com.example.reservationapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Calendar


//Retrofit - Model
data class UserSignUpInfo (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String,
    @SerializedName("name") var name: String
)
data class UserLoginInfo (
    @SerializedName("id") var id: String,
    @SerializedName("password") var password: String
)

//ReserveAlarmAdapter
data class ReserveItem (
    var hospital_name : String, //예약한 병원이름
    var date : String //예약한 날짜
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
    var hospitalName: String, //병원이름 ㅇ
    var starScore: String, //별점(4.0)
    var openingTimes: String, //영업시간 ㅇ
    var hospitalAddress: String, //병원주소 ㅇ
    var className: List<String>, //진료과 ㅇ
    //var favoriteCount: Int //즐겨찾기 수
)

//ChattingAdapter
data class ChatItem (
    var user : String, //사람11
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
    val writer: String,
    val likes : String,
    val reviews : String,
    val timestamp : String
    //val commentNumber: Int
)


//HistoryAdapter
data class HistoryItem (
    var status: String, //진료상태
    var hospitalName: String, //병원이름
    var className: String, //진료과명
    var reserveDay: String, //예약 날짜
    var reserveTime: String, //예약 시간
    var reviewWriteBoolean: Boolean //리뷰 썼는지 확인
): Serializable

//ReviewAdapter
data class ReviewItem (
    var starScore: String, //별점
    var comment: String, //리뷰내용
    var reviewDate: String, //날짜
    var userId: String, //유저이름
)

data class CommentItem(
    val title: String, // 리뷰 제목
    val writer: String, // 리뷰 작성자
    val timestamp: String // 리뷰 작성 시간
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

/*
lateinit var filterList: ArrayList<FilterItem> //필터에 사용할 병원정보
lateinit var reviewList: ArrayList<ReviewItem> //병원정보의 리뷰정보
*/

var filterList: ArrayList<FilterItem> = ArrayList() //필터에 사용할 병원정보
var reviewList: ArrayList<ReviewItem> = ArrayList() //병원정보의 리뷰정보

var userHospitalFavorite: HashMap<String, Boolean> = hashMapOf()
