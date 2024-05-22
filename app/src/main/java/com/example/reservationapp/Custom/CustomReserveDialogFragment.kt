package com.example.reservationapp.Custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.CalendarView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.reservationapp.CheckReservationActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReservationRequest
import com.example.reservationapp.Model.ReservationResponse
import com.example.reservationapp.Retrofit.RetrofitClient

import com.example.reservationapp.databinding.FragmentCustomReserveDialogBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class CustomReserveDialogFragment() : DialogFragment() {
    private lateinit var binding: FragmentCustomReserveDialogBinding
    private var thisHospitalName: String = ""
    private var thisClassName: String = ""
    private var thisHospitalId: Long = 0 //병원 레이블 번호

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

    //DB에서 가져온 데이터 넣을 view
    private lateinit var hospitalNameTextView: TextView //병원이름
    private lateinit var reservationButton: Button //예약 버튼


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBodyHospitalDetail: HospitalSignupInfoResponse
    private lateinit var responseBodyReservation: ReservationResponse


    companion object {
        private const val ARG_STRING_HOSPITAL_NAME = "arg_string_hospital_name"
        private const val ARG_STRING_CLASS_NAME = "arg_string_class_name"
        private const val ARG_STRING_HOSPITAL_ID = "arg_string_hospital_id"

        fun newInstance(hospitalName: String, className: String, hospitalId: Long): CustomReserveDialogFragment {
            return CustomReserveDialogFragment().apply {
                arguments = Bundle().apply {//arguments = bundleOf(ARG_LIST to newList)
                    putString(ARG_STRING_HOSPITAL_NAME, hospitalName)
                    putString(ARG_STRING_CLASS_NAME, className)
                    putLong(ARG_STRING_HOSPITAL_ID, hospitalId)
                }
                thisHospitalName = arguments?.getString(ARG_STRING_HOSPITAL_NAME) ?: ""
                thisClassName = arguments?.getString(ARG_STRING_CLASS_NAME) ?: ""
                thisHospitalId = arguments?.getLong(ARG_STRING_HOSPITAL_ID) ?: 0
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCustomReserveDialogBinding.inflate(inflater, container, false)

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


        //달력 선택 가능범위 설정 하기위한 변수
        val currentCalendar = Calendar.getInstance()
        currentYear = currentCalendar.get(Calendar.YEAR)
        currentMonth = currentCalendar.get(Calendar.MONTH)
        currentDay = currentCalendar.get(Calendar.DATE)



        val calendarOpenButton = binding.imageViewDateArrowOpen
        var reserveDateTextView = binding.textViewReserveDate //선택한 날짜 textView
        var reserveTimeTextView = binding.textViewReserveTime //선택한 시간 textView

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
        reservationButton = binding.reserveButton
        calendarView.setOnDateChangeListener { view, year, month, day -> //view = CalendarView
            val dayOfWeek = getDayOfWeek(year, month, day) //월
            val dateString = String.format("%d-%d-%d", year, month+1, day) //2024-5-20
            val dayOfWeekString = " ($dayOfWeek)" //(월)

            reserveDateTextView.text = String.format("%d.%d.%d ", year, month+1, day) + dayOfWeekString //2024.05.20
            reserveDate = dateString //2024-5-20

            //날짜에 따른 예약 가능한 시간 다르게
            apiService.getHospitalDetail(thisHospitalId).enqueue(object: Callback<HospitalSignupInfoResponse> {
                override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        reserveTime = null; reserveTimeTextView.text = null //새로운 날짜 선택으로 인해 시간 지우기
                        timeTableLayout.removeAllViews() //이전의 시간 테이블 모두 지우기
                        responseBodyHospitalDetail = response.body()!!
                        Log.w("CustomREserveDialogFragment", "responseBodyHospitalDetail: $responseBodyHospitalDetail")

                        val startEndTimeList =  getDBDayOfWeek(year, month, day)
                        val startTime =  startEndTimeList[1]
                        val endTime = startEndTimeList[2]

                        if(startTime == "휴무" || startTime == "정기휴무")  { //휴무나 정기휴무이면 예약 못하도록

                        } else {
                            val startTimeParts = startTime.split(":") //시간, 분 분리
                            val endTimeParts = endTime.split(":")

                            val startHour = startTimeParts[0].toInt() //오픈하는 hour
                            val endHour = endTimeParts[0].toInt() //문닫는 hour

                            val startMinute = startTimeParts[1].toInt() //오픈하는 minute
                            val endMinute = endTimeParts[1].toInt() //문닫는 minute

                            //시간 동적으로 넣기 (시간이 다 찼을 경우는 안그리기 위해서)
                            val rowSize = 4 //한 행에 들어갈 버튼 수
                            var tableRow: TableRow ?= null //동적으로 추가하기 위함, 아직 테이블에 아무 행도 없기 때문에 id를 찾을 수 없음
                            var buttonCountInRow = 0 //현재 행에 추가된 버튼 개수
                            var buttonCount = 0 //현재 버튼 개수

                            for (hour in startHour..endHour) {
                                for (minute in arrayOf(0, 30)) { //30분 단위로 추가
                                    if (buttonCountInRow == rowSize || buttonCount%4 == 0) {
                                        buttonCountInRow = 0 //한 행에 버튼 개수 초기화
                                    }

                                    if (buttonCountInRow == 0) { //새로운 행 시작
                                        tableRow = TableRow(requireContext())
                                        tableRow?.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                        timeTableLayout.addView(tableRow)
                                    }

                                    if(hour == startHour && startMinute == 30 && minute == 0) { //오픈시간 minute가 9:30분부터 시작이면, 9:00는 건너뛰기
                                        continue
                                    }
                                    if(hour == endHour && endMinute == 0 && minute == 30) { //문닫는 시간 minute가 14:00 이라면, 14:30는 건너뛰기
                                        continue
                                    }

                                    val button = Button(requireContext())
                                    val timeString = String.format("%02d:%02d", hour, minute)
                                    button.text = timeString
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

/*
                                    for(i in responseBodyHospitalDetail.data.reservations.indices) {
                                        Log.w("responseBodyHospitalDetail", "$i, ${responseBodyHospitalDetail.data.reservations[i].reservationDate}, ${responseBodyHospitalDetail.data.reservations[i].reservationTime}")
                                    }
*/


                                }
                            }
                        }
                        reservationButtonEnabled()
                    }

                    //통신 성공, 응답 실패
                    else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
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
                override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
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
        reservationButton.setOnClickListener {
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

            val reservation = ReservationRequest(dateFormatter, timeFormatter, thisHospitalId)
            apiService.postReservation(reservation).enqueue(object: Callback<ReservationResponse> {
                override fun onResponse(call: Call<ReservationResponse>, response: Response<ReservationResponse>) {
                    if(response.isSuccessful) {
                        responseBodyReservation = response.body()!!
                        Log.d("CustomReserveDialogFragment", "reserve response.body() : $responseBodyReservation")

                        val intent = Intent(requireActivity(), CheckReservationActivity::class.java)
                        intent.putExtra("reservationResponse", responseBodyReservation)
                        startActivity(intent)
                    }

                    else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
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

                override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                    Log.w("CONNECTION FAILURE: ", "Reservation Connect FAILURE : ${t.localizedMessage}")
                }
            })
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        /*
        val dialogWidth = binding.customReserveConstraintLayout.width
        dialog?.window?.setGravity(Gravity.BOTTOM) //하단에 위치
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) //dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        */

        val Dialog = dialog?.window
        Dialog?.setGravity(Gravity.BOTTOM) //하단에 위치
        Dialog?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //백그라운드 컬러 불투명

        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val deviceSize = Point() //현재 디바이스 크기
        display.getSize(deviceSize)

        val params = Dialog?.attributes
        params?.width = deviceSize.x
        params?.horizontalMargin = 0.0f
        dialog?.window?.attributes = params


        val minDate = Calendar.getInstance() //캘린더에서 선택할 수 있는 최소날짜
        val maxDate = Calendar.getInstance() //캘린더에서 선택할 수 있는 최대날짜

        //오늘부터 선택 가능하도록
        minDate.set(currentYear, currentMonth, currentDay)
        calendarView.minDate = minDate.timeInMillis

        //올해말까지 선택 가능하도록 (12/31)
        maxDate.set(currentYear, 11, 31)
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
    //년, 월, 일 해당하는 날짜의 요일 구해서 DB값 가져오기
    private fun getDBDayOfWeek(year:Int, month:Int, day: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> listOf("일", responseBodyHospitalDetail.data.hospitalDetail.sun_open, responseBodyHospitalDetail.data.hospitalDetail.sun_close)
            Calendar.MONDAY -> listOf("월", responseBodyHospitalDetail.data.hospitalDetail.mon_open, responseBodyHospitalDetail.data.hospitalDetail.mon_close)
            Calendar.TUESDAY -> listOf("화", responseBodyHospitalDetail.data.hospitalDetail.tue_open, responseBodyHospitalDetail.data.hospitalDetail.tue_close)
            Calendar.WEDNESDAY -> listOf("수", responseBodyHospitalDetail.data.hospitalDetail.wed_open, responseBodyHospitalDetail.data.hospitalDetail.wed_close)
            Calendar.THURSDAY -> listOf("목", responseBodyHospitalDetail.data.hospitalDetail.thu_open, responseBodyHospitalDetail.data.hospitalDetail.thu_close)
            Calendar.FRIDAY -> listOf("금", responseBodyHospitalDetail.data.hospitalDetail.fri_open, responseBodyHospitalDetail.data.hospitalDetail.fri_close)
            Calendar.SATURDAY -> listOf("토", responseBodyHospitalDetail.data.hospitalDetail.sat_open, responseBodyHospitalDetail.data.hospitalDetail.sat_close)
            else -> listOf("")
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

    //예약 시간 클릭시 버튼 색상 변경
    private fun selectButtonColor(clickedButton: Button) {
        //선택된 버튼이 없거나 클릭된 버튼이 아닐 경우
        if(selectedButton == null || selectedButton != clickedButton) {
            selectedButton?.setBackgroundResource(android.R.drawable.btn_default_small) //이전에 선택된 버튼의 색을 원래대로 되돌림
            clickedButton.setBackgroundColor(Color.GREEN) //현재 클릭된 버튼 색 변경
            selectedButton = clickedButton
        }
    }

    //
}
