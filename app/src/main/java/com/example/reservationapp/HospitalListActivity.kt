package com.example.reservationapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone2024.RecentSearchWordAdapter
import com.example.reservationapp.Adapter.HospitalListAdapter
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.databinding.ActivityHospitalListBinding

//검색하면 나오는 병원 목록 페이지
class HospitalListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalListBinding

    private lateinit var adapter: HospitalListAdapter
    private lateinit var hospitalList : ArrayList<HospitalItem>

    private lateinit var intentString: String //최근검색어 추가하기 위한 문자열
    private lateinit var searchTextField: EditText

    private lateinit var recentSearchWordList: ArrayList<RecentItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Hospital Search Activity에서 검색어 넘겨받기
        searchTextField = binding.searchEditTextField
        intentString = ""
        intentString = intent.getStringExtra("searchWord").toString()

        recentSearchWordList = intent.getSerializableExtra("searchWordList") as? ArrayList<RecentItem> ?: ArrayList()
        Log.w("HospitalListActivity", "1. $recentSearchWordList")

        if(intentString != "null") {
            searchTextField.setText(intentString)
            //HospitalSearchActivity().recentSearchWordList.add(0, RecentItem(intentString)) //맨 처음으로 들어가게
            //HospitalSearchActivity().adapter.updateList(HospitalSearchActivity().recentSearchWordList)
        }


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
        hospitalList.add(HospitalItem("삼성드림이비인후과", "4.1", "오전9:30~오후6:30", "역삼동 826-24 화인타워 5,6층", "이비인후과"))
        hospitalList.add(HospitalItem("강남성모이비인후과의원", "4.4", "오전9:30~오후6:30", "서울특별시", "이비인후과"))
        hospitalList.add(HospitalItem("강남코모키의원", "4.9", "오전9:30~오후6:30", "서울특별시", "내과"))
        hospitalList.add(HospitalItem("강남서울이비인후과", "4.5", "오전9:30~오후6:30", "서울특별시", "이비인후과"))
        hospitalList.add(HospitalItem("청소년내과", "4.0", "오전9:30~오후6:30", "서울특별시", "내과"))

        hospitalList.add(HospitalItem("삼성드림이비인후과", "4.1", "오전9:30~오후6:30", "역삼동 826-24 화인타워 5,6층", "이비인후과"))
        hospitalList.add(HospitalItem("강남성모이비인후과의원", "4.4", "오전9:30~오후6:30", "서울특별시", "이비인후과"))
        hospitalList.add(HospitalItem("강남코모키의원", "4.9", "오전9:30~오후6:30", "서울특별시", "내과"))
        hospitalList.add(HospitalItem("강남서울이비인후과", "4.5", "오전9:30~오후6:30", "서울특별시", "이비인후과"))
        hospitalList.add(HospitalItem("청소년내과", "4.0", "오전9:30~오후6:30", "서울특별시", "내과"))

        hospitalList.add(HospitalItem("삼성드림이비인후과", "4.1", "오전9:30~오후6:30", "역삼동 826-24 화인타워 5,6층", "이비인후과"))
        hospitalList.add(HospitalItem("강남성모이비인후과의원", "4.4", "오전9:30~오후6:30", "서울특별시", "이비인후과"))
        hospitalList.add(HospitalItem("강남코모키의원", "4.9", "오전9:30~오후6:30", "서울특별시", "내과"))
        hospitalList.add(HospitalItem("강남서울이비인후과", "4.5", "오전9:30~오후6:30", "서울특별시", "이비인후과"))
        hospitalList.add(HospitalItem("청소년내과", "4.0", "오전9:30~오후6:30", "서울특별시", "내과"))

        adapter.updatelist(hospitalList)


        //검색어 변경 감지 - 새로운 검색어를 추가하기 위해서
        /*
        binding.searchTextField.addTextChangedListener(object: TextWatcher {
            //변경되기전 문자열
            //startPos:시작되는 위치, countChar:문자열 길이, afterPos:바뀌고 난 후 길이
            override fun beforeTextChanged(currentChar: CharSequence?, startPos: Int, countChar: Int, afterChar: Int) {
                TODO("Not yet implemented")
            }
            //
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }
            //
            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }
        })
        */

        //검색어에 따른 병원 필터

        //뒤로가기 버튼 눌렀을때 - 메인화면 나옴
        val backButton = binding.backButtonImageView
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity()::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("searchWordList", recentSearchWordList)
            startActivity(intent) // 인텐트 이동
            finish()
        }

        //검색버튼 또 눌렀을때
        val submitButton = binding.searchButtonImageView
        submitButton.setOnClickListener {
            intentString = searchTextField.toString()
            recentSearchWordList.add(RecentItem(intentString))
            HospitalSearchActivity().getAdapter().updateList(recentSearchWordList)
        }
    }


    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) // 지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
        intent.putExtra("searchWordList", recentSearchWordList)
        startActivity(intent) // 인텐트 이동
        finish() // 현재 액티비티 종료
    }
}