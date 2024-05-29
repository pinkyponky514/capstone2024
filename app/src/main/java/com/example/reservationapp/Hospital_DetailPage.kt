package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.HospitalDetailImageAdapter
import com.example.reservationapp.Adapter.ReviewAdapter
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.BookmarkResponse
import com.example.reservationapp.Model.HospitalSearchResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.MyBookmarkResponse
import com.example.reservationapp.Model.NaverMapApiInterface
import com.example.reservationapp.Model.NaverMapRequest
import com.example.reservationapp.Model.PharmacyMap.NaverMapItem
import com.example.reservationapp.Model.ReservationRequest
import com.example.reservationapp.Model.ReservationResponse
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityHospitalDetailpageExampleAddBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlin.properties.Delegates

class Hospital_DetailPage : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityHospitalDetailpageExampleAddBinding

    //리뷰
    private lateinit var notReviewConstraintLayout: ConstraintLayout //리뷰가 없을때 constraintLayout
    private lateinit var reviewConstraintLayout: ConstraintLayout //리뷰가 있을때 constraintLayout

    private lateinit var noReviewWriteButton: Button //리뷰 없을때 리뷰쓰기 버튼
    private lateinit var reviewWriteButton: Button //리뷰 한개라도 있을때 리뷰쓰기 버튼
    private lateinit var reviewCountTextView: TextView //리뷰 숫자
    private var reviewCount: Int = 0 //리뷰개수

    private lateinit var adapter: ReviewAdapter
    private lateinit var hospitalNameString: String
    private var hospitalDetailId: Long = 0 //병원 상세정보 레이블 번호


    //예약
    private var calendar_open_flag: Boolean = false //달력 펼쳐져 있는지 확인하는 flag
    private var time_open_flag: Boolean = true //시간 펼쳐져 있는지 확인하는 flag
    private lateinit var calendarView: CalendarView //달력 calenderView
    private lateinit var timeTableLayout: TableLayout //시간 tableLayout

    private var reserveDate: String ?= null //예약 날짜 -> intent할때 쓸 변수
    private var reserveTime: String ?= null //예약 시간 -> intent할때 쓸 변수

    private var selectedButton: Button ?= null //선택된 시간 버튼

    //오늘날짜 변수
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0


    //즐겨찾기
    private var bookmark_flag: Boolean = false

    //병원 상세정보 이미지
    private lateinit var hospitalDetailImageAdapter: HospitalDetailImageAdapter

    //
    private lateinit var mainActivity: MainActivity


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBodyDetail: HospitalSearchResponse
    private lateinit var responseBodyMyBookmark: MyBookmarkResponse
    private lateinit var responseBodyBookmark: BookmarkResponse
    private lateinit var responseBodyHospitalDetail: HospitalSearchResponse
    private lateinit var responseBodyReservation: ReservationResponse
    private lateinit var responseBodyHospitalDetailImage: List<String>
    private lateinit var db_lunch_time_start: String //점심 시작 시간
    private lateinit var db_lunch_time_end: String //점심 끝나는 시간


    //DB에서 가져온 데이터 넣을 view
    private lateinit var hospitalNameTextView: TextView //병원이름
    private lateinit var classNameTextView: TextView //진료과명
    private lateinit var statusTextView: TextView //진료가능 상태
    private lateinit var waitCountTextView: TextView //대기인원
    private lateinit var hospitalPositionTextView: TextView //병원주소
    private lateinit var todayTimeTextView: TextView //금일 운영시간
    private lateinit var hospitalCallTextView: TextView //병원 연락처

    private lateinit var reservationButton: Button //예약 버튼
    private lateinit var bottomSheetReservationButton: Button //Bottom Sheet 예약 버튼
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


    private var hospitalId by Delegates.notNull<Long>()

    //예약
    lateinit var reservationBotoomSheetBehavior: BottomSheetBehavior<View>


    private lateinit var mapView: MapView
    private lateinit var mNaverMap: NaverMap

    private var hospitalMapX: Double = 0.0
    private var hospitalMapY:Double = 0.0

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
        tuesdayTimeTextView = binding.textViewMondayTime
        wednesdayTimeTextView = binding.textViewTuesdayTime
        thursdayTimeTextView = binding.textViewThursdayTime
        fridayTimeTextView = binding.textViewFridayTime
        saturdayTimeTextView = binding.textViewSaturdayTime
        sundayTimeTextView = binding.textViewSundayTime
        dayOffTimeTextView = binding.textViewDayOffTime

        reviewCountTextView = binding.reviewCountTextView

        mapView = binding.destinationmap
        mapView?.getMapAsync(this)

        //달력 선택 가능범위 설정 하기위한 변수
        val currentCalendar = Calendar.getInstance()
        currentYear = currentCalendar.get(Calendar.YEAR)
        currentMonth = currentCalendar.get(Calendar.MONTH)
        currentDay = currentCalendar.get(Calendar.DATE)

        val calendarOpenButton = binding.imageViewDateArrowOpen
        var reserveDateTextView = binding.textViewReserveDate //선택한 날짜 textView
        var reserveTimeTextView = binding.textViewReserveTime //선택한 시간 textView


        //리뷰 관련 초기화
        adapter = ReviewAdapter()
        val recyclerView = binding.reviewRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)



        //DB에서 데이터 가져올 것임
        hospitalId = intent.getLongExtra("hospitalId", 0)
        val className = binding.textViewClassName.text.toString()
        Log.w("Hospital DetailPage", "hospitalId: $hospitalId")

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()


        //상세정보 채우기
        apiService.getHospitalDetail(hospitalId).enqueue(object: Callback<HospitalSearchResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<HospitalSearchResponse>, response: Response<HospitalSearchResponse>) {
                if(response.isSuccessful) {
                    responseBodyDetail = response.body()!!

                    hospitalNameString = responseBodyDetail.data.name //병원이름 저장
                    hospitalDetailId = responseBodyDetail.data.hospital.hospitalDetail.detailId //병원 디테일 레이블 번호
                    hospitalNameTextView.text = responseBodyDetail.data.hospital.hospitalDetail.department //병원 진료과 설정
                    hospitalNameTextView.text = responseBodyDetail.data.name //병원 이름 설정
                    hospitalPositionTextView.text = responseBodyDetail.data.hospital.openApiHospital.address //병원 주소 설정
                    hospitalCallTextView.text = responseBodyDetail.data.hospital.openApiHospital.tel //병원 전화번호 설정


                    hospitalMapX = responseBodyDetail.data.hospital.openApiHospital.mapX
                    hospitalMapY = responseBodyDetail.data.hospital.openApiHospital.mapY
                    //병원 이미지 설정
                    var imageList:ArrayList<Bitmap>
                    hospitalDetailImageAdapter = HospitalDetailImageAdapter()
                    val hospitalDetailImageRecyclerView = binding.hospitalDetailImageRecyclerview
                    val hospitalDetailLinearLayoutManager = LinearLayoutManager(this@Hospital_DetailPage, LinearLayoutManager.HORIZONTAL, false)
                    hospitalDetailImageRecyclerView.adapter = hospitalDetailImageAdapter
                    hospitalDetailImageRecyclerView.layoutManager = hospitalDetailLinearLayoutManager

                    apiService.getHospitalDetailImage(hospitalDetailId).enqueue(object: Callback<List<String>> {
                        override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                            if(response.isSuccessful) { //이미지 변환
                                imageList = ArrayList()
                                responseBodyHospitalDetailImage = response.body()!!
                                Log.w("Hospital_DetailPage", "병원 이미지 응답 response body = ${responseBodyHospitalDetailImage}")

                                for(image in responseBodyHospitalDetailImage) {
                                    val decodedBytes: ByteArray = Base64.decode(image, Base64.DEFAULT)
                                    var bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                    imageList.add(bitmap)
                                }
                                hospitalDetailImageAdapter.updatelist(imageList)
                            }

                            else handleErrorResponse(response)
                        }

                        override fun onFailure(call: Call<List<String>>, t: Throwable) {
                            Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                        }
                    })


                    //금일 운영시간 설정
                    val calendar = Calendar.getInstance()
                    val currentYear = calendar.get(Calendar.YEAR)
                    val currentMonth = calendar.get(Calendar.MONTH)
                    val currentDay = calendar.get(Calendar.DATE)
                    val dayOfWeekTimeList = db_getDayOfWeek(currentYear, currentMonth, currentDay) //현재 요일 오픈, 마감시간 구하기
                    val todayLocalDate = LocalDate.of(currentYear, currentMonth+1, currentDay) //2024-05-12, 현재날짜 로컬데이트로 변환

                    var reservationCount = 0
                    for(reservation in responseBodyDetail.data.hospital.reservations) {
                        if(reservation.reservationDate == todayLocalDate && reservation.status == "예약확정") { //예약날짜가 오늘날짜와 같고, 에약확정 상태여야함
                            reservationCount++
                        }
                    }
                    waitCountTextView.text = "${reservationCount}명 대기중" //대기인원 설정


                    if(dayOfWeekTimeList[1].equals("휴무")) { //금일 시간 설정, 휴무인 날짜일 경우
                        todayTimeTextView.text = "휴무"
                    } else if(dayOfWeekTimeList[1].equals("정기휴무")) { //정기휴무 날짜일 경우
                        todayTimeTextView.text = "정기휴무"
                    }else {
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

                    /*
                                        if(statusTextView.text == "진료마감") { //진료마감 됐으면 예약 못함
                                            reservationButton.isEnabled = false
                                            reservationButton.setBackgroundResource(R.drawable.style_gray_radius_20)
                                        }
                    */

                    //진료시간 table 설정
                    db_lunch_time_start = responseBodyDetail.data.hospital.hospitalDetail.lunch_start
                    db_lunch_time_end = responseBodyDetail.data.hospital.hospitalDetail.lunch_end
                    lunchTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.lunch_start, responseBodyDetail.data.hospital.hospitalDetail.lunch_end) //점심시간
                    mondayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.mon_open, responseBodyDetail.data.hospital.hospitalDetail.mon_close) //월요일
                    tuesdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.tue_open, responseBodyDetail.data.hospital.hospitalDetail.tue_close) //화요일
                    wednesdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.wed_open, responseBodyDetail.data.hospital.hospitalDetail.wed_close) //수요일
                    thursdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.thu_open, responseBodyDetail.data.hospital.hospitalDetail.thu_close) //목요일
                    fridayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.fri_open, responseBodyDetail.data.hospital.hospitalDetail.fri_close) //금요일
                    saturdayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.sat_open, responseBodyDetail.data.hospital.hospitalDetail.sat_close) //토요일
                    sundayTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.sun_open, responseBodyDetail.data.hospital.hospitalDetail.sun_close) //일요일
                    dayOffTimeTextView.text = db_getOpenningTime(responseBodyDetail.data.hospital.hospitalDetail.hol_open, responseBodyDetail.data.hospital.hospitalDetail.hol_close) //공휴일


                    //리뷰 설정
                    reviewCount = responseBodyDetail.data.hospital.review.size
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
                        for(review in responseBodyDetail.data.hospital.review) {
                            val reviewId = review.id //리뷰 레이블 번호
                            reviewList.add(ReviewItem(hospitalId, reviewId))
                        }
                        adapter.updatelist(reviewList)
                        recyclerView.suppressLayout(true) //스트롤 불가능
                    }

                } else Log.w("Hospital_DetailPage FAILURE Response", "Detail Connect SUCESS, Response FAILURE") //통신 성공, 응답 실패
            }

            override fun onFailure(call: Call<HospitalSearchResponse>, t: Throwable) {
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
                                    favoriteButton.setImageResource(R.drawable.ic_heart)
                                    bookmark_flag = true
                                    break
                                }
                                //내가 즐겨찾기 한 병원이 있지만, 해당 병원은 아닐 경우
                                favoriteButton.setImageResource(R.drawable.ic_empty_heart)
                            }
                        }
                        else { //나의 즐겨찾기 한 목록이 없다면, 빈 하트
                            favoriteButton.setImageResource(R.drawable.ic_empty_heart)
                        }
                    }

                    //통신 성공, 응답 실패
                    else handleErrorResponse(response)
                }

                //통신 실패
                override fun onFailure(call: Call<MyBookmarkResponse>, t: Throwable) {
                    Log.w("Hospital_DetailPage CONNECTION FAILURE: ", "MyBookmark Connect FAILURE : ${t.localizedMessage}")
                }
            })
        }
        else favoriteButton.setImageResource(R.drawable.ic_empty_heart) //user token 없으면, 빈 하트


        //즐겨찾기 버튼 onClick
        favoriteButton.setOnClickListener {
            //user token이 있으면 == 로그인 했으면
            if(App.prefs.token != null) {
                if(bookmark_flag) { //즐겨찾기 한 병원이면, 즐겨찾기 취소
                    bookmark_flag = false
                    favoriteButton.setImageResource(R.drawable.ic_empty_heart)
                    Log.w("Hospital_DetailPage", "즐겨찾기 취소: $bookmark_flag")
                }
                else { //즐겨찾기 안한 병원이면, 즐겨찾기
                    bookmark_flag = true
                    favoriteButton.setImageResource(R.drawable.ic_heart)
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
                        else handleErrorResponse(response)
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
                val intent = Intent(this, ReviewAbleListActivity::class.java)
                intent.putExtra("hospitalId", hospitalId)
                intent.putExtra("hospitalName", hospitalNameString)
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
                val intent = Intent(this, ReviewAbleListActivity::class.java)
                intent.putExtra("hospitalId", hospitalId)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, HPDivisonActivity::class.java)
                startActivity(intent)
            }
        }



        //뒷배경
        val backgroundView = binding.backgroundView
        val bottomSheetCallback = object: BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //상태가 EXPANDED, COLLAPSED일때 뒷배경 불투명
                if(newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_EXPANDED) {
                    backgroundView.visibility = View.VISIBLE
                } else {
                    backgroundView.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if(-1<slideOffset || slideOffset<=1) {
                    backgroundView.visibility = View.VISIBLE
                } else {
                    backgroundView.visibility = View.GONE
                }
            }
        }

        //예약 Bottom Sheet 초기화
        val reservationBottomSheet = binding.hospitalDetailReservationBottomSheet
        reservationBotoomSheetBehavior = BottomSheetBehavior.from(reservationBottomSheet)
        reservationBotoomSheetBehavior.isHideable = true
        reservationBotoomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        reservationBotoomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        // 예약하기 버튼 -> 예약창 Bottom Sheet 보이게
        reservationButton = binding.ReservationButton
        reservationButton.setOnClickListener {
            //로그인 되어있으면
            if(App.prefs.token != null) {
                reservationBotoomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            //로그인 안되어있으면 로그인부터
            else {
                val intent = Intent(this, HPDivisonActivity::class.java)
                startActivity(intent)
            }
        }

        //배경 클릭했을때 Bottom Sheet 사라지게 상태 설정
        backgroundView.setOnClickListener {
            reservationBotoomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        //
        timeTableLayout = binding.tableLayoutReserveTime //시간 선택하는 TableLayout
        calendarView = binding.calendarView //달력 뷰

        //달력 펼치는 버튼 눌렀을 경우
        calendarView.visibility = View.GONE
        calendarOpenButton.setOnClickListener {
            toggleCalendarVisibility()
        }
        reserveDateTextView.setOnClickListener {
            toggleCalendarVisibility()
        }


        //달력에서 날짜 선택 변경감지
        bottomSheetReservationButton = binding.reserveButton
        calendarView.setOnDateChangeListener { view, year, month, day -> //view = CalendarView
            val dayOfWeek = db_getDayOfWeek(year, month, day) //월
            val dateString = String.format("%d-%d-%d", year, month+1, day) //2024-5-20
            val dayOfWeekString = "(${dayOfWeek[0]})" //(월)

            reserveDateTextView.text = String.format("%d.%d.%d ", year, month+1, day) + " $dayOfWeekString" //2024.05.20
            reserveDate = dateString //2024-5-20

            //날짜에 따른 예약 가능한 시간 다르게
            apiService.getHospitalDetail(hospitalId).enqueue(object: Callback<HospitalSearchResponse> {
                override fun onResponse(call: Call<HospitalSearchResponse>, response: Response<HospitalSearchResponse>) {
                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        reserveTime = null; reserveTimeTextView.text = null //새로운 날짜 선택으로 인해 시간 지우기
                        timeTableLayout.removeAllViews() //이전의 시간 테이블 모두 지우기
                        responseBodyHospitalDetail = response.body()!!
                        Log.w("CustomREserveDialogFragment", "responseBodyHospitalDetail: $responseBodyHospitalDetail")

                        val startEndTimeList =  db_getDayOfWeek(year, month, day)
                        val startTime =  startEndTimeList[1]
                        val endTime = startEndTimeList[2]

                        var selectDateFormat = LocalDate.of(year, month+1, day) //2024-05-12


                        if(startTime == "휴무" || startTime == "정기휴무")  { //휴무나 정기휴무이면 오늘날짜로
                            calendarView.date = Calendar.getInstance().timeInMillis //오늘날짜
                            CustomToast(this@Hospital_DetailPage, "휴무 날짜는 선택할 수 없습니다.").show()

                            val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()) //2024.05.24 표시용
                            val dayFormat = SimpleDateFormat("EEE", Locale.KOREA)
                            val dateString = dateFormat.format(calendarView.date)
                            val dayString = dayFormat.format(calendarView.date)

                            reserveDateTextView.text = "$dateString ($dayString)"

                            val db_dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val db_dateString = db_dateFormat.format(calendarView.date) //2024-05-24 request용
                            reserveDate = db_dateString
                        } else {
                            val startTimeParts = startTime.split(":") //시간, 분 분리
                            val endTimeParts = endTime.split(":")

                            val startHour = startTimeParts[0].toInt() //오픈하는 hour
                            val endHour = endTimeParts[0].toInt() //문닫는 hour

                            val startMinute = startTimeParts[1].toInt() //오픈하는 minute
                            val endMinute = endTimeParts[1].toInt() //문닫는 minute

                            //시간 동적으로 넣기 (시간이 다 찼을 경우는 안그리기 위해서)
                            val rowSize = 4 //한 행에 들어갈 버튼 수
                            var tableRow: TableRow?= null //동적으로 추가하기 위함, 아직 테이블에 아무 행도 없기 때문에 id를 찾을 수 없음
                            var buttonCountInRow = 0 //현재 행에 추가된 버튼 개수
                            var buttonCount = 0 //현재 버튼 개수

                            for (hour in startHour..endHour) {
                                for (minute in arrayOf(0, 30)) { //30분 단위로 추가
                                    if (buttonCountInRow == rowSize || buttonCount%4 == 0) {
                                        buttonCountInRow = 0 //한 행에 버튼 개수 초기화
                                    }

                                    if (buttonCountInRow == 0) { //새로운 행 시작
                                        tableRow = TableRow(this@Hospital_DetailPage)
                                        tableRow?.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                        //tableRow?.setPadding(5,5,5,5)
                                        //tableRow?.gravity = Gravity.CENTER
                                        timeTableLayout.addView(tableRow)
                                    }

                                    if(hour == startHour && startMinute == 30 && minute == 0) { //오픈시간 minute가 9:30분부터 시작이면, 9:00는 건너뛰기
                                        continue
                                    }
                                    if(hour == endHour && endMinute == 0 && minute == 30) { //문닫는 시간 minute가 14:00 이라면, 14:30는 건너뛰기
                                        continue
                                    }

                                    val button = Button(this@Hospital_DetailPage)
                                    val timeString = String.format("%02d:%02d", hour, minute)
                                    val timeFormat = LocalTime.of(hour, minute) //09:00

                                    if(timeString == db_lunch_time_start || timeString == db_lunch_time_end) { //점심시간이면 건너뛰기
                                        continue
                                    } else if(timeString in db_lunch_time_start..db_lunch_time_end) { //점심시간 사이의 시간도 건너뛰기
                                        continue
                                    }

                                    button.text = timeString
                                    button.setBackgroundResource(R.drawable.style_button_focus)

                                    //같은 날짜와 같은 예약된 시간 비활성화
                                    for(reservation in responseBodyHospitalDetail.data.hospital.reservations) {
                                        if(reservation.reservationDate == selectDateFormat && reservation.reservationTime == timeFormat) { //선택한 날짜와 같은 예약된 날짜 거르기
                                            button.isEnabled = false
                                            button.setBackgroundResource(R.drawable.style_gray_radius_5)
                                        }
                                    }


                                    button.setOnClickListener {
                                        //val timeString = button.text
                                        reserveTimeTextView.text = timeString //시간 선택 textView
                                        reserveTime = timeString
                                        reservationButtonEnabled()
                                        selectButtonColor(button) //시간 선택하면 색 변경하도록
                                    }
                                    tableRow?.addView(button)
                                    buttonCountInRow++
                                    buttonCount++
                                }
                            }
                        }
                        reservationButtonEnabled()
                    }

                    //통신 성공, 응답 실패
                    else handleErrorResponse(response)
                }

                //통신 실패
                override fun onFailure(call: Call<HospitalSearchResponse>, t: Throwable) {
                    Log.w("CONNECTION FAILURE: ", "Connect FAILURE : ${t.localizedMessage}")
                }
            })
        }


        //시간 펼치는 버튼 눌렀을 경우
        val timeOpenButton = binding.imageViewTimeArrowOpen
        val timeTableLayout = binding.tableLayoutReserveTime
        timeTableLayout.visibility = View.VISIBLE
        timeOpenButton.setOnClickListener {
            toggleTimeVisibility()
        }
        reserveTimeTextView.setOnClickListener {
            toggleTimeVisibility()
        }



        // 예약 버튼 클릭 onClick
        bottomSheetReservationButton.setOnClickListener {
            Log.d("Date, Time", "reserveDate: $reserveDate, reserveTime: $reserveTime")

            // reserveDate와 reserveTime을 LocalDate와 LocalTime으로 파싱
            val reservationLocalDate = LocalDate.parse(reserveDate, DateTimeFormatter.ofPattern("yyyy-M-d")) //yyyy-M-d
            val reservationLocalTime = LocalTime.parse(reserveTime, DateTimeFormatter.ofPattern("H:mm")) //H:mm

            // LocalDate와 LocalTime을 LocalDateTime으로 변환
            val reservationLocalDateTime = LocalDateTime.of(reservationLocalDate, reservationLocalTime)

            // 원하는 포맷으로 변환
            val dateFormatter = reservationLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val timeFormatter = reservationLocalDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            Log.d("Date, Time", "LocalDate: $dateFormatter, LocalTime: $timeFormatter")

            val reservation = ReservationRequest(dateFormatter, timeFormatter, hospitalId)
            apiService.postReservation(reservation).enqueue(object: Callback<ReservationResponse> {
                override fun onResponse(call: Call<ReservationResponse>, response: Response<ReservationResponse>) {
                    if(response.isSuccessful) {
                        responseBodyReservation = response.body()!!
                        Log.d("CustomReserveDialogFragment", "reserve response.body() : $responseBodyReservation")

                        val intent = Intent(this@Hospital_DetailPage, CheckReservationActivity::class.java)
                        intent.putExtra("reservationResponse", responseBodyReservation)
                        startActivity(intent)
                    }

                    else handleErrorResponse(response)
                }

                override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                    Log.w("CONNECTION FAILURE: ", "Reservation Connect FAILURE : ${t.localizedMessage}")
                }
            })
        }

        //
    }

    override fun onResume() {
        super.onResume()

        val minDate = Calendar.getInstance() //캘린더에서 선택할 수 있는 최소날짜
        val maxDate = Calendar.getInstance() //캘린더에서 선택할 수 있는 최대날짜

        //오늘부터 선택 가능하도록
        minDate.set(currentYear, currentMonth, currentDay)
        calendarView.minDate = minDate.timeInMillis

        //올해말까지 선택 가능하도록 (12/31)
        maxDate.set(currentYear, 11, 31)
        calendarView.maxDate = maxDate.timeInMillis
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
            Calendar.SUNDAY -> listOf("일", responseBodyDetail.data.hospital.hospitalDetail.sun_open, responseBodyDetail.data.hospital.hospitalDetail.sun_close)
            Calendar.MONDAY -> listOf("월", responseBodyDetail.data.hospital.hospitalDetail.mon_open, responseBodyDetail.data.hospital.hospitalDetail.mon_close)
            Calendar.TUESDAY -> listOf("화", responseBodyDetail.data.hospital.hospitalDetail.tue_open, responseBodyDetail.data.hospital.hospitalDetail.tue_close)
            Calendar.WEDNESDAY -> listOf("수", responseBodyDetail.data.hospital.hospitalDetail.wed_open, responseBodyDetail.data.hospital.hospitalDetail.wed_close)
            Calendar.THURSDAY -> listOf("목", responseBodyDetail.data.hospital.hospitalDetail.thu_open, responseBodyDetail.data.hospital.hospitalDetail.thu_close)
            Calendar.FRIDAY -> listOf("금", responseBodyDetail.data.hospital.hospitalDetail.fri_open, responseBodyDetail.data.hospital.hospitalDetail.fri_close)
            Calendar.SATURDAY -> listOf("토", responseBodyDetail.data.hospital.hospitalDetail.sat_open, responseBodyDetail.data.hospital.hospitalDetail.sat_close)
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
            bottomSheetReservationButton.isEnabled = true
            reservationButtonEnabledBackgroundResource()

        } else if(reserveDate == null || reserveTime == null) { //예약 버튼 비활성화
            Log.w("reserveDate || reserveTime", "reserveDate or reserveTime is null")
            bottomSheetReservationButton.isEnabled = false
            reservationButtonEnabledBackgroundResource()
        }
    }

    //예약 시간 클릭시 버튼 색상 변경
    private fun selectButtonColor(clickedButton: Button) {
        //선택된 버튼이 없거나 클릭된 버튼이 아닐 경우
        if(selectedButton == null || selectedButton != clickedButton) {
            selectedButton?.setBackgroundResource(R.drawable.style_button_focus) //이전에 선택된 버튼의 색을 원래대로 되돌림
            clickedButton.setBackgroundResource(R.drawable.style_dark_green_line_light_green_radius_5) //현재 클릭된 버튼 색 변경
            selectedButton = clickedButton
        }
    }

    //버튼 비활성화, 활성화 배경 색
    private fun reservationButtonEnabledBackgroundResource() {
        if(reservationButton.isEnabled) {
            bottomSheetReservationButton.setBackgroundResource(R.drawable.style_dark_green_radius_20_pressed_button)
        } else {
            bottomSheetReservationButton.setBackgroundResource(R.drawable.style_gray_radius_20)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap

        apiService.getHospitalDetail(hospitalId).enqueue(object: Callback<HospitalSearchResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<HospitalSearchResponse>, response: Response<HospitalSearchResponse>) {
                if(response.isSuccessful) {
                    responseBodyDetail = response.body()!!

                    hospitalMapX = responseBodyDetail.data.hospital.openApiHospital.mapY
                    hospitalMapY = responseBodyDetail.data.hospital.openApiHospital.mapX
                    //병원 이미지 설정

                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(hospitalMapX, hospitalMapY))
                    naverMap.moveCamera(cameraUpdate)

                    val locationOverlay = mNaverMap.locationOverlay
                //    locationOverlay.isVisible = true
                    locationOverlay.position = LatLng(hospitalMapX, hospitalMapY)
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    naverMap.uiSettings.isLocationButtonEnabled = false

                    val naverMapApiInterface = NaverMapRequest.getClient().create(NaverMapApiInterface::class.java)


                    val marker = Marker()
                    marker.position = LatLng(hospitalMapX, hospitalMapY)
                    marker.icon = OverlayImage.fromResource(R.drawable.hospital_pin)
                    marker.width = 130
                    marker.height = 130
                    marker.map = mNaverMap
                    marker.map = mNaverMap
                }

                //통신 성공, 응답 실패
                else handleErrorResponse(response)
            }

            //통신 실패
            override fun onFailure(call: Call<HospitalSearchResponse>, t: Throwable) {
                Log.w("CONNECTION FAILURE: ", "Connect FAILURE : ${t.localizedMessage}")
            }
        })

//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(hospitalMapX, hospitalMapY))
//        naverMap.moveCamera(cameraUpdate)
//
//        val locationOverlay = mNaverMap.locationOverlay
//    //    locationOverlay.isVisible = true
//        locationOverlay.position = LatLng(hospitalMapX, hospitalMapY)
//        naverMap.locationTrackingMode = LocationTrackingMode.Follow
//        naverMap.uiSettings.isLocationButtonEnabled = true
//
//        val naverMapApiInterface = NaverMapRequest.getClient().create(NaverMapApiInterface::class.java)
//
//
//        val marker = Marker()
//        marker.position = LatLng(hospitalMapX, hospitalMapY)
//        marker.icon = OverlayImage.fromResource(R.drawable.hospital_pin)
//        marker.width = 100
//        marker.height = 100
//        marker.map = mNaverMap
//        marker.map = mNaverMap
    }


    //
}