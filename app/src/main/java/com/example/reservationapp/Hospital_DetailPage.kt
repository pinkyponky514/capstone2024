package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.ReviewAdapter
import com.example.reservationapp.Custom.CustomReserveDialogFragment
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.BookmarkResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.MyBookmarkResponse
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityHospitalDetailpageExampleAddBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Hospital_DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalDetailpageExampleAddBinding

    //리뷰
    private lateinit var notReviewConstraintLayout: ConstraintLayout //리뷰가 없을때 constraintLayout
    private lateinit var reviewConstraintLayout: ConstraintLayout //리뷰가 있을때 constraintLayout

    private lateinit var noReviewWriteButton: Button //리뷰 없을때 리뷰쓰기 버튼
    private lateinit var reviewWriteButton: Button //리뷰 한개라도 있을때 리뷰쓰기 버튼
    private lateinit var reviewCountTextView: TextView //리뷰 숫자
    private var reviewCount: Int = 0 //리뷰개수

    private lateinit var adapter: ReviewAdapter


    //즐겨찾기
    private var bookmark_flag: Boolean = false


    //
    private lateinit var mainActivity: MainActivity


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBodyDetail: HospitalSignupInfoResponse
    private lateinit var responseBodyMyBookmark: MyBookmarkResponse
    private lateinit var responseBodyBookmark: BookmarkResponse

    //DB에서 가져온 데이터 넣을 view
    private lateinit var hospitalNameTextView: TextView //병원이름
    private lateinit var classNameTextView: TextView //진료과명
    private lateinit var statusTextView: TextView //진료가능 상태
    private lateinit var waitCountTextView: TextView //대기인원
    private lateinit var hospitalPositionTextView: TextView //병원주소
    private lateinit var todayTimeTextView: TextView //금일 운영시간
    private lateinit var hospitalCallTextView: TextView //병원 연락처

    private lateinit var reservationButton: Button //예약 버튼
    private lateinit var favoriteButton: ImageView //즐겨찾기 버튼

    private lateinit var lunchTimeTextView: TextView //점심시간
    private lateinit var mondayTimeTextView: TextView
    private lateinit var tuesdayTimeTextView: TextView
    private lateinit var wednesdayTimeTextView: TextView
    private lateinit var thursdayTimeTextView: TextView
    private lateinit var fridayTimeTextView: TextView
    private lateinit var saturdayTimeTextView: TextView
    private lateinit var sundayTimeTextView: TextView
    private lateinit var dayOffTimeTextView: TextView //공휴일


    private var hospitalString: String = ""


    //
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalDetailpageExampleAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        mainActivity = MainActivity()

        hospitalNameTextView = binding.textViewHospitalName
        classNameTextView = binding.textViewClassName
        statusTextView = binding.textViewStatus
        waitCountTextView = binding.textViewWaitCount
        hospitalPositionTextView = binding.textViewHospitalPos
        todayTimeTextView = binding.textViewTodayTime
        hospitalCallTextView = binding.textViewHospitalCall

        lunchTimeTextView = binding.textViewLunchTime
        mondayTimeTextView = binding.textViewMondayTime
        tuesdayTimeTextView = binding.textViewTuesdayTime
        wednesdayTimeTextView = binding.textViewWednesdayTime
        thursdayTimeTextView = binding.textViewThursdayTime
        fridayTimeTextView = binding.textViewFridayTime
        saturdayTimeTextView = binding.textViewSaturdayTime
        sundayTimeTextView = binding.textViewSundayTime
        dayOffTimeTextView = binding.textViewDayOffTime

        reviewCountTextView = binding.reviewCountTextView

        //리뷰 관련 초기화
        adapter = ReviewAdapter()
        val recyclerView = binding.reviewRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)



        //DB에서 데이터 가져올 것임
        val hospitalId = intent.getLongExtra("hospitalId", 0)
        val hospitalName = intent.getStringExtra("hospitalName").toString()
        val className = binding.textViewClassName.text.toString()
        Log.w("Hospital DetailPage", "hospitalId: $hospitalId")

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


        //상세정보 채우기
        apiService.getHospitalDetail(hospitalId).enqueue(object : Callback<HospitalSignupInfoResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                if(response.isSuccessful) {
                    responseBodyDetail = response.body()!!

                    hospitalString = responseBodyDetail.data.name //병원이름 저장
                    hospitalNameTextView.text = responseBodyDetail.data.hospitalDetail.department //병원 진료과 설정
                    hospitalNameTextView.text = responseBodyDetail.data.name //병원 이름 설정
                    waitCountTextView.text = "${responseBodyDetail.data.reservations.size}명 대기중" //대기인원 설정
                    hospitalPositionTextView.text = responseBodyDetail.data.openApiHospital.address //병원 주소 설정
                    hospitalCallTextView.text = responseBodyDetail.data.openApiHospital.tel //병원 전화번호 설정

                    //금일 운영시간 설정
                    val calendar = Calendar.getInstance()
                    val currentYear = calendar.get(Calendar.YEAR)
                    val currentMonth = calendar.get(Calendar.MONTH)
                    val currentDay = calendar.get(Calendar.DATE)
                    val dayOfWeekTimeList = db_getDayOfWeek(currentYear, currentMonth, currentDay) //현재 요일 오픈, 마감시간 구하기

                    if(dayOfWeekTimeList[1].equals("휴무") || dayOfWeekTimeList[1].equals("정기휴무")) { //금일 시간 설정, 정기휴무나 휴무인 날짜일 경우
                        todayTimeTextView.text = "휴무"
                    } else {
                        todayTimeTextView.text = "${dayOfWeekTimeList[0]}요일 ${dayOfWeekTimeList[1]} ~ ${dayOfWeekTimeList[2]}"
                    }

                    //병원 운영상태 설정
                    var startTime = dayOfWeekTimeList[1]
                    var endTime = dayOfWeekTimeList[2]
                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val currentTimeFormatted = dateFormat.format(calendar.time)

                    //휴무 날짜인지 확인
                    if(dayOfWeekTimeList[1].equals("휴무") || dayOfWeekTimeList[1].equals("정기휴무")) {
                        statusTextView.text = "진료마감"
                    }
                    //현재시간이 운영시간 사이에 있는지 확인
                    if(currentTimeFormatted >= startTime && currentTimeFormatted <= endTime) {
                        statusTextView.text = "진료중"
                    } else {
                        statusTextView.text = "진료마감"
                    }

                    //진료시간 table 설정
                    lunchTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.lunch_start, responseBodyDetail.data.hospitalDetail.lunch_end) //점심시간
                    mondayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.mon_open, responseBodyDetail.data.hospitalDetail.mon_close) //월요일
                    tuesdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.tue_open, responseBodyDetail.data.hospitalDetail.tue_close) //화요일
                    wednesdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.wed_open, responseBodyDetail.data.hospitalDetail.wed_close) //수요일
                    thursdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.thu_open, responseBodyDetail.data.hospitalDetail.thu_close) //목요일
                    fridayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.fri_open, responseBodyDetail.data.hospitalDetail.fri_close) //금요일
                    saturdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.sat_open, responseBodyDetail.data.hospitalDetail.sat_close) //토요일
                    sundayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.sun_open, responseBodyDetail.data.hospitalDetail.sun_close) //일요일
                    dayOffTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospitalDetail.hol_open, responseBodyDetail.data.hospitalDetail.hol_close) //공휴일


                    //리뷰 설정
                    reviewCount = responseBodyDetail.data.review.size
                    reviewCountTextView.text = reviewCount.toString()

                    notReviewConstraintLayout = binding.notReviewConstraintLayout //리뷰가 없을 경우 constraintLayout
                    reviewConstraintLayout = binding.reviewConstraintLayout //리뷰가 있을 경우 constraintLayout
                    notReviewConstraintLayout.visibility = View.GONE
                    reviewConstraintLayout.visibility = View.GONE

                    if(reviewCount == 0) { //리뷰가 하나도 없을 경우
                        notReviewConstraintLayout.visibility = View.VISIBLE
                        reviewConstraintLayout.visibility = View.GONE

                    } else { //리뷰가 한개이상 있을 경우
                        reviewConstraintLayout.visibility = View.VISIBLE
                        notReviewConstraintLayout.visibility = View.GONE

                        val reviewList: ArrayList<ReviewItem> = ArrayList()
                        for(reviewIndex in responseBodyDetail.data.review.indices) {
                            val starScore = responseBodyDetail.data.review[reviewIndex].starScore.toString()
                            val comment = responseBodyDetail.data.review[reviewIndex].comment
                            val reviewDate = responseBodyDetail.data.review[reviewIndex].registerDate
                            val userName = responseBodyDetail.data.review[reviewIndex].user.name

                            reviewList.add(ReviewItem(starScore, comment, reviewDate.toString(), userName))
                        }
                        adapter.updatelist(reviewList)
                        recyclerView.suppressLayout(true) //스트롤 불가능
                    }

                } else Log.w("Hospital_DetailPage FAILURE Response", "Detail Connect SUCESS, Response FAILURE") //통신 성공, 응답 실패
            }

            override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
                Log.w("Hospital_DetailPage CONNECTION FAILURE: ", "Detail Connect FAILURE : ${t.localizedMessage}") //통신 실패
            }
        })


        //즐겨찾기 이미지 설정
        favoriteButton = binding.favoriteImageView
        if(App.prefs.token != null) { //user token이 있으면 == 로그인 했으면
            apiService.getMyHospitalBookmarkList().enqueue(object: Callback<MyBookmarkResponse> { //내가 즐겨찾기 한 병원 불러오기
                override fun onResponse(call: Call<MyBookmarkResponse>, response: Response<MyBookmarkResponse>) {
                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        responseBodyMyBookmark = response.body()!!
                        Log.w("Hospital_DetailPage", "responseBody MyBookmark : $responseBodyMyBookmark, bookmark_flag: $bookmark_flag")

                        if(responseBodyMyBookmark.result.data.boards != null) { //내가 즐겨찾기한 병원 목록이 있다면
                            for(responseMyBookmarkIndex in responseBodyMyBookmark.result.data.boards.indices) {
                                if(responseBodyMyBookmark.result.data.boards[responseMyBookmarkIndex].hospitalId == hospitalId) { //즐겨찾기한 병원 아이디랑 상세페이지 병원 아이디가 같은 경우
                                    favoriteButton.setImageResource(R.drawable.ic_favoritelikes)
                                    bookmark_flag = true
                                    break
                                }
                                //내가 즐겨찾기 한 병원이 있지만, 해당 병원은 아닐 경우
                                favoriteButton.setImageResource(R.drawable.ic_likes)
                            }
                        }
                        else { //나의 즐겨찾기 한 목록이 없다면, 빈 하트
                            favoriteButton.setImageResource(R.drawable.ic_likes)
                        }
                    }

                    //통신 성공, 응답 실패
                    else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("Hospital_DetailPage FAILURE Response", "MyBookmark Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                        if (errorBody != null) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                val timestamp = jsonObject.optString("timestamp")
                                val status = jsonObject.optInt("status")
                                val error = jsonObject.optString("error")
                                val message = jsonObject.optString("message")
                                val path = jsonObject.optString("path")

                                Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                            } catch (e: JSONException) {
                                Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                            }
                        }
                    }
                }

                //통신 실패
                override fun onFailure(call: Call<MyBookmarkResponse>, t: Throwable) {
                    Log.w("Hospital_DetailPage CONNECTION FAILURE: ", "MyBookmark Connect FAILURE : ${t.localizedMessage}")
                }
            })
        }
        else favoriteButton.setImageResource(R.drawable.ic_likes) //user token 없으면, 빈 하트


        //즐겨찾기 버튼 onClick
        favoriteButton.setOnClickListener {
            //user token이 있으면 == 로그인 했으면
            if(App.prefs.token != null) {
                if(bookmark_flag) { //즐겨찾기 한 병원이면, 즐겨찾기 취소
                    bookmark_flag = false
                    favoriteButton.setImageResource(R.drawable.ic_likes)
                    Log.w("Hospital_DetailPage", "즐겨찾기 취소: $bookmark_flag")
                }
                else { //즐겨찾기 안한 병원이면, 즐겨찾기
                    bookmark_flag = true
                    favoriteButton.setImageResource(R.drawable.ic_favoritelikes)
                    Log.w("Hospital_DetailPage", "즐겨찾기 등록 : $bookmark_flag")
                }

                apiService.postHospitalBookmark(hospitalId).enqueue(object: Callback<BookmarkResponse> {
                    override fun onResponse(call: Call<BookmarkResponse>, response: Response<BookmarkResponse>) {
                        //통신, 응답 성공
                        if(response.isSuccessful) {
                            responseBodyBookmark = response.body()!!
                            Log.w("Hospital_DetailPage", "responseBookmark: $responseBodyBookmark")
                        }

                        //통신 성공, 응답 실패
                        else {
                            val errorBody = response.errorBody()?.string()
                            Log.d("FAILURE Response", "Bookmark onClick Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                            if (errorBody != null) {
                                try {
                                    val jsonObject = JSONObject(errorBody)
                                    val timestamp = jsonObject.optString("timestamp")
                                    val status = jsonObject.optInt("status")
                                    val error = jsonObject.optString("error")
                                    val message = jsonObject.optString("message")
                                    val path = jsonObject.optString("path")

                                    Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                                } catch (e: JSONException) {
                                    Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                                }
                            }
                        }
                    }

                    //통신 실패
                    override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                        Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                    }
                })
            }

            //user token이 없으면, 로그인 먼저
            else {
                //다이얼로그 띄워서 로그인 먼저 하고 기능을 이용하라고 코드 구현하기
                val intent = Intent(this, HPDivisonActivity::class.java)
                startActivity(intent)
                //finish()
            }
        }


        //리뷰 하나도 없을때 리뷰쓰기 버튼 onClick
        noReviewWriteButton = binding.noReviewWriteButton
        noReviewWriteButton.setOnClickListener {
            if(App.prefs.token != null) {
                val intent = Intent(this, ReviewWriteDetailActivity::class.java)
                intent.putExtra("hospitalId", hospitalId)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, HPDivisonActivity::class.java)
                startActivity(intent)
            }
        }

        //리뷰 한개라도 있을때 리뷰쓰기 버튼 onClick
        reviewWriteButton = binding.reviewWriteButton
        reviewWriteButton.setOnClickListener {
            if(App.prefs.token != null) {
                val intent = Intent(this, ReviewWriteDetailActivity::class.java)
                intent.putExtra("hospitalId", hospitalId)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, HPDivisonActivity::class.java)
                startActivity(intent)
            }
        }


        // 예약 버튼 클릭 이벤트 설정
        reservationButton = binding.ReservationButton
        reservationButton.setOnClickListener {
            //로그인 되어있으면
            if(App.prefs.token != null) {
                val dialog = CustomReserveDialogFragment.newInstance(hospitalName, className, hospitalId)
                dialog.show(supportFragmentManager, "CustomReserveDialog")
            }

            //로그인 안되어있으면 로그인부터
            else {
                val intent = Intent(this, HPDivisonActivity::class.java)
                startActivity(intent)
            }
        }



        //
    }

    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        finish() //현재 액티비티 종료
    }

    //년, 월, 일 해당하는 날짜의 운영시간 구하기
    private fun db_getDayOfWeek(year:Int, month:Int, day: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> listOf("일", responseBodyDetail.data.hospitalDetail.sun_open, responseBodyDetail.data.hospitalDetail.sun_close)
            Calendar.MONDAY -> listOf("월", responseBodyDetail.data.hospitalDetail.mon_open, responseBodyDetail.data.hospitalDetail.mon_close)
            Calendar.TUESDAY -> listOf("화", responseBodyDetail.data.hospitalDetail.tue_open, responseBodyDetail.data.hospitalDetail.tue_close)
            Calendar.WEDNESDAY -> listOf("수", responseBodyDetail.data.hospitalDetail.wed_open, responseBodyDetail.data.hospitalDetail.wed_close)
            Calendar.THURSDAY -> listOf("목", responseBodyDetail.data.hospitalDetail.thu_open, responseBodyDetail.data.hospitalDetail.thu_close)
            Calendar.FRIDAY -> listOf("금", responseBodyDetail.data.hospitalDetail.fri_open, responseBodyDetail.data.hospitalDetail.fri_close)
            Calendar.SATURDAY -> listOf("토", responseBodyDetail.data.hospitalDetail.sat_open, responseBodyDetail.data.hospitalDetail.sat_close)
            else -> listOf("")
        }
    }

    //운영시간 테이블 값 구하기
    private fun db_getOpenningTime(open: String, close: String): String {
        if(open == "정기휴무" || open == "휴무") {
            return open
        } else {
            return "$open ~ $close"
        }
    }


    //
}
