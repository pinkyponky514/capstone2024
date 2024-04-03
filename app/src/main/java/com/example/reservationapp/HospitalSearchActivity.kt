package com.example.reservationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone2024.RecentSearchWordAdapter
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.databinding.ActivityHospitalSearchBinding


//병원 검색 페이지 액티비티
class HospitalSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalSearchBinding

    private lateinit var adapter: RecentSearchWordAdapter
    private lateinit var recentSearchWordList: ArrayList<RecentItem> //최근 검색어 리스트

    private lateinit var searchEditText: EditText
    private lateinit var submitButton: ImageView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //초기화
        searchEditText = binding.searchEditText
        submitButton = binding.submitButtonImageView
        backButton = binding.backButtonImageView

        //adapter
        adapter = RecentSearchWordAdapter()

        //최근 검색어 보여줄 recyclerView
        val recyclerView = binding.recentSearchRecyclerView
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        //최근 검색어 리스트 초기화
        recentSearchWordList = ArrayList()
        recentSearchWordList.add(RecentItem("이비인후과"))
        recentSearchWordList.add(RecentItem("코로나"))
        recentSearchWordList.add(RecentItem("강남"))
        recentSearchWordList.add(RecentItem("감기"))
        recentSearchWordList.add(RecentItem("인후통"))
        recentSearchWordList.add(RecentItem("성형외과"))
        adapter.updateList(recentSearchWordList)


        //검색 버튼 눌렀을 경우
        var searchWord: String
        submitButton.setOnClickListener {
            searchWord = searchEditText.text.toString()
            val intent = Intent(this, HospitalListActivity::class.java)
            intent.putExtra("searchWord", searchWord)
            startActivity(intent)
            finish()
        }

        /*
        //뒤로가기 버튼 눌렀을 경우
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
         */

    }
}