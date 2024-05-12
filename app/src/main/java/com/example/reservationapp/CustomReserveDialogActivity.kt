package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
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
import androidx.fragment.app.DialogFragment
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.Model.HospitalItem

import com.example.reservationapp.databinding.FragmentCustomReserveDialogBinding
import java.util.*


class CustomReserveDialogActivity() : DialogFragment() {
    private lateinit var binding: FragmentCustomReserveDialogBinding
    private var thisHospitalName:String = ""
    private var thisClassName: String = ""

    private var calendar_open_flag: Boolean = false //달력 펼쳐져 있는지 확인하는 flag
    private var time_open_flag: Boolean = false //시간 펼쳐져 있는지 확인하는 flag
    private lateinit var calendarView: CalendarView //달력 calenderView
    private lateinit var timeTableLayout: TableLayout //시간 tableLayout

    //DB에서 데이터 가져오기
    private var reserveTimeList: List<String> = listOf("9:00", "10:00", "10:30", "11:00", "12:00", "14:00", "14:30", "15:30", "18:00") //예약 가능한 시간 넣는 리스트

    private var reserveDate: String ?= null //예약 날짜 -> intent할때 쓸 변수
    private var reserveTime: String ?= null //예약 시간 -> intent할때 쓸 변수

    //오늘날짜 변수
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0

    //DB에서 가져온 데이터 넣을 view
    private lateinit var hospitalNameTextView: TextView //병원이름
    private lateinit var reservationButton: Button //예약 버튼

    companion object {
        private const val ARG_STRING_HOSPITAL_NAME = "arg_string_hospital_name"
        private const val ARG_STRING_CLASS_NAME = "arg_string_class_name"

        fun newInstance(hospitalName: String, className: String): CustomReserveDialogActivity {
            return CustomReserveDialogActivity().apply {
                arguments = Bundle().apply {//arguments = bundleOf(ARG_LIST to newList)
                    putString(ARG_STRING_HOSPITAL_NAME, hospitalName)
                    putString(ARG_STRING_CLASS_NAME, className)
                }
                thisHospitalName = arguments?.getString(ARG_STRING_HOSPITAL_NAME) ?: ""
                thisClassName = arguments?.getString(ARG_STRING_CLASS_NAME) ?: ""
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCustomReserveDialogBinding.inflate(inflater, container, false)


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
        reservationButton = binding.reserveButton
        calendarView.setOnDateChangeListener { view, year, month, day -> //view = CalendarView
            val dayOfWeek = getDayOfWeek(year, month, day)
            val dateString = String.format("%d.%d.%d ", year, month+1, day)
            val dayOfWeekString = "($dayOfWeek)"
            reserveDateTextView.text = dateString + dayOfWeekString
            reserveDate = dateString
            reservationButtonEnabled()
        }




        //시간 동적으로 넣기 (시간이 다 찼을 경우는 안그리기 위해서)
        //DB에서 예약 가능한 시간 가져와서 list 재설정 해줘야함
        val rowSize = 4 //한 행에 들어갈 버튼 수
        var tableRow: TableRow?= null //동적으로 추가하기 위함, 아직 테이블에 아무 행도 없기 때문에 id를 찾을 수 없음
        var buttonCountInRow = 0 //현재 행에 추가된 버튼 개수
        var reserveTimeTextView = binding.textViewReserveTime //선택한 시간 textView
        timeTableLayout = binding.tableLayoutReserveTime

        for(i in reserveTimeList.indices) {
            if(buttonCountInRow == rowSize || (i%4 == 0) ) {
                buttonCountInRow = 0 //한 행에 버튼 개수 초기화
            }

            if(buttonCountInRow == 0) { //새로운 행 시작
                tableRow = TableRow(requireContext())
                tableRow?.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                timeTableLayout.addView(tableRow)
            }

            val button = Button(requireContext())
            button.text = reserveTimeList[i]
            button.setOnClickListener {//시간 선택 변경 감지했을 때
                val timeString = button.text
                reserveTimeTextView.text = timeString
                reserveTime = timeString.toString()
                reservationButtonEnabled()
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
           /*
            val intent = Intent(this, Final_Reservation::class.java)
            startActivity(intent)
            finish()
            */
            //val reserveDataItem = ArrayList<HistoryItem>()
            //reserveDataItem.add(HistoryItem("대기중", thisHospitalName, thisClassName, reserveDate.toString(), reserveTime.toString()))

            val reserveDataItem = HistoryItem("대기중", thisHospitalName, thisClassName, reserveDate.toString(), reserveTime.toString(), false)
            Log.w("CustomReserveDialog reservationButton", "reserveDataItem: $reserveDataItem")

            val intent = Intent(requireActivity(), CheckReservationActivity::class.java)
            intent.putExtra("reserveDataItem",reserveDataItem)
            startActivity(intent)
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
        minDate.set(currentYear, currentMonth,currentDay)
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


}
