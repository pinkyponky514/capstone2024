package com.example.reservationapp

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity

class Final_Reservation : AppCompatActivity() {
    private var rdoCal: RadioButton? = null
    private var rdoTime: RadioButton? = null
    private var calView: CalendarView? = null
    private var tPicker: TimePicker? = null
    private var tvYear: TextView? = null
    private var tvMonth: TextView? = null
    private var tvDay: TextView? = null
    private var tvHour: TextView? = null
    private var tvMinute: TextView? = null
    private var selectYear = 0
    private var selectMonth = 0
    private var selectDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_reservation)
        setTitle("시간 예약")

        // 라디오버튼 2개
        rdoCal = findViewById<View>(R.id.rdoCal) as RadioButton
        rdoTime = findViewById<View>(R.id.rdoTime) as RadioButton

        // FrameLayout의 2개 위젯
        tPicker = findViewById<View>(R.id.timePicker1) as TimePicker
        calView = findViewById<View>(R.id.calendarView1) as CalendarView

        // 텍스트뷰 중에서 연,월,일,시,분 숫자
        tvYear = findViewById<View>(R.id.tvYear) as TextView
        tvMonth = findViewById<View>(R.id.tvMonth) as TextView
        tvDay = findViewById<View>(R.id.tvDay) as TextView
        tvHour = findViewById<View>(R.id.tvHour) as TextView
        tvMinute = findViewById<View>(R.id.tvMinute) as TextView

        // 처음에는 2개를 안보이게 설정
        tPicker!!.visibility = View.INVISIBLE
        calView!!.visibility = View.INVISIBLE

        rdoCal!!.setOnClickListener {
            calView!!.visibility = View.VISIBLE
            tPicker!!.visibility = View.INVISIBLE
        }

        rdoTime!!.setOnClickListener {
            calView!!.visibility = View.INVISIBLE
            tPicker!!.visibility = View.VISIBLE
        }

        // 버튼을 클릭하면 날짜,시간을 가져온다.
        findViewById<Button>(R.id.btnEnd).setOnClickListener {
            /* 맨 아래 텍스트뷰에 타임피커로부터 가져온 시간과 분 표시.
            tPicker.getHour 자동완성 뜨는 것 보면 이것이 정수형(int)임을 알 수 있으므로
            정수형을 문자열로 바꿔주는 Integer.toString을 사용하기 */

            tvHour!!.text = tPicker!!.hour.toString()
            tvMinute!!.text = tPicker!!.minute.toString()

            // 맨 아래 텍스트뷰에 캘린더뷰에서 가져온 년/월/일 표시.
            tvYear!!.text = selectYear.toString()
            tvMonth!!.text = (selectMonth + 1).toString() // 월은 0부터 시작하므로 +1 해줌
            tvDay!!.text = selectDay.toString()
        }

        calView!!.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectYear = year
            selectMonth = month
            selectDay = dayOfMonth
        }
    }
}
