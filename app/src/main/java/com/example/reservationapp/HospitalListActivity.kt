package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.HospitalListAdapter
import com.example.reservationapp.Model.FilterItem
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.databinding.ActivityHospitalListBinding
import java.util.Calendar

//검색하면 나오는 병원 목록 페이지
class HospitalListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalListBinding

    private lateinit var adapter: HospitalListAdapter
    private lateinit var hospitalList : ArrayList<HospitalItem> //병원 리스트

    private lateinit var intentString: String //다른 액티비티로부터 넘겨받은 검색어, 최근검색어 추가하기 위한 문자열
    private lateinit var searchTextField: EditText

    private lateinit var recentSearchWordList: ArrayList<RecentItem> //최근검색어 리스트
    private lateinit var filterList: ArrayList<FilterItem> //필터에 사용할 병원정보

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //병원정보 초기화 (DB에서 받아서 넣어야함)
        filterList = ArrayList()
        filterList.add(FilterItem("삼성드림이비인후과", "역삼동 826-24 화인타워 5,6층", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업"))))
        filterList.add(FilterItem("강남성모이비인후과의원", "서울특별시 강남", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업"))))
        filterList.add(FilterItem("강남코모키의원", "서울특별시 강남", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무"))))
        filterList.add(FilterItem("강남서울이비인후과", "서울특별시 강남", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무"))))
        filterList.add(FilterItem("청소년내과", "충청북도 청주시", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무"))))
        filterList.add(FilterItem("동탄호수소아청소년과의원", "경기도 화성시 송동", listOf("소아청소년과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무"))))
        filterList.add(FilterItem("우리의원", "경기도 오산시 궐동", listOf("외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무"))))
        filterList.add(FilterItem("새봄연합의원외과", "경기도 오산시 갈곶동", listOf("가정의학과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무"))))
        filterList.add(FilterItem("두리이비인후과의원", "경기도 화성시 송동", listOf("이비인후과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업"))))
        filterList.add(FilterItem("분당서울여성의원", "경기도 성남시 분당구 정자동", listOf("산부인과",), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업"))))


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

        recentSearchWordList = intent.getSerializableExtra("searchWordList") as? ArrayList<RecentItem> ?: ArrayList()
        Log.w("HospitalListActivity", "1. $recentSearchWordList")

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
        val intent = Intent(this, MainActivity::class.java) // 지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
        intent.putExtra("searchWordList", recentSearchWordList)
        startActivity(intent) // 인텐트 이동
        finish() // 현재 액티비티 종료
    }

    //검색 필터
    private fun searchFilterAction(string: String) {
        searchTextField.setText(string)

        //필터기능 구현
        val searchString = searchTextField.text.toString().trim()

        val filteredList = filterList.filter { filterItem ->
                    filterItem.hospitalName.contains(searchString, true) ||
                    filterItem.hospitalAddress.contains(searchString, true) ||
                    filterItem.className.any { it.contains(searchString, true) } ||
                    filterItem.weekTime.any { it.value.contains(searchString, true) }
        }

        hospitalList = ArrayList()
        for(i in filteredList.indices) {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = 30 //calendar.get(Calendar.DATE)

            val dayOfWeek = getDayOfWeek(currentYear, currentMonth, currentDay) //현재 요일 구하기
            val dayTime = filteredList[i].weekTime[dayOfWeek] //현재 요일에 맞는 영업시간 가져오기

            hospitalList.add(HospitalItem(filteredList[i].hospitalName, "4.0", dayTime ?: "영업시간 정보 없음", filteredList[i].hospitalAddress, filteredList[i].className))
        }
        adapter.updatelist(hospitalList)
    }

    //
    //년, 월, 일 해당하는 날짜의 요일 구하기
    private fun getDayOfWeek(year:Int, month:Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }
    }

}