package com.example.reservationapp

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.ReviewAdapter
import com.example.reservationapp.Custom.CustomReserveDialogFragment
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Model.filterList
import com.example.reservationapp.Model.userHospitalFavorite
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityHospitalDetailpageExampleAddBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Hospital_DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalDetailpageExampleAddBinding

    private var not_review_constraint_flag: Boolean = false //리뷰 개수가 0일때의 constraintLayout flag
    private var review_constraint_flag: Boolean = false //리뷰가 있을 경우 constraintLayout flag

    private lateinit var notReviewConstraintLayout: ConstraintLayout //리뷰가 없을때 constraintLayout
    private lateinit var reviewConstraintLayout: RecyclerView //리뷰가 있을때 constraintLayout

    private lateinit var adapter: ReviewAdapter
    private lateinit var mainActivity: MainActivity

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBody: HospitalSignupInfoResponse

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

    private lateinit var reviewCountTextView: TextView //리뷰 숫자

    private var reviewCount: Int = 0 //리뷰개수


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
        apiService.getHospitalDetail(hospitalId).enqueue(object : Callback<HospitalSignupInfoResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                if(response.isSuccessful) {
                    responseBody = response.body()!!

                    hospitalString = responseBody.data.name //병원이름 저장
                    hospitalNameTextView.text = responseBody.data.hospitalDetail.department //병원 진료과 설정
                    hospitalNameTextView.text = responseBody.data.name //병원 이름 설정
                    waitCountTextView.text = "${responseBody.data.reservations.size}명 대기중" //대기인원 설정
                    hospitalPositionTextView.text = responseBody.data.openApiHospital.address //병원 주소 설정
                    hospitalCallTextView.text = responseBody.data.openApiHospital.tel //병원 전화번호 설정

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
                    //lunchTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.lunch_start, responseBody.data.hospitalDetail.lunch_end) //점심시간
                    mondayTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.mon_open, responseBody.data.hospitalDetail.mon_close) //월요일
                    tuesdayTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.tue_open, responseBody.data.hospitalDetail.tue_close) //화요일
                    wednesdayTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.wed_open, responseBody.data.hospitalDetail.wed_close) //수요일
                    thursdayTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.thu_open, responseBody.data.hospitalDetail.thu_close) //목요일
                    fridayTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.fri_open, responseBody.data.hospitalDetail.fri_close) //금요일
                    saturdayTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.sat_open, responseBody.data.hospitalDetail.sat_close) //토요일
                    sundayTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.sun_open, responseBody.data.hospitalDetail.sun_close) //일요일
                    dayOffTimeTextView.text = db_getOpenningTime(responseBody.data.hospitalDetail.hol_open, responseBody.data.hospitalDetail.hol_close) //공휴일


                    //리뷰 설정
                    reviewCount = responseBody.data.review.size
                    reviewCountTextView.text = reviewCount.toString()

                    notReviewConstraintLayout = binding.notReviewConstraintLayout //리뷰가 없을 경우 constraintLayout
                    reviewConstraintLayout = binding.reviewRecyclerView //리뷰가 있을 경우 constraintLayout
                    notReviewConstraintLayout.visibility = View.GONE
                    reviewConstraintLayout.visibility = View.GONE

                    if(reviewCount == 0) { //리뷰가 하나도 없을 경우
                        notReviewConstraintLayout.visibility = View.VISIBLE
                        reviewConstraintLayout.visibility = View.GONE

                    } else { //리뷰가 한개이상 있을 경우
                        reviewConstraintLayout.visibility = View.VISIBLE
                        notReviewConstraintLayout.visibility = View.GONE

                        val reviewList: ArrayList<ReviewItem> = ArrayList()
                        for(reviewIndex in responseBody.data.review.indices) {
                            val starScore = responseBody.data.review[reviewIndex].starScore.toString()
                            val comment = responseBody.data.review[reviewIndex].comment
                            val reviewDate = responseBody.data.review[reviewIndex].registerDate
                            //val userName = responseBody.data.review[reviewIndex].user.name

                            reviewList.add(ReviewItem(starScore, comment, reviewDate.toString(), userName))
                        }
                        adapter.updatelist(reviewList)
                        recyclerView.suppressLayout(true) //스트롤 불가능
                    }

                } else Log.w("FAILURE Response", "Connect SUCESS, Response FAILURE") //통신 성공, 응답 실패
            }

            override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
                Log.w("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
            }
        })



        // 예약 버튼 클릭 이벤트 설정
        reservationButton = binding.ReservationButton
        reservationButton.setOnClickListener {
            val dialog = CustomReserveDialogFragment.newInstance(hospitalName, className)
            dialog.show(supportFragmentManager, "CustomReserveDialog")
        }

        //즐겨찾기 버튼 클릭 이벤트 설정
        favoriteButton = binding.favoriteImageView
        if(userHospitalFavorite[hospitalString] == true) {
            favoriteButton.setImageResource(R.drawable.ic_favoritelikes)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_likes)
        }

        favoriteButton.setOnClickListener {

            if (userHospitalFavorite[hospitalString] == true) { //즐겨찾기 취소
                userHospitalFavorite[hospitalString] = false
                favoriteButton.setImageResource(R.drawable.ic_likes)

                for (filterItem in filterList) {
                    if (filterItem.hospitalName == hospitalString) {
                        filterItem.favoriteCount--
                        Log.w("favroiteCount", "count: ${filterItem.favoriteCount}")
                        break
                    }
                }

            } else { //즐겨찾기
                userHospitalFavorite[hospitalString] = true
                favoriteButton.setImageResource(R.drawable.ic_favoritelikes)

                for (filterItem in filterList) {
                    if (filterItem.hospitalName == hospitalString) {
                        filterItem.favoriteCount++
                        Log.w("favroiteCount", "count: ${filterItem.favoriteCount}")
                        break
                    }
                }
            }

            Log.w("Hospital DetailPage", "favorite Button Boolean : ${userHospitalFavorite}")
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
            Calendar.SUNDAY -> listOf("일", responseBody.data.hospitalDetail.sun_open, responseBody.data.hospitalDetail.sun_close)
            Calendar.MONDAY -> listOf("월", responseBody.data.hospitalDetail.mon_open, responseBody.data.hospitalDetail.mon_close)
            Calendar.TUESDAY -> listOf("화", responseBody.data.hospitalDetail.tue_open, responseBody.data.hospitalDetail.tue_close)
            Calendar.WEDNESDAY -> listOf("수", responseBody.data.hospitalDetail.wed_open, responseBody.data.hospitalDetail.wed_close)
            Calendar.THURSDAY -> listOf("목", responseBody.data.hospitalDetail.thu_open, responseBody.data.hospitalDetail.thu_close)
            Calendar.FRIDAY -> listOf("금", responseBody.data.hospitalDetail.fri_open, responseBody.data.hospitalDetail.fri_close)
            Calendar.SATURDAY -> listOf("토", responseBody.data.hospitalDetail.sat_open, responseBody.data.hospitalDetail.sat_close)
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
