package com.example.reservationapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.HospitalListAdapter
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.Model.RecentSearchWordResponseData
import com.example.reservationapp.Model.SearchHospital
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityHospitalListBinding
import com.google.android.material.navigation.NavigationView
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
    private lateinit var hospitalList: ArrayList<HospitalItem> //병원 리스트
    private lateinit var sortedStarScoreHospitalList: ArrayList<HospitalItem>

    //최근검색어
    private lateinit var intentString: String //다른 액티비티로부터 넘겨받은 검색어, 최근검색어 추가하기 위한 문자열
    private lateinit var searchTextField: EditText

    //필터
    private lateinit var navigationView: NavigationView //필터 네비게이션 뷰
    private lateinit var drawerLayout: DrawerLayout //네비게이션 드로우
    private lateinit var filterHideButton: ImageView //네비게이션 드로우의 x 버튼
    private lateinit var filterHolSpreadButton: ImageView //휴일진료 펼치는 버튼
    private var filterHolSpreadFlag = false //휴일진료 펼쳤는지 플래그
    private lateinit var filterHolSpreadConstraintLayout: ConstraintLayout

    //거리, 평점 순
    private lateinit var distanceButton: Button
    private lateinit var starScoreButton: Button
    private var distanceFlag = false
    private var starScoreFlag = false
    private var selectedButton: Button ?= null //선택된 버튼


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBody: List<SearchHospital>


    private lateinit var satCheckbox: CheckBox
    private lateinit var sunCheckbox: CheckBox
    private lateinit var holCheckbox: CheckBox

    private lateinit var filterApplyButton: Button

    private var isSaturdaySelected = false
    private var isSundaySelected = false
    private var isHolidaySelected = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity().tokenCheck() //토큰 만료 검사

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()


        //adapter
        adapter = HospitalListAdapter()

        //병원 목록 보여줄 recyclerView
        val recyclerView = binding.hospitalListRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true) //뷰마다 항목 사이즈를 같게함


        //필터
        drawerLayout = binding.drawerLayout
        navigationView = binding.filterNavigatinonView

        //필터 창 닫기
        filterHideButton = navigationView.findViewById(R.id.back_ImageView)
        filterHideButton.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        //거리, 평점 순 버튼 초기화
        distanceButton = binding.distanceButton
        starScoreButton = binding.starScoreListButton

        //휴일 진료 펼치기
        filterHolSpreadButton = navigationView.findViewById(R.id.hol_spread_ImageView)
        filterHolSpreadConstraintLayout = navigationView.findViewById(R.id.hol_diagnosis_select_ConstraintLayout)
        filterHolSpreadConstraintLayout.visibility = View.GONE
        filterHolSpreadButton.setOnClickListener {
            if(filterHolSpreadFlag) { //펼쳐져 있으면
                filterHolSpreadConstraintLayout.visibility = View.GONE
                filterHolSpreadFlag = false
            } else { //접혀 있으면
                filterHolSpreadConstraintLayout.visibility = View.VISIBLE
                filterHolSpreadFlag = true
            }
        }

        satCheckbox =navigationView.findViewById(R.id.saturday_checkbox) //토요일 체크박스
        sunCheckbox = navigationView.findViewById(R.id.sunday_checkbox) //일요일 체크박스
        holCheckbox = navigationView.findViewById(R.id.holiday_checkbox) //공휴일 체크박

        filterApplyButton = navigationView.findViewById(R.id.filter_apply_Button)
        filterApplyButton.setOnClickListener {
            applyFilter()
            // 네비게이션 드로어 닫기 (선택 사항)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        //뒤로가기 버튼 눌렀을때 - 메인화면 나옴
        val backButton = binding.backButtonImageView
        backButton.setOnClickListener {
            finish()
        }

        //병원 지도 플로팅 버튼 onClick
        val hospitalListMapFloatingButton = binding.hospitalListFloatingButton
        hospitalListMapFloatingButton.setOnClickListener {
            val intent = Intent(this, HospitalMapActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        //다른 Activity에서 검색어 넘겨받기
        searchTextField = binding.searchEdittext
        intentString = ""
        intentString = intent.getStringExtra("searchWord").toString()

        if(intentString != "null") {
            searchTextField.setText(intentString)

            //검색어 입력시 필터 동작
            searchFilterAction(intentString)
        }

        //검색버튼 또 눌렀을때
        val submitButton = binding.searchButtonImageview
        submitButton.setOnClickListener {
            intentString = searchTextField.text.toString()
            searchTextField.text.clear()
            recentSearchWord(intentString) //최근 검색단어 저장
            searchFilterAction(intentString)

            //키보드 내리기
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        //거리순 버튼 onClick
        distanceButton.setOnClickListener {
            selectButtonColor(distanceButton)
            if(distanceFlag) { //누른 상태 -> 안누른 상태로
                distanceFlag = false
                distanceButton.setBackgroundResource(R.drawable.style_light_gray_radius_line_5)
                Log.w("HospitalListActivity", "거리순 안누른 상태")
                //searchFilterAction(intentString)
            } else { //안누른 상태 -> 누른 상태로
                distanceFlag = true
                starScoreFlag = false
                distanceButton.setBackgroundResource(R.drawable.style_dark_green_line_light_green_radius_5)
                searchFilterAction(intentString)
                Log.w("HospitalListActivity", "거리순 누른 상태")
            }
        }

        //평점순 버튼 onClick
        starScoreButton.setOnClickListener {
            selectButtonColor(starScoreButton)
            if(starScoreFlag) { //누른 상태 -> 안누른 상태로
                starScoreFlag = false
                starScoreButton.setBackgroundResource(R.drawable.style_light_gray_radius_line_5)
                searchFilterAction(intentString)
                Log.w("HospitalListActivity", "평점순 안누른 상태")
            } else { //안누른 상태 - > 누른 상태로
                starScoreFlag = true
                distanceFlag = false
                starScoreButton.setBackgroundResource(R.drawable.style_dark_green_line_light_green_radius_5)
                starScoreFilterAction()
                Log.w("HospitalListActivity", "평점순 누른 상태")
            }
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

        var searchString = searchTextField.text.toString().trim() //앞, 뒤 공백 제거

        val classString = "내과, 외과, 이비인후과, 피부과, 안과, 성형외과, 신경외과, 소아청소년과"

        val class1 = "고혈압, 당뇨병, 고지혈증, 소화불량, 위염, 간염, 폐렴, 천식, 갑상선, 가슴통증, 호흡곤란, 기침, 체중변화, 피로, 소화장애" //내과
        val class2 = "충수염(맹장염), 탈장, 치질, 유방암, 갑상선암, 위암, 대장암, 간암, 췌장암, 담낭염, 종양, 혹, 출혈, 상처, 외상" //외과
        val class3 = "중이염, 편도염, 인후염, 비염, 부비동염(축농증), 난청, 이명, 어지럼증, 비염, 귀통증, 청력저하, 코막힘, 콧물, 인후통, 목소리변화, 어지럼증" //이비인후과
        val class4 = "여드름, 건선, 습진, 피부염, 곰팡이 감염, 바이러스 감염(사마귀, 대상포진), 탈모, 피부암, 두드러기,발진, 가려움, 붉은 반점, 여드름, 피부 궤양" //피부과
        val class5 = "결막염, 각막염, 백내장, 녹내장, 황반변성, 안구건조증, 시력 이상, 시력 저하, 눈 통증, 눈의 충혈, 눈 가려움, 눈의 이물감, 시야에 검은 점이나 빛 번쩍임" //안과
        val class6 = "화상 후 재건, 선천성 기형, 미용 성형, 흉터 제거, 지방흡입, 주름 제거" //성형외과
        val class7 = "뇌종양, 뇌출혈, 뇌경색, 척추 디스크, 척추 협착증, 삼차 신경통, 말초 신경 손상" //신경외과
        val class8 = "소아 감기, 구토, 설사, 기관지염, 식욕부진, 소아 비만, 알레르기, 예방접종" //소아청소년과

        val departments = mapOf(
            "내과" to class1,
            "외과" to class2,
            "이비인후과" to class3,
            "피부과" to class4,
            "안과" to class5,
            "성형외과" to class6,
            "신경외과" to class7,
            "소아청소년과" to class8
        )

        //
        var query = ""
        var department = ""


        if(classString.contains(searchString)) {
            department = searchString
        } else {
            query = searchString
        }
        Log.w("HospitalListActivity", "1. query = $query, department = $department")


        val matchedDepartment = departments.entries.firstOrNull { it.value.contains(searchString) }?.key
        if(matchedDepartment != null) {
            query = ""
            department = matchedDepartment
        }
        Log.w("HospitalListActivity", "2. query = $query, department = $department, matchedDepartment: $matchedDepartment")


        //병원 검색 정보 가져오기
        apiService.getSearchHospital(query=query, className=department).enqueue(object : Callback<List<SearchHospital>> {
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
                        val mainImage = responseBody[responseIndex].mainImage //병원 대표하는 이미지(디테일의 첫번째 이미지)


                        if(responseBody[responseIndex].hospital != null) { //병원 상세정보 있으면
                            hospitalId = responseBody[responseIndex].hospital.hospitalId //병원 레이블 번호
                            status = operatingStatus(calendar, startTime, endTime) //현재시간이 운영시간 사이에 있는지 확인
                        } else { //병원 상세정보가 없으면
                            operatingTime = "운영시간,"
                            className = "진료과 정보없음"
                            status = "정보없음"
                        }

                        //받아온 이미지가 있으면
                        var bitmap: Bitmap ?= null
                        if(mainImage != null) {
                            val decodedBytes: ByteArray = Base64.decode(mainImage, Base64.DEFAULT)
                            bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        }

                        //리뷰가 있으면, 평점 구하기
                        if(responseBody[responseIndex]?.hospital?.review != null) {
                            for (i in responseBody[responseIndex].hospital.review.indices) {
                                reviewAverage += responseBody[responseIndex].hospital.review[i].starScore
                            }
                            reviewAverage/(responseBody[responseIndex].hospital.review.size)
                        }

                        var sat_open = ""
                        var sun_open = ""
                        var hol_open = ""

                        if(responseBody[responseIndex].hospital != null){
                            sat_open =  responseBody[responseIndex].hospital.hospitalDetail.sat_open
                            sun_open = responseBody[responseIndex].hospital.hospitalDetail.sun_open
                            hol_open =  responseBody[responseIndex].hospital.hospitalDetail.hol_open

                        }

                        //리스트에 병원 추가
                        hospitalList.add(HospitalItem(hospitalId, hospitalName, reviewAverage.toString(), operatingTime, address, listOf(className), status, bitmap, sat_open, sun_open, hol_open))


                    }
                    adapter.updatelist(hospitalList)
                    adapter.itemClick = filterItemOnClick(hospitalList)
/*
                        (object: HospitalListAdapter.ItemClick { //클릭 이벤트 처리
                        override fun itemSetOnClick(itemView: View, position: Int) {
                            if(hospitalList[position].openingTimes.startsWith("운")) { //병원 정보가 없을때
                                //Toast.makeText(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.", Toast.LENGTH_SHORT).show()
                                CustomToast(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.").show()
                            }
                            else { //병원 정보가 있을때
                                val intent = Intent(this@HospitalListActivity, Hospital_DetailPage::class.java)
                                intent.putExtra("hospitalId", hospitalList[position].hospitalId)
                                Log.w("HospitalList", "hospitalId ${hospitalList[position].hospitalId}")
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
                                startActivity(intent)
                            }
                        }
                    })
*/
                }

                //통신 성공, 응답 실패
                else handleErrorResponse(response)
            }

            // 통신 실패
            override fun onFailure(call: Call<List<SearchHospital>>, t: Throwable) {
                Log.w("HospitalListActivity CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }

/*
    //아이템 필터
    private fun filterItemOnClick(list: ArrayList<HospitalItem>): HospitalListAdapter.ItemClick? {
        adapter.itemClick = (object: HospitalListAdapter.ItemClick { //클릭 이벤트 처리
            override fun itemSetOnClick(itemView: View, position: Int) {
                if(list[position].openingTimes.startsWith("운")) { //병원 정보가 없을때
                    //Toast.makeText(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.", Toast.LENGTH_SHORT).show()
                    CustomToast(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.").show()
                }
                else { //병원 정보가 있을때
                    val intent = Intent(this@HospitalListActivity, Hospital_DetailPage::class.java)
                    intent.putExtra("hospitalId", list[position].hospitalId)
                    Log.w("HospitalList", "hospitalId ${list[position].hospitalId}")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
                    startActivity(intent)
                }
            }
        })
        return
    }
*/
    // 아이템 필터 함수
    private fun filterItemOnClick(list: ArrayList<HospitalItem>): HospitalListAdapter.ItemClick? {
        return object : HospitalListAdapter.ItemClick { // 클릭 이벤트 처리
            override fun itemSetOnClick(itemView: View, position: Int) {
                if (list[position].openingTimes.startsWith("운")) { // 병원 정보가 없을 때
                    // Toast.makeText(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.", Toast.LENGTH_SHORT).show()
                    CustomToast(this@HospitalListActivity, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.").show()
                } else { // 병원 정보가 있을 때
                    val intent = Intent(this@HospitalListActivity, Hospital_DetailPage::class.java)
                    intent.putExtra("hospitalId", list[position].hospitalId)
                    Log.w("HospitalList", "hospitalId ${list[position].hospitalId}")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
                    startActivity(intent)
                }
            }
        }
    }



    //평점 순 필터
    private fun starScoreFilterAction() {
        Log.w("HospitalListActivity", "어댑터에 넣을 hospitalList = $hospitalList")

        val sortedStarScoreHospitalList = hospitalList.sortedByDescending { it.starScore }.map { it } as ArrayList<HospitalItem> //ArrayList<HospitalItem>
        adapter.updatelist(sortedStarScoreHospitalList)
    }

    //선택한 버튼 바꾸기
    private fun selectButtonColor(clickedButton: Button) {
        //선택된 버튼이 없거나 클릭된 버튼이 아닐 경우
        if(selectedButton == null || selectedButton != clickedButton) {
            selectedButton?.setBackgroundResource(R.drawable.style_light_gray_radius_line_5) //이전에 선택된 버튼의 색을 원래대로 되돌림
            clickedButton.setBackgroundResource(R.drawable.style_dark_green_line_light_green_radius_5) //현재 클릭된 버튼 색 변경
            selectedButton = clickedButton
        } else if(selectedButton != null || selectedButton == clickedButton) {
            selectedButton?.setBackgroundResource(R.drawable.style_light_gray_radius_line_5)
            clickedButton.setBackgroundResource(R.drawable.style_light_gray_radius_line_5)
            selectedButton = clickedButton
        }
    }

    //네비게이션 드로우 열기
    fun openDrawer(view: View) {
        drawerLayout.openDrawer(GravityCompat.START)
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
    }

    //검색어 저장
    private fun recentSearchWord(searchWord: String) {
        var searchString = searchWord.trim()
        //로그인 되어있으면
        if(App.prefs.token != null) {
            apiService.postRecentSearchWord(searchString).enqueue(object: Callback<RecentSearchWordResponseData> {
                override fun onResponse(call: Call<RecentSearchWordResponseData>, response: Response<RecentSearchWordResponseData>) {
                    if(response.isSuccessful) {
                        val responseBodyPost = response.body()!!
                        Log.w("HospitalSearchActivity", "검색어 저장! responseBodyPost : $responseBodyPost")
                    }

                    else handleErrorResponse(response)
                }

                override fun onFailure(call: Call<RecentSearchWordResponseData>, t: Throwable) {
                    Log.w("HospitalSearchActivity", "post Recent Search Word API call failed: ${t.localizedMessage}")
                }
            })
        }
    }
    private fun updateSelectedDays() {
        isSaturdaySelected = satCheckbox.isChecked
        isSundaySelected = sunCheckbox.isChecked
        isHolidaySelected = holCheckbox.isChecked
    }


    private fun applyFilter() {
        // 선택한 요일 업데이트
        updateSelectedDays()

        // 선택한 요일에 영업하는 병원만 추출하여 새로운 리스트 생성
        val filteredList = hospitalList.filter { item ->
            // 선택한 요일에 영업하는 병원인지 확인
            val isOperatingOnSelectedDay = when {
                isSaturdaySelected && item.sat_open == "휴무" ->false
                isSundaySelected && item.sun_open == "휴무" ->false
                isHolidaySelected && item.hol_open == "휴무" -> false
                isSaturdaySelected && item.sat_open != "휴무" -> true
                isSundaySelected && item.sun_open != "휴무" -> true
                isHolidaySelected && item.hol_open != "휴무" -> true
                else -> true
            }
            isOperatingOnSelectedDay
        }

        val filteredArrayList = ArrayList(filteredList)

        // 새로운 리스트로 어댑터 업데이트
        adapter.updatelist(filteredArrayList)
        adapter.itemClick = filterItemOnClick(filteredArrayList)
    }

//
}


