package com.example.reservationapp

import ReservationAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.ReservationItem
import com.example.reservationapp.R

class HospitalActivity : AppCompatActivity() {

    private lateinit var reservationAdapter: ReservationAdapter
    private lateinit var reservationList: ArrayList<ReservationItem>
    private var selectedDate: String? = null
    private var userId: String? = null
    private var userToken: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)

        userId = intent.getStringExtra("userId")

        val reservationRecyclerView = findViewById<RecyclerView>(R.id.reservation_list)
        val calendarView = findViewById<CalendarView>(R.id.calendar_view)

        // 예약 리스트 초기화 및 임의의 데이터 값 설정
        reservationList = ArrayList()
        reservationList.add(ReservationItem("10:00", "김철수", "1990-04-11", "2024-04-29"))
        reservationList.add(ReservationItem("14:30", "이영희", "1985-04-19", "2024-04-29"))
        reservationList.add(ReservationItem("12:30", "김아무", "1985-04-30", "2024-05-19"))
        reservationList.add(ReservationItem("11:30", "유재석", "1985-05-23", "2024-06-01"))
        reservationList.add(ReservationItem("15:30", "이광수", "1985-03-29", "2024-03-01"))

        // 예약된 내역을 표시할 RecyclerView 설정
        reservationAdapter = ReservationAdapter()
        reservationRecyclerView.layoutManager = LinearLayoutManager(this)
        reservationRecyclerView.adapter = reservationAdapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val formattedMonth = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
            val formattedDay = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            val newSelectedDate = "$year-$formattedMonth-$formattedDay" // 새로 선택된 날짜 문자열로 변환
            if (selectedDate != newSelectedDate) {
                selectedDate = newSelectedDate
                updateReservationList()
            }
        }

        val hospitalNameTextView: TextView = findViewById(R.id.hospital_name)
        hospitalNameTextView.setOnClickListener {
            val intent = Intent(this, Hospital_Mypage::class.java)

            intent.putExtra("userToken", userToken)

            startActivity(intent)
        }
    }

    private fun updateReservationList() {
        val filteredList = filterReservationList(selectedDate)
        reservationAdapter.updateList(filteredList)
    }

    private fun filterReservationList(selectedDate: String?): List<ReservationItem> {
        return reservationList.filter { it.reservationDate == selectedDate }
    }
}
