package com.example.reservationapp.Model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable



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
    var hospitalName: String, //병원이름
    var starScore: String, //별점(4.0)
    var openingTimes: String, //영업시간
    var hospitalAddress: String, //병원주소
    var className: List<String> //진료과명
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
    val status: String // 진료상태

)

//MultiImageAdapter
data class ImageData(
    val uri: Uri
    // 필요한 경우 다른 메타데이터를 추가할 수 있습니다.
)

//MultiImageHospitalAdapter
data class ImageDataHospital(
    val uri: Uri
)

data class ImageItem(
    val imageResId: Int
)


//HistoryAdapter
data class HistoryItem (
    var status: String, //진료상태
    var hospitalName: String, //병원이름
    var className: String, //진료과명
    var reserveDay: String, //예약 날짜
    var reserveTime: String, //예약 시간
): Serializable

//ReviewAdapter
data class ReviewItem (
    var starScore: String, //별점
    var comment: String, //리뷰내용
    var reviewDate: String, //날짜
    var userId: String //유저이름
)

data class CommentItem(
    val content: String, // 댓글 내용
    val author: String, // 댓글 작성자
    val timestamp: String // 댓글 작성 시간
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

//Filter
data class FilterItem (
    var hospitalName: String, //병원이름
    var hospitalAddress: String, //병원주소
    var className: List<String>, //진료과
    var weekTime: HashMap<String, String> //월~금 진료시간
)
