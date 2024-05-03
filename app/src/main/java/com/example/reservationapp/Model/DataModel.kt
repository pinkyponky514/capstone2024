package com.example.reservationapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


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
data class HospitalItem (
    var hospitalName: String, //병원이름
    var starScore: String, //별점(4.0)
    var openingTimes: String, //영업시간
    var hospitalAddress: String, //병원주소
    var className: String //진료과명
)

//ChattingAdapter
data class ChatItem (
    var user : String, //사람11
    var text : String //메세지
)

//HistoryAdapter
data class HistoryItem (
    var status: String, //진료상태
    var hospitalName: String, //병원이름
    var className: String, //진료과명
    var reserveDate: String, //예약 날짜
)