package com.example.reservationapp

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.ReviewAdapter
import com.example.reservationapp.Model.filterList
import com.example.reservationapp.Model.getDayOfWeek
import com.example.reservationapp.Model.reviewList
import com.example.reservationapp.Model.userHospitalFavorite
import com.example.reservationapp.databinding.ActivityHospitalDetailpageExampleAddBinding
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


    //DB에서 가져온 데이터 넣을 view
    private lateinit var hospitalNameTextView: TextView //병원이름
    private lateinit var classNameTextView: TextView //진료과명
    private lateinit var statusTextView: TextView //진료가능 상태
    private lateinit var waitCountTextView: TextView //대기인원
    private lateinit var hospitalPositionTextView: TextView //병원위치
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

    private var reviewCount: Int = 0 //리뷰개수

    private var hospitalString: String = ""

    //
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

        //리뷰 관련 초기화
        adapter = ReviewAdapter()
        val recyclerView = binding.reviewRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)



        //DB에서 데이터 가져올 것임
        val hospitalName = intent.getStringExtra("hospitalName").toString()
        val className = binding.textViewClassName.text.toString()
        Log.w("Hospital DetailPage", "filterList: $filterList")
        for(i in filterList.indices) {
            if(filterList[i].hospitalName == hospitalName) { //넘겨받은 병원이름으로 DB에서 데이터 가져올 것임

                hospitalString = filterList[i].hospitalName

                //병원이름
                hospitalNameTextView.text = filterList[i].hospitalName

                //진료과명
                var className = ""
                if(filterList[i].className.size > 1) {
                    for(j in filterList[i].className.indices-1) {
                        className += "${filterList[i].className[j]} | "
                    }
                }
                className += filterList[i].className[filterList[i].className.lastIndex]
                classNameTextView.text = className


                waitCountTextView.text = "${filterList[i].waitCount}명 대기중" //대기인원
                hospitalPositionTextView.text = filterList[i].hospitalAddress //병원주소

                //금일 운영시간
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentDay = calendar.get(Calendar.DATE)
                val dayOfWeek = getDayOfWeek(currentYear, currentMonth, currentDay) //현재 요일 구하기
                val dayTime = filterList[i].weekTime[dayOfWeek] //현재 요일에 맞는 영업시간 가져오기
                todayTimeTextView.text = "${dayOfWeek}요일 ${dayTime}"

                //병원 운영상태
                val timeSplit = dayTime.toString().split("~")
                var startTime = ""
                var endTime = ""
                if(timeSplit.size > 1) {
                    startTime = timeSplit[0]
                    endTime = timeSplit[1]
                } else {
                    startTime = timeSplit[0]
                    endTime = timeSplit[0]
                }

                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTimeFormatted = dateFormat.format(calendar.time)

                //현재시간이 운영시간 사이에 있는지 확인
                if(currentTimeFormatted >= startTime && currentTimeFormatted <= endTime) {
                    statusTextView.text = "진료중"
                } else {
                    statusTextView.text = "진료마감"
                }


                //진료시간 table
                lunchTimeTextView.text = filterList[i].weekTime["점심"]
                mondayTimeTextView.text = filterList[i].weekTime["월"]
                tuesdayTimeTextView.text = filterList[i].weekTime["화"]
                wednesdayTimeTextView.text = filterList[i].weekTime["수"]
                thursdayTimeTextView.text = filterList[i].weekTime["목"]
                fridayTimeTextView.text = filterList[i].weekTime["금"]
                saturdayTimeTextView.text = filterList[i].weekTime["토"]
                sundayTimeTextView.text = filterList[i].weekTime["일"]
                dayOffTimeTextView.text = filterList[i].weekTime["공휴일"]


                //리뷰 설정
                //
                reviewCount = filterList[i].reviewList.size
                val reviewCountTextView = binding.reviewCountTextView
                reviewCountTextView.text = reviewCount.toString() //리뷰개수 textView에 표시

                notReviewConstraintLayout = binding.notReviewConstraintLayout //리뷰가 없을 경우 constraintLayout
                reviewConstraintLayout = binding.reviewRecyclerView //리뷰가 있을 경우 constraintLayout
                notReviewConstraintLayout.visibility = View.GONE
                reviewConstraintLayout.visibility = View.GONE

                if(reviewCount == 0) { //리뷰가 하나도 없을 경우
                    not_review_constraint_flag = true
                    notReviewConstraintLayout.visibility = View.VISIBLE

                    review_constraint_flag = false
                    reviewConstraintLayout.visibility = View.GONE

                } else { //리뷰가 한개이상 있을 경우
                    review_constraint_flag = true
                    reviewConstraintLayout.visibility = View.VISIBLE

                    not_review_constraint_flag = false
                    notReviewConstraintLayout.visibility = View.GONE

                    adapter.updatelist(filterList[i].reviewList)
                    recyclerView.suppressLayout(true) //스트롤 불가능
                }

                break
            }
        }



        // 예약 버튼 클릭 이벤트 설정
        reservationButton = binding.ReservationButton
        reservationButton.setOnClickListener {
            val dialog = CustomReserveDialogActivity.newInstance(hospitalName, className)
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
}
