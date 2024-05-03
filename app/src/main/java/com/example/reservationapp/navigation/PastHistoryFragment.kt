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


//과거 진료내역 프래그먼트
class PastHistoryFragment : Fragment() {
    private lateinit var binding: FragmentPastHistoryBinding

    private lateinit var adapter: PastHistoryAdapter
    private lateinit var historyList: ArrayList<HistoryItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPastHistoryBinding.inflate(inflater)


        //진료내역 recyclerview
        adapter = PastHistoryAdapter()
        val recyclerView = binding.pastHistoryRecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        //과거 진료내역 데이터 넣기
        historyList = ArrayList()
        historyList.add(HistoryItem("진료완료","강정협내과의원", "내과", "2/14(수) 15:00"))
        historyList.add(HistoryItem("진료완료","서울모이비인후과", "이비인후과", "3/15(금) 14:30"))
        historyList.add(HistoryItem("진료완료", "강남성형외과", "성형외과", "3/30(토) 11:00"))
        historyList.add(HistoryItem("진료완료","강정협내과의원", "내과", "2/14(수) 15:00"))
        historyList.add(HistoryItem("진료완료","서울모이비인후과", "이비인후과", "3/15(금) 14:30"))
        historyList.add(HistoryItem("진료완료", "강남성형외과", "성형외과", "3/30(토) 11:00"))
        adapter.updatelist(historyList)



        return binding.root
    }
}