package com.example.reservationapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Adapter.HospitalListAdapter
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.databinding.ActivityHospitalListBinding

//검색하면 나오는 병원 목록 페이지
class HospitalListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalListBinding

    private lateinit var adapter: HospitalListAdapter
    private lateinit var hospitalList : ArrayList<HospitalItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalListBinding.inflate(layoutInflater)
        setContentView(binding.root)



        var intentString = ""
        intentString = intent.getStringExtra("searchWord").toString()
        if(intentString != "null")
            binding.searchTextField.setText(intentString)



        /*
        //adapter
        adapter = HospitalListAdapter()

        //병원 목록 보여줄 recyclerView
        val recyclerView = binding.hospitalListRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true) //뷰마다 항목 사이즈를 같게함


        //병원 목록 리스트 초기화
        hospitalList = ArrayList()
        hospitalList.add(HospitalItem("삼성드림이비인후과", "4.1", "오전9:30~오후6:30", "역삼동 826-24 화인타워 5,6층"))
        hospitalList.add(HospitalItem("강남성모이비인후과의원", "4.4", "오전9:30~오후6:30", "서울특별시"))
        hospitalList.add(HospitalItem("강남코모키의원", "4.9", "오전9:30~오후6:30", "서울특별시"))
        hospitalList.add(HospitalItem("강남서울이비인후과", "4.5", "오전9:30~오후6:30", "서울특별시"))
        hospitalList.add(HospitalItem("청소년내과", "4.0", "오전9:30~오후6:30", "서울특별시"))
        adapter.updatelist(hospitalList)

        //검색어에 따른 병원 필터


         */

    }
}