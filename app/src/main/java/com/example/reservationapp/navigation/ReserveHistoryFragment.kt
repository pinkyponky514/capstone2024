package com.example.reservationapp.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.ReserveHistoryAdapter
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.R
import com.example.reservationapp.databinding.FragmentReserveHistoryBinding


//현재 진행중, 예약 진료내역 프래그먼트
class ReserveHistoryFragment : Fragment() {
    private lateinit var binding: FragmentReserveHistoryBinding

    private lateinit var adapter: ReserveHistoryAdapter
    private lateinit var reserveHistoryList: ArrayList<HistoryItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReserveHistoryBinding.inflate(inflater)

        //예약 진료내역 recyclerView
        adapter = ReserveHistoryAdapter()
        val recyclerView = binding.reserveHistoryRecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        //예약 진료내역 데이터 넣기 (DB 데이터 가져와서 넣기)
        reserveHistoryList = ArrayList()
        reserveHistoryList.add(HistoryItem("예약","강남대학병원", "내과", "2024.2.14","15:00"))
        reserveHistoryList.add(HistoryItem("예약","서울병원", "이비인후과", "2024.3.14", "14:00"))
        reserveHistoryList.add(HistoryItem("예약", "별빛한의원", "외과", "2024.3.25", "18:30"))
        reserveHistoryList.add(HistoryItem("대기중","강남성형외과", "성형외과", "2024.5.13", "13:30"))
        reserveHistoryList.add(HistoryItem("대기중","버팀병원", "내과", "2024.05.28", "16:20"))
        adapter.updatelist(reserveHistoryList)


        return binding.root
    }
}