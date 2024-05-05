package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.ReviewAdapter
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.databinding.ActivityHospitalDetailpageExampleAddBinding
import java.util.Calendar

class Hospital_DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalDetailpageExampleAddBinding

    private var calendar_open_flag: Boolean = false //달력 펼쳐져 있는지 확인하는 flag
    private var time_open_flag: Boolean = false //시간 펼쳐져 있는지 확인하는 flag
    private lateinit var calendarView: CalendarView //달력 calenderView
    private lateinit var timeTableLayout: TableLayout //시간 tableLayout


    private var not_review_constraint_flag: Boolean = false //리뷰 개수가 0일때의 constraintLayout flag
    private var review_constraint_flag: Boolean = false //리뷰가 있을 경우 constraintLayout flag
    private lateinit var notReviewConstraintLayout: ConstraintLayout
    private lateinit var reviewConstraintLayout: RecyclerView


    private lateinit var adapter: ReviewAdapter
    private lateinit var reviewList: ArrayList<ReviewItem>


    private var reserveTimeList: List<String> = listOf("9:00", "10:00", "10:30", "11:00", "12:00", "14:00", "14:30", "15:30", "18:00") //예약 가능한 시간 넣는 리스트

    //DB에서 가져온 데이터 넣을 view
    private lateinit var hospitalNameTextView: TextView //병원이름
    private lateinit var classNameTextView: TextView //진료과명
    private lateinit var statusTextView: TextView //진료가능 상태
    private lateinit var waitCountTextView: TextView //대기인원
    private lateinit var hospitalPositionTextView: TextView //병원위치
    private lateinit var todayTimeTextView: TextView //금일 운영시간
    private lateinit var hospitalCallTextView: TextView //병원 연락처
    private lateinit var reservationButton: Button //예약 버튼

    private var reserveDate: String ?= null //예약 날짜 -> intent할때 쓸 변수
    private var reserveTime: String ?= null //예약 시간 -> intent할때 쓸 변수

    //오늘날짜 변수
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0

    private var reviewCount: Int = 0 //리뷰개수

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalDetailpageExampleAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //넘겨받은 병원이름으로 DB에서 데이터 가져올 것임
        val hospitalName = intent.getStringExtra("hospitalName").toString()
        hospitalNameTextView = binding.textViewHospitalName
        hospitalNameTextView.text = hospitalName
        Log.w("GetExtra Hospital Name", "GetExtra hospital Name: ${hospitalName}")


        //달력 선택 가능범위 설정 하기위한 변수
        val currentCalendar = Calendar.getInstance()
        currentYear = currentCalendar.get(Calendar.YEAR)
        currentMonth = currentCalendar.get(Calendar.MONTH)
        currentDay = currentCalendar.get(Calendar.DATE)


        //달력 펼치는 버튼 눌렀을 경우
        val calendarOpenButton = binding.imageViewDateArrowOpen
        var reserveDateTextView = binding.textViewReserveDate //선택한 날짜 textView
        calendarView = binding.calendarView
        calendarView.visibility = View.GONE
        calendarOpenButton.setOnClickListener {
            toggleCalendarVisibility()
        }
        reserveDateTextView.setOnClickListener {
            toggleCalendarVisibility()
        }
        //달력에서 날짜 선택 변경감지
        reservationButton = binding.ReservationButton
        calendarView.setOnDateChangeListener { view, year, month, day -> //view = CalendarView
            val dayOfWeek = getDayOfWeek(year, month, day)
            val dateString = String.format("%d.%d.%d ", year, month+1, day) + "(" + dayOfWeek + ")"
            reserveDateTextView.text = dateString
            reserveDate = dateString
        }




        //시간 동적으로 넣기 (시간이 다 찼을 경우는 안그리기 위해서)
        //DB에서 예약 가능한 시간 가져와서 list 재설정 해줘야함
        val rowSize = 4 //한 행에 들어갈 버튼 수
        var tableRow: TableRow ?= null //동적으로 추가하기 위함, 아직 테이블에 아무 행도 없기 때문에 id를 찾을 수 없음
        var buttonCountInRow = 0 //현재 행에 추가된 버튼 개수
        var reserveTimeTextView = binding.textViewReserveTime //선택한 시간 textView
        timeTableLayout = binding.tableLayoutReserveTime

        for(i in reserveTimeList.indices) {
            if(buttonCountInRow == rowSize || (i%4 == 0) ) {
                buttonCountInRow = 0 //한 행에 버튼 개수 초기화
            }

            if(buttonCountInRow == 0) { //새로운 행 시작
                tableRow = TableRow(this)
                tableRow?.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                timeTableLayout.addView(tableRow)
            }

            val button = Button(this)
            button.text = reserveTimeList[i]
            button.setOnClickListener {//시간 선택 변경 감지했을 때
                val timeString = button.text
                reserveTimeTextView.text = timeString
                reserveTime = timeString.toString()
            }

            tableRow?.addView(button)
            buttonCountInRow++
        }
        //시간 펼치는 버튼 눌렀을 경우
        val timeOpenButton = binding.imageViewTimeArrowOpen
        val timeTableLayout = binding.tableLayoutReserveTime
        timeTableLayout.visibility = View.GONE
        timeOpenButton.setOnClickListener {
            toggleTimeVisibility()
        }
        reserveTimeTextView.setOnClickListener {
            toggleTimeVisibility()
        }



        // 예약 버튼 클릭 이벤트 설정
        reservationButton.setOnClickListener {
            Log.w("reservationButton setOnClickListener", "Date:${reserveDate}, Time:${reserveTime}")
            val intent = Intent(this, Final_Reservation::class.java)
            startActivity(intent)
            finish()
        }



        //리뷰 설정
        adapter = ReviewAdapter()

        val recyclerView = binding.reviewRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

        //리뷰 목록 리스트 초기화 (DB에서 가져오기)
        reviewList = ArrayList()
        reviewList.add(ReviewItem("4.0", "병원이 너무 좋아요", "2024.04.01", "hansung"))
        reviewList.add(ReviewItem("3.0", "솔직하게 말하자면 병원이 크고 시설도 다 좋은데, 의사 선생님이나 간호사 분들이 너무 불친절합니다.", "2024.04.05", "user1"))
        reviewList.add(ReviewItem("4.5", "다닐만 합니다", "2024.04.20", "user2"))
        reviewList.add(ReviewItem("2.0", "대기 시간도 너무 길고 신설 병원이라 그런지 너무 체계가 엉망입니다", "2024.05.01", "user3"))
        reviewList.add(ReviewItem("4.0", "병원이 너무 좋아요", "2024.05.03", "user4"))
        reviewList.add(ReviewItem("4.5", "병원이 너무 좋아요", "2024.05.05", "user5"))

        //
        reviewCount = reviewList.size
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

            adapter.updatelist(reviewList)
            recyclerView.suppressLayout(true)
        }



    }

    override fun onResume() {
        super.onResume()

        val minDate = Calendar.getInstance() //캘린더에서 선택할 수 있는 최소날짜
        val maxDate = Calendar.getInstance() //캘린더에서 선택할 수 있는 최대날짜 (12/31)

        //오늘부터 선택 가능하도록
        minDate.set(currentYear, currentMonth,currentDay)
        calendarView.minDate = minDate.timeInMillis

        //올해말까지 선택 가능하도록
        maxDate.set(currentYear, 12, 31)
        calendarView.maxDate = maxDate.timeInMillis
    }


    //
    //년, 월, 일 해당하는 날짜의 요일 구하기
    private fun getDayOfWeek(year:Int, month:Int, day: Int): String {
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

    //달력 펼치기, 접기
    private fun toggleCalendarVisibility() {
        if(calendar_open_flag) { //true=열려있는 상태, 달력 접기
            calendar_open_flag = false
            calendarView.visibility = View.GONE //gone = 화면에 보이지 않으며, 자리차지도 안함
        } else { //false=닫혀있는 상태, 달력 접기
            calendar_open_flag = true
            calendarView.visibility = View.VISIBLE
        }
    }
    //시간 펼치기, 접기
    private fun toggleTimeVisibility() {
        if(time_open_flag) { //true=열려있는 상태, 시간 접기
            time_open_flag = false
            timeTableLayout.visibility = View.GONE
        } else { //false=닫혀있는 상태, 시간 열기
            time_open_flag = true
            timeTableLayout.visibility = View.VISIBLE
        }
    }

    //예약 버튼 enabled
    private fun reservationButtonEnabled() {
        if(reserveDate != null && reserveTime != null) { //예약 버튼 활성화
            Log.w("reserveDate && reserveTime", "reserveDate and reserveTime not null")
            reservationButton.isEnabled = true
        } else if(reserveDate == null || reserveTime == null) { //예약 버튼 비활성화
            Log.w("reserveDate || reserveTime", "reserveDate or reserveTime is null")
            reservationButton.isEnabled = false
        }
    }

    //int를 dp로 바꾸기
    private fun changeDP(value : Int) : Int{
        var displayMetrics = resources.displayMetrics
        var dp = (value * displayMetrics.density).toInt() //var dp = Math.round(value * displayMetrics.density)
        return dp
    }
    //
}
