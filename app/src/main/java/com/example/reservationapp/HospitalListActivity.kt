package com.example.reservationapp

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.HospitalListAdapter
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.Model.SearchHospital
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityHospitalListBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.util.Calendar
import java.util.Locale

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity().tokenCheck() //토큰 만료 검사

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
        finish() //현재 액티비티 종료
    }

    //검색 필터
    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchFilterAction(string: String) {
        searchTextField.setText(string)

        val searchString = searchTextField.text.toString().trim() //앞, 뒤 공백 제거

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)

        apiService.getSearchHospital(searchString).enqueue(object : Callback<List<SearchHospital>> {
            override fun onResponse(call: Call<List<SearchHospital>>, response: Response<List<SearchHospital>>) {

                //연결 응답 성공
                if(response.isSuccessful) {
                    responseBody = response.body()!!
                    Log.w("HospitalListActivity", "responseBody: $responseBody")

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
                        val currentYear = calendar.get(Calendar.YEAR) //현재 년도
                        val currentMonth = calendar.get(Calendar.MONTH) //현재 월
                        val currentDay = calendar.get(Calendar.DATE) //현재 일

                        val dayOfWeekTimeList = db_get_position_DayOfWeek(currentYear, currentMonth, currentDay, responseIndex) //현재 요일 운영시간 구하기
                        var startTime = dayOfWeekTimeList[0] //시작시간
                        var endTime = dayOfWeekTimeList[1] //끝나는시간


                        //금일 영업시간
                        var operatingTime: String
                        if(startTime.startsWith("정") || startTime.startsWith("휴")) { //"정"이나 "휴"으로 시작하는 글자면
                            operatingTime = "$startTime"
                        } else {
                            operatingTime = "$startTime ~ $endTime"
                        }

                        var hospitalId:Long = 0 //병원 레이블 번호
                        val hospitalName = responseBody[responseIndex].hospitalName //병원이름
                        var reviewAverage = 0.0F //리뷰 별점 평균
                        var className = responseBody[responseIndex]?.hospital?.hospitalDetail?.department ?: "진료과 없음" //진료과명
                        val address = responseBody[responseIndex].address //병원주소
                        var status: String //병원 영업 상태

                        if(responseBody[responseIndex].hospital != null) { //병원 상세정보 있으면
                            hospitalId = responseBody[responseIndex].hospital.hospitalId //병원 레이블 번호
                            status = operatingStatus(calendar, startTime, endTime) //현재시간이 운영시간 사이에 있는지 확인
                        } else { //병원 상세정보가 없으면
                            operatingTime = "운영시간,"
                            className = "진료과 정보없음"
                            status = "정보없음"
                        }


                        //리뷰가 있으면, 평점 구하기
                        if(responseBody[responseIndex]?.hospital?.review != null) {
                            for (i in responseBody[responseIndex].hospital.review.indices) {
                                reviewAverage += responseBody[responseIndex].hospital.review[i].starScore
                            }
                            reviewAverage/(responseBody[responseIndex].hospital.review.size)
                        }

                        //리스트에 병원 추가
                        hospitalList.add(HospitalItem(hospitalId, hospitalName, reviewAverage.toString(), operatingTime, address, listOf(className), status))

                    }
                    adapter.updatelist(hospitalList)
                    adapter.itemClick = (object: HospitalListAdapter.ItemClick { //클릭 이벤트 처리
                        override fun itemSetOnClick(itemView: View, position: Int) {
                            if(hospitalList[position].openingTimes.startsWith("운")) { //병원 정보가 없을때
                                //Toast.makeText(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.", Toast.LENGTH_SHORT).show()
                                CustomToast(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.").show()
                            }
                            else { //병원 정보가 있을때
                                val intent = Intent(this@HospitalListActivity, Hospital_DetailPage::class.java)
                                intent.putExtra("hospitalId", hospitalList[position].hospitalId)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
                                startActivity(intent)
                            }
                        }
                    })
                }

                //통신 성공, 응답 실패
                else {
                    //Log.w("Hospital_DetailPage FAILURE Response", "MyBookmark Connect SUCESS, Response FAILURE")

                    val errorBody = response.errorBody()?.string()
                    Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                    if (errorBody != null) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val timestamp = jsonObject.optString("timestamp")
                            val status = jsonObject.optInt("status")
                            val error = jsonObject.optString("error")
                            val message = jsonObject.optString("message")
                            val path = jsonObject.optString("path")

                            Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                        } catch (e: JSONException) {
                            Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                        }
                    }
                }
            }

            // 통신 실패
            override fun onFailure(call: Call<List<SearchHospital>>, t: Throwable) {
                Log.w("HospitalListActivity CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }

    //년, 월, 일 해당하는 날짜의 운영시간 구하기
    private fun db_get_position_DayOfWeek(year:Int, month:Int, day: Int, position: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.sun_open ?: "운영시간 정보없음", responseBody[position]?.hospital?.hospitalDetail?.sun_close ?: "운영시간 정보없음")
            Calendar.MONDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.mon_open ?: "운영시간 정보없음", responseBody[position]?.hospital?.hospitalDetail?.mon_close ?: "운영시간 정보없음")
            Calendar.TUESDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.tue_open ?: "운영시간 정보없음", responseBody[position]?.hospital?.hospitalDetail?.tue_close ?: "운영시간 정보없음")
            Calendar.WEDNESDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.wed_open ?: "운영시간 정보없음", responseBody[position]?.hospital?.hospitalDetail?.wed_close ?: "운영시간 정보없음")
            Calendar.THURSDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.thu_open ?: "운영시간 정보없음", responseBody[position]?.hospital?.hospitalDetail?.thu_close ?: "운영시간 정보없음")
            Calendar.FRIDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.fri_open ?: "운영시간 정보없음", responseBody[position]?.hospital?.hospitalDetail?.fri_close ?: "운영시간 정보없음")
            Calendar.SATURDAY -> listOf(responseBody[position]?.hospital?.hospitalDetail?.sat_open ?: "운영시간 정보없음", responseBody[position]?.hospital?.hospitalDetail?.sat_close ?: "운영시간 정보없음")
            else -> listOf("")
        }
    }

    //병원 영업 상태 구하기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun operatingStatus(calendar: Calendar, startTime: String, endTime: String): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = calendar.time

        try {
            val startTimeDate = dateFormat.parse(startTime)
            val endTimeDate = dateFormat.parse(endTime)

            if (currentTime in startTimeDate..endTimeDate) return "진료중"
            else return "진료마감"
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""

/*
        if(currentTimeFormatted >= startTime && currentTimeFormatted <= endTime) return "진료중"
        else return "진료마감"
*/
    }
//
}


