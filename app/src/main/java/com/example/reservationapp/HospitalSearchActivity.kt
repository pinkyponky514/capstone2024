package com.example.reservationapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone2024.RecentSearchWordAdapter
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.Model.RecentSearchWordResponse
import com.example.reservationapp.Model.RecentSearchWordResponseData
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityHospitalSearchBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



//병원 검색 페이지 액티비티
class HospitalSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalSearchBinding
    private lateinit var adapter: RecentSearchWordAdapter

    private var recentSearchWordList: ArrayList<RecentItem> = ArrayList() //최근 검색어 리스트

    private lateinit var searchEditText: EditText //검색창
    private lateinit var submitButton: ImageView //검색버튼
    private lateinit var backButton: ImageView //뒤로가기 버튼

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBodyGet: RecentSearchWordResponse
    private lateinit var responseBodyPost: RecentSearchWordResponseData


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //초기화
        searchEditText = binding.searchEditText
        submitButton = binding.submitButtonImageView
        backButton = binding.backButtonImageView

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()


        //adapter 초기화
        adapter = RecentSearchWordAdapter()

        //최근 검색어 보여줄 recyclerView
        val recyclerView = binding.recentSearchRecyclerView
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager


        if(App.prefs.token != null) { //로그인 되어 있으면 최근검색어 불러오기
            apiService.getRecentSearchWordList().enqueue(object: Callback<RecentSearchWordResponse> {
                override fun onResponse(call: Call<RecentSearchWordResponse>, response: Response<RecentSearchWordResponse>) {

                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        responseBodyGet = response.body()!!
                        Log.w("HostpialSearchActivity", "최근 검색어 응답 responseBody = $responseBodyGet")


                        recentSearchWordList = ArrayList()
                        if(responseBodyGet.data.isNotEmpty()) {
                            for(recentSearchWord in responseBodyGet.data) {
                                recentSearchWordList.add(RecentItem(recentSearchWord.keyword)) //, recentSearchWord.searchDate, recentSearchWord.searchTime))
                            }
                            adapter.updateList(recentSearchWordList)
                        }
                    }

                    //통신 성공, 응답 실패
                    else handleErrorResponse(response)

                }

                //통신 실패
                override fun onFailure(call: Call<RecentSearchWordResponse>, t: Throwable) {
                    Log.w("HospitalSearchActivity", "get Recent Search Word API call failed: ${t.localizedMessage}")
                }
            })
        }
        else { //로그인 안했으면
            adapter.updateList(recentSearchWordList)
        }



        //검색 버튼 눌렀을 경우 - 병원 검색목록 페이지 나옴
        submitButton.setOnClickListener {
            var searchWord = searchEditText.text.toString()
            adapter.updateList(recentSearchWordList)

            //로그인 되어 있으면 저장되게
            if(App.prefs.token != null) {
                apiService.postRecentSearchWord(searchWord).enqueue(object: Callback<RecentSearchWordResponseData> {
                    override fun onResponse(call: Call<RecentSearchWordResponseData>, response: Response<RecentSearchWordResponseData>) {
                        if(response.isSuccessful) {
                            responseBodyPost = response.body()!!
                            Log.w("HospitalSearchActivity", "검색어 저장! responseBodyPost : $responseBodyPost")

                            val intent = Intent(this@HospitalSearchActivity, HospitalListActivity::class.java)
                            intent.putExtra("searchWord", searchWord)
                            startActivity(intent)
                            finish()
                        }

                        else handleErrorResponse(response)
                    }

                    override fun onFailure(call: Call<RecentSearchWordResponseData>, t: Throwable) {
                        Log.w("HospitalSearchActivity", "post Recent Search Word API call failed: ${t.localizedMessage}")
                    }
                })
            }

            //로그인 안되어 있으면 전달만 하기
            else {
                recentSearchWordList.add(0, RecentItem(searchWord))
                adapter.updateList(recentSearchWordList)

                val intent = Intent(this, HospitalListActivity::class.java)
                intent.putExtra("searchWord", searchWord)
                startActivity(intent)
                finish()
            }


        }

        //뒤로가기 버튼 눌렀을 경우 - 메인화면이 나옴
        backButton.setOnClickListener {
            finish()
        }
    }



    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // 현재 액티비티 종료
    }

//
}