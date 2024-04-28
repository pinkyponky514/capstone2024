package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.databinding.ActivityHospitalDetailpageExampleBinding
import java.util.Calendar

class Hospital_DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalDetailpageExampleBinding
    private var calendar_open_flag: Boolean = false //달력 펼쳐져 있는지 확인하는 flag
    private var time_open_flag: Boolean = false //시간 펼쳐져 있는지 확인하는 flag
    private var reserveTimeList: List<String> = listOf("9:00", "10:00", "10:30", "11:00", "12:00", "14:00", "14:30", "15:30", "18:00") //예약 가능한 시간 넣는 리스트

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHospitalDetailpageExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //예약하기 -달력 펼치는 버튼 눌렀을 경우
        val calendarOpenButton = binding.imageViewDateArrowOpen
        val calendar = binding.calendarView
        calendar.visibility = View.GONE
        calendarOpenButton.setOnClickListener {
            Log.w("calendar Button Flag Before", "flag: ${calendar_open_flag}, visibility: ${calendar.visibility}")

            if(calendar_open_flag) { //true=열려있는 상태, 달력 접기
                calendar_open_flag = false
                calendar.visibility = View.GONE
            } else { //false=닫혀있는 상태, 달력 접기
                calendar_open_flag = true
                calendar.visibility = View.VISIBLE
            }

            Log.w("calendar Button Flag After", "${calendar_open_flag}, visibility: ${calendar.visibility}")
        }

        //예약하기 -날짜 선택 변경감지
        val reserveDateTextView = binding.textViewReserveDate
        calendar.setOnDateChangeListener { view, year, month, day -> //view = CalendarView
            //gone = 화면에 보이지 않으며, 자리차지도 안함
            val dayOfWeek = getDayOfWeek(year, month, day)
            reserveDateTextView.text = String.format("%d.%d.%d ", year, month+1, day) + "(" + dayOfWeek + ")"
        }

        //예약하기 -시간 동적으로 넣기 (시간이 다 찼을 경우는 안그리기 위해서)
        //DB에서 예약 가능한 시간 가져와서 list 재설정 해줘야함
        val reserveTimeTextView = binding.textViewReserveTime
        val tableLayout: TableLayout = binding.tableLayoutReserveTime

        val rowSize = 4 //한 행에 들어갈 버튼 수
        var tableRow: TableRow?= null //동적으로 추가하기 위함, 아직 테이블에 아무 행도 없기 때문에 id를 찾을 수 없음
        var buttonCountInRow = 0 //현재 행에 추가된 버튼 개수

        for(i in reserveTimeList.indices) {
            if(buttonCountInRow == rowSize || (i%4 == 0) ) {
                buttonCountInRow = 0 //한 행에 버튼 개수 초기화
            }

            if(buttonCountInRow == 0) { //새로운 행 시작
                tableRow = TableRow(this)
                tableRow?.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                tableLayout.addView(tableRow)
            }

            val button = Button(this)
            button.text = reserveTimeList[i]
            button.setOnClickListener {
                reserveTimeTextView.text = button.text
            }
            //button.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)

            tableRow?.addView(button)
            buttonCountInRow++
        }
//        while(buttonCountInRow != 0) {
//
//        }

        //예약하기 -시간 펼치는 버튼 눌렀을 경우
        val timeOpenButton = binding.imageViewTimeArrowOpen
        val time = binding.tableLayoutReserveTime
        time.visibility = View.GONE
        timeOpenButton.setOnClickListener {
            if(time_open_flag) { //true=열려있는 상태, 달력 접기
                time_open_flag = false
                time.visibility = View.GONE
            } else { //false=닫혀있는 상태, 달력 접기
                time_open_flag = true
                time.visibility = View.VISIBLE
            }
        }
        

        // 예약 버튼 클릭 이벤트 설정
        binding.ReservationButton.setOnClickListener {
            val intent = Intent(this, Final_Reservation::class.java)
            startActivity(intent)
            finish()
        }
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

    //
}
