package com.example.reservationapp.navigation

import ReservationAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.HospitalSecurityActivity
import com.example.reservationapp.Model.ReservationItem
import com.example.reservationapp.WebViewActivity
import com.example.reservationapp.databinding.FragmentHospitalBinding

class HospitalFragment : Fragment() {
    private lateinit var binding: FragmentHospitalBinding
    private lateinit var reservationAdapter: ReservationAdapter
    private lateinit var reservationList: ArrayList<ReservationItem>
    private var selectedDate: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHospitalBinding.inflate(inflater, container, false)

        val reservationRecyclerView = binding.reservationList
        val calendarView = binding.calendarView

        // 예약 리스트 초기화 및 임의의 데이터 값 설정
        reservationList = ArrayList()
        reservationList.add(ReservationItem("10:00", "김철수", "1990-04-11", "2024-04-29", "진료완료"))
        reservationList.add(ReservationItem("14:30", "이영희", "1985-04-19", "2024-04-29", "진료완료"))
        reservationList.add(ReservationItem("12:30", "김아무", "1985-04-30", "2024-05-19", "예약"))
        reservationList.add(ReservationItem("15:30", "이광수", "1985-03-29", "2024-05-19", "대기"))
        reservationList.add(ReservationItem("11:30", "유재석", "1985-05-23", "2024-05-19", "진료완료"))

        // 예약된 내역을 표시할 RecyclerView 설정
        reservationAdapter = ReservationAdapter()
        reservationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reservationRecyclerView.adapter = reservationAdapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val formattedMonth = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
            val formattedDay = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            val newSelectedDate = "$year-$formattedMonth-$formattedDay"
            if (selectedDate != newSelectedDate) {
                selectedDate = newSelectedDate
                updateReservationList()
            }
        }

        // 병원 보안 버튼 클릭 시 HospitalSecurityActivity로 이동
        binding.hospitalSecurity.setOnClickListener {
            val intent = Intent(requireContext(), HospitalSecurityActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun updateReservationList() {
        val filteredList = filterReservationList(selectedDate)
        reservationAdapter.updateList(filteredList)
    }

    private fun filterReservationList(selectedDate: String?): List<ReservationItem> {
        return reservationList.filter { it.reservationDate == selectedDate }
    }
}
