package com.example.reservationapp.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.PastHistoryAdapter
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.databinding.FragmentPastHistoryBinding

var pastHistoryList = ArrayList<HistoryItem>()

//과거 진료내역 프래그먼트
class PastHistoryFragment : Fragment() {
    private lateinit var binding: FragmentPastHistoryBinding

    private lateinit var adapter: PastHistoryAdapter
    //private lateinit var pastHistoryList: ArrayList<HistoryItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPastHistoryBinding.inflate(inflater)


        //진료내역 recyclerView
        adapter = PastHistoryAdapter()
        val recyclerView = binding.pastHistoryRecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        //과거 진료내역 데이터 넣기 (DB에서 불러오기)
        //pastHistoryList = ArrayList()
        if(pastHistoryList.isEmpty()) {
            pastHistoryList.add(HistoryItem("진료완료","삼성드림이비인후과", "내과", "2024.2.14", "15:00", true))
            pastHistoryList.add(HistoryItem("진료완료","강남성모이비인후과의원", "이비인후과", "2024.3.15", "14:30", false))
            pastHistoryList.add(HistoryItem("진료완료", "청소년내과", "성형외과", "2024.3.30", "11:00", true))
            pastHistoryList.add(HistoryItem("진료완료","우리의원", "외과", "2024.2.14","15:00", false))
            pastHistoryList.add(HistoryItem("진료완료","새봄연합의원외과", "가정의학과", "2024.3.15","14:30", false))
            pastHistoryList.add(HistoryItem("진료완료", "두리이비인후과의원", "이비인후과", "2024.3.30","11:00", false))
        }
        adapter.updatelist(pastHistoryList)



        return binding.root
    }
}