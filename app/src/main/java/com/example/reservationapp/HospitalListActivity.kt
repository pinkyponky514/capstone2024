package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.HospitalListAdapter
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.Hospital
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.Model.SearchHospital
import com.example.reservationapp.Model.filterList
import com.example.reservationapp.Model.getDayOfWeek
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityHospitalListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

//검색하면 나오는 병원 목록 페이지
class HospitalListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalListBinding

    //병원 검색 리스트
    private lateinit var adapter: HospitalListAdapter
    private lateinit var hospitalList : ArrayList<HospitalItem> //병원 리스트

    //최근검색어
    private lateinit var intentString: String //다른 액티비티로부터 넘겨받은 검색어, 최근검색어 추가하기 위한 문자열
    private lateinit var searchTextField: EditText
    private lateinit var recentSearchWordList: ArrayList<RecentItem> //최근검색어 리스트

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBody: List<SearchHospital>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.w("HospitalListActivity", "filterList: ${filterList}")

        //adapter
        adapter = HospitalListAdapter()

        //병원 목록 보여줄 recyclerView
        val recyclerView = binding.hospitalListRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true) //뷰마다 항목 사이즈를 같게함


        //다른 Activity에서 검색어 넘겨받기
        searchTextField = binding.searchEditTextField
        intentString = ""
        intentString = intent.getStringExtra("searchWord").toString()

        //recentSearchWordList = intent.getSerializableExtra("searchWordList") as? ArrayList<RecentItem> ?: ArrayList()
        //Log.w("HospitalListActivity", "1. $recentSearchWordList")

        if(intentString != "null") {
            searchTextField.setText(intentString)
            //HospitalSearchActivity().recentSearchWordList.add(0, RecentItem(intentString)) //맨 처음으로 들어가게
            //HospitalSearchActivity().adapter.updateList(HospitalSearchActivity().recentSearchWordList)

            //검색어 입력시 필터 동작
            searchFilterAction(intentString)
        }


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

            intentString = searchTextField.text.toString()
            searchTextField.text.clear()
            searchFilterAction(intentString)

            //키보드 내리기
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
        //intent.putExtra("searchWordList", recentSearchWordList)
        startActivity(intent) //인텐트 이동
        finish() //현재 액티비티 종료
    }

    //검색 필터
    private fun searchFilterAction(string: String) {
        searchTextField.setText(string)

        //필터기능 구현
        val searchString = searchTextField.text.toString().trim() //앞, 뒤 공백 제거

/*
        val filteredList = filterList.filter { filterItem ->
            filterItem.hospitalName.contains(searchString, true) ||
                    filterItem.hospitalAddress.contains(searchString, true) ||
                    filterItem.className.any { it.contains(searchString, true) } ||
                    filterItem.weekTime.any { it.value.contains(searchString, true) } ||
                    filterItem.starScore.contains(searchString, true)
        }

        hospitalList = ArrayList()
        for (i in filteredList.indices) {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DATE)

            val dayOfWeek = getDayOfWeek(currentYear, currentMonth, currentDay) //현재 요일 구하기
            val dayTime = filteredList[i].weekTime[dayOfWeek] //현재 요일에 맞는 영업시간 가져오기

            hospitalList.add(
                HospitalItem(
                    filteredList[i].hospitalName,
                    filteredList[i].starScore,
                    dayTime ?: "영업시간 정보 없음",
                    filteredList[i].hospitalAddress,
                    filteredList[i].className
                )
            )
        }
        adapter.updatelist(hospitalList)
*/

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)
        apiService.searchHospital(searchString).enqueue(object : Callback<List<SearchHospital>> {
            override fun onResponse(
                call: Call<List<SearchHospital>>,
                response: Response<List<SearchHospital>>
            ) {
                if (response.isSuccessful()) {
                    responseBody = response.body()!!
                    Log.w("HospitalListActivity", "responseBody: ${responseBody}")

                    hospitalList = ArrayList()
                    for(responseIndex in responseBody.indices) {
/*
                        //hospital Detail 작성한 병원만 나옴
                        if(responseBody[responseIndex].hospital != null) {
                            val calendar = Calendar.getInstance()
                            val currentYear = calendar.get(Calendar.YEAR)
                            val currentMonth = calendar.get(Calendar.MONTH)
                            val currentDay = calendar.get(Calendar.DATE)

                            val dayOfWeek = db_getDayOfWeek(currentYear, currentMonth, currentDay, responseIndex) //현재 요일 운영시간 구하기
                            Log.w("HospitalActivity !!", "dayTime open: $dayOfWeek")

                            val hospitalName = responseBody[responseIndex].hospitalName
                            val className = responseBody[responseIndex].hospital.hospitalDetail.department
                            val address = responseBody[responseIndex].address
                            var reviewAverage: Float = 0.0F //리뷰 별점 평균
                            if(responseBody[responseIndex].hospital.review != null) { //리뷰가 있으면
                                for (i in responseBody[responseIndex].hospital.review.indices) {
                                    reviewAverage += responseBody[responseIndex].hospital.review[i].starScore
                                }
                                reviewAverage/responseBody[responseIndex].hospital.review.size
                            }
                            hospitalList.add(HospitalItem(hospitalName, reviewAverage.toString(), dayOfWeek, address, listOf(className)))
                        }
*/

                        //hospital Detail 작성안한 병원도 나옴
                        val calendar = Calendar.getInstance()
                        val currentYear = calendar.get(Calendar.YEAR)
                        val currentMonth = calendar.get(Calendar.MONTH)
                        val currentDay = calendar.get(Calendar.DATE)

                        val dayOfWeek = db_getDayOfWeek(currentYear, currentMonth, currentDay, responseIndex) //현재 요일 운영시간 구하기
                        val openingTime = "${dayOfWeek[0]}~${dayOfWeek[1]}"
                        Log.w("HospitalActivity !!", "dayTime open: $dayOfWeek")


                        val hospitalName = responseBody[responseIndex].hospitalName
                        val className = responseBody[responseIndex]?.hospital?.hospitalDetail?.department ?: "진료과 없음"
                        val address = responseBody[responseIndex].address
                        var reviewAverage: Float = 0.0F //리뷰 별점 평균
                        if(responseBody[responseIndex]?.hospital?.review != null) { //리뷰가 있으면
                            for (i in responseBody[responseIndex].hospital.review.indices) {
                                reviewAverage += responseBody[responseIndex].hospital.review[i].starScore
                            }
                            reviewAverage/responseBody[responseIndex].hospital.review.size
                        }
                        hospitalList.add(HospitalItem(hospitalName, reviewAverage.toString(), openingTime, address, listOf(className)))

                    }
                    adapter.updatelist(hospitalList)

                } else Log.w("FAILURE Response", "Connect SUCESS, Response FAILURE") //통신 성공, 응답 실패
            }

            override fun onFailure(call: Call<List<SearchHospital>>, t: Throwable) {
                Log.w("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
            }
        })


        //
    }

    //년, 월, 일 해당하는 날짜의 요일 구하기
    //fun db_getDayOfWeek(year:Int, month:Int, day: Int, position: Int): String {
    fun db_getDayOfWeek(year:Int, month:Int, day: Int, position: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.sun_open ?: "운영시간 없음", responseBody[position]?.hospital?.hospitalDetail?.sun_close ?: "운영시간 없음")
            Calendar.MONDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.mon_open ?: "운영시간 없음", responseBody[position]?.hospital?.hospitalDetail?.mon_close ?: "운영시간 없음")
            Calendar.TUESDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.tue_open ?: "운영시간 없음", responseBody[position]?.hospital?.hospitalDetail?.tue_close ?: "운영시간 없음")
            Calendar.WEDNESDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.wed_open ?: "운영시간 없음", responseBody[position]?.hospital?.hospitalDetail?.wed_close ?: "운영시간 없음")
            Calendar.THURSDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.thu_open ?: "운영시간 없음", responseBody[position]?.hospital?.hospitalDetail?.thu_close ?: "운영시간 없음")
            Calendar.FRIDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.fri_open ?: "운영시간 없음", responseBody[position]?.hospital?.hospitalDetail?.fri_close ?: "운영시간 없음")
            Calendar.SATURDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.sat_open ?: "운영시간 없음", responseBody[position]?.hospital?.hospitalDetail?.sat_close ?: "운영시간 없음")
            else -> listOf("")
        }
    }

//
}


