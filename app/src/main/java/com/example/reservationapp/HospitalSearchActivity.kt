package com.example.reservationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone2024.RecentSearchWordAdapter
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.databinding.ActivityHospitalSearchBinding


var adapter: RecentSearchWordAdapter = RecentSearchWordAdapter()
//lateinit var adapter: RecentSearchWordAdapter

//병원 검색 페이지 액티비티
class HospitalSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalSearchBinding

    private var recentSearchWordList: ArrayList<RecentItem> = ArrayList() //최근 검색어 리스트

    private lateinit var searchEditText: EditText //검색창
    private lateinit var submitButton: ImageView //검색버튼
    private lateinit var backButton: ImageView //뒤로가기 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //초기화
        searchEditText = binding.searchEditText
        submitButton = binding.submitButtonImageView
        backButton = binding.backButtonImageView

        //adapter 초기화
        //adapter = RecentSearchWordAdapter()

        //최근 검색어 리스트 초기화
        //recentSearchWordList = ArrayList()
        //recentSearchWordList = adapter.getRecentWorldData() //DB에서 가져오면 됨
        recentSearchWordList = intent.getSerializableExtra("searchWordList") as? ArrayList<RecentItem> ?: ArrayList()
        Log.w("HospitalSearchActivity", "1. recentSearchWordList : $recentSearchWordList")


        //최근 검색어 보여줄 recyclerView
        val recyclerView = binding.recentSearchRecyclerView
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager


/*
        recentSearchWordList.add(RecentItem("이비인후과"))
        recentSearchWordList.add(RecentItem("코로나"))
        recentSearchWordList.add(RecentItem("강남"))
        recentSearchWordList.add(RecentItem("감기"))
        recentSearchWordList.add(RecentItem("인후통"))
        recentSearchWordList.add(RecentItem("성형외과"))
        adapter.updateList(recentSearchWordList)
*/



        //검색 버튼 눌렀을 경우 - 병원 검색목록 페이지 나옴
        submitButton.setOnClickListener {
            var searchWord = searchEditText.text.toString()
            recentSearchWordList.add(0, RecentItem(searchWord)) //맨 앞에 들어가게
            adapter.updateList(recentSearchWordList)
            Log.w("HospitalSearchActivity", "2. $recentSearchWordList")

            val intent = Intent(this, HospitalListActivity::class.java)
            intent.putExtra("searchWord", searchWord)
            intent.putExtra("searchWordList", recentSearchWordList)
            startActivity(intent)
            recentSearchWordList.clear()
            //finish()
        }

        //뒤로가기 버튼 눌렀을 경우 - 메인화면이 나옴
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // 지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
            finish()
        }
    }

    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) // 지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
        startActivity(intent) // 인텐트 이동
        finish() // 현재 액티비티 종료
    }


    fun getAdapter(): RecentSearchWordAdapter {
        return adapter
    }

    override fun onStart() {
        super.onStart()

        var dataList = adapter.getRecentWorldData()
        adapter.updateList(dataList) // 다른 Fragment로 이동했다가 다시 화면에 보여질 때 검색했던 기록이 초기화되도록 설정
    }
    /*
    override fun onStart() {
        super.onStart()

        //최근 검색어 리스트 초기화
        recentSearchWordList = ArrayList()
        recentSearchWordList = adapter.getRecentWorldData()
    }

    override fun onResume() {
        super.onResume()

        //검색 버튼 눌렀을 경우 - 병원 검색목록 페이지 나옴
        var searchWord: String
        submitButton.setOnClickListener {
            searchWord = searchEditText.text.toString()
            recentSearchWordList.add(0, RecentItem(searchWord)) //맨 처음으로 들어가게
            adapter.updateList(recentSearchWordList)

            val intent = Intent(this, HospitalListActivity::class.java)
            intent.putExtra("searchWord", searchWord)
            startActivity(intent)
            recentSearchWordList.clear()
            //finish()
        }

        //뒤로가기 버튼 눌렀을 경우 - 메인화면이 나옴
        backButton.setOnClickListener {
            MainActivity().setActivity(this, MainActivity())
            //overridePendingTransition(R.anim.activity_intent_no_move, R.anim.activity_intent_no_move)
            finish()
            //overridePendingTransition(R.anim.activity_intent_no_move, R.anim.activity_intent_no_move)


        }
    }
    */
}