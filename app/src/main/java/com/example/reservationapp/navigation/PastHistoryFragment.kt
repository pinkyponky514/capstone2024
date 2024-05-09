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
    private lateinit var pastHistoryList: ArrayList<HistoryItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPastHistoryBinding.inflate(inflater)


        //진료내역 recyclerView
        adapter = PastHistoryAdapter()
        val recyclerView = binding.pastHistoryRecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        //과거 진료내역 데이터 넣기
        pastHistoryList = ArrayList()
        pastHistoryList.add(HistoryItem("진료완료","강정협내과의원", "내과", "2024.2.14", "15:00"))
        pastHistoryList.add(HistoryItem("진료완료","서울모이비인후과", "이비인후과", "2024.3.15", "14:30"))
        pastHistoryList.add(HistoryItem("진료완료", "강남성형외과", "성형외과", "2024.3.30", "11:00"))
        pastHistoryList.add(HistoryItem("진료완료","강정협내과의원", "내과", "2024.2.14","15:00"))
        pastHistoryList.add(HistoryItem("진료완료","서울모이비인후과", "이비인후과", "2024.3.15","14:30"))
        pastHistoryList.add(HistoryItem("진료완료", "강남성형외과", "성형외과", "2024.3.30","11:00"))
        adapter.updatelist(pastHistoryList)



        return binding.root
    }
}