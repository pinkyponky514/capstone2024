package com.example.reservationapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.reservationapp.Model.FilterItem
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Model.filterList
import com.example.reservationapp.Model.reviewList
import com.example.reservationapp.Model.userHospitalFavorite
import com.example.reservationapp.databinding.ActivityMainBinding
import com.example.reservationapp.navigation.CommunityFragment
import com.example.reservationapp.navigation.HomeFragment
import com.example.reservationapp.navigation.MedicalHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

var userId: String = ""
var userName: String = ""
var userToken: String = ""

//메인메뉴
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var searchRecentWordList = ArrayList<RecentItem>()

    lateinit var navigation: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_main)

        val intentUserId = intent.getStringExtra("userId") //userId 초기화
        val intentUserToken = intent.getStringExtra("userToken") //userToken 초기화
        Log.w("MainActivity", "intentUserId: $intentUserId, intentUserToken: $intentUserToken")

        if(intentUserId != null) { userId = intentUserId.toString() }
        if(intentUserToken != null) { userToken = intentUserToken.toString() }
        Log.w("MainActivity", "userId: $userId, userToken: $userToken, userName: $userName")


        if(reviewList.isEmpty() && filterList.isEmpty()) {
            //병원정보 초기화 (DB에서 받아서 넣어야함)
/*
            reviewList = ArrayList()
            filterList = ArrayList()
*/

            reviewList.add(ReviewItem("4.0", "병원이 너무 좋아요", "2024.04.01", "hansung"))
            reviewList.add(ReviewItem("3.0", "솔직하게 말하자면 병원이 크고 시설도 다 좋은데, 의사 선생님이나 간호사 분들이 너무 불친절합니다.", "2024.04.05", "user1"))
            reviewList.add(ReviewItem("4.5", "다닐만 합니다", "2024.04.20", "user2"))
            reviewList.add(ReviewItem("2.0", "대기 시간도 너무 길고 신설 병원이라 그런지 너무 체계가 엉망입니다", "2024.05.01", "user3"))
            reviewList.add(ReviewItem("4.0", "병원이 너무 좋아요", "2024.05.03", "user4"))
            reviewList.add(ReviewItem("4.5", "병원이 너무 좋아요", "2024.05.05", "user5"))
            reviewList.add(ReviewItem("4.5", "병원이 너무 좋아요", "2024.05.05", "user5"))
            reviewList.add(ReviewItem("4.5", "병원이 너무 좋아요", "2024.05.05", "user5"))
            reviewList.add(ReviewItem("4.5", "병원이 너무 좋아요", "2024.05.05", "user5"))
            reviewList.add(ReviewItem("4.5", "병원이 너무 좋아요", "2024.05.05", "user5"))


            filterList.add(FilterItem("삼성드림이비인후과", "역삼동 826-24 화인타워 5,6층", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업")), 5, reviewList, "4.0", 5))
            filterList.add(FilterItem("강남성모이비인후과의원", "서울특별시 강남", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업")), 6, reviewList, "4.0", 6))
            filterList.add(FilterItem("강남코모키의원", "서울특별시 강남", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무")), 4, reviewList, "4.0", 4))
            filterList.add(FilterItem("강남서울이비인후과", "서울특별시 강남", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무")), 7, reviewList, "4.0", 7))
            filterList.add(FilterItem("청소년내과", "충청북도 청주시", listOf("이비인후과", "내과", "외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무")), 2, reviewList, "4.0", 2))
            filterList.add(FilterItem("동탄호수소아청소년과의원", "경기도 화성시 송동", listOf("소아청소년과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무")), 10, reviewList, "4.0", 8))
            filterList.add(FilterItem("우리의원", "경기도 오산시 궐동", listOf("외과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무")), 9, ArrayList(), "4.0", 9))
            filterList.add(FilterItem("새봄연합의원외과", "경기도 오산시 갈곶동", listOf("가정의학과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","휴무")), 10, reviewList, "4.0", 10))
            filterList.add(FilterItem("두리이비인후과의원", "경기도 화성시 송동", listOf("이비인후과"), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업")), 3, ArrayList(), "4.0", 3))
            filterList.add(FilterItem("분당서울여성의원", "경기도 성남시 분당구 정자동", listOf("산부인과",), hashMapOf(Pair("월","9:00~15:00"), Pair("화","9:00~16:00"), Pair("수","9:00~17:00"), Pair("목","9:00~18:00"), Pair("금","9:00~19:00"), Pair("토","9:00~14:00"), Pair("일","정기휴무"), Pair("점심","12:00~13:00"), Pair("공휴일","정상영업")), 1, reviewList, "4.0", 1))
        }

        //최근검색어
        //searchRecentWordList = intent.getSerializableExtra("searchWordList") as? ArrayList<RecentItem> ?: ArrayList()


        //처음 실행시 홈 선택으로 시작
        navigation = binding.navigation
        navigation.selectedItemId = R.id.homeFrag
        setFragment(HomeFragment())


        //네비게이션바 선택에 따른 화면전환
        navigationSetItem()
    }

    //액티비티 전환 사용자정의 함수
    fun setActivity(context: Context, activity: Any) {
        val intent = Intent(context, activity::class.java)
        context.startActivity(intent)
        //(context as Activity).finish()
    }
    //프래그먼트 전환 사용자정의 함수
    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()
    }

    fun navigationSetItem() {
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFrag -> { //홈 버튼 클릭했을 때
                    setFragment(HomeFragment())
                }
                R.id.checkupFrag -> { //나의 진료내역 버튼 클릭했을 때
                    setFragment(MedicalHistoryFragment())
                }
                R.id.communityFrag -> { //커뮤니티 버튼 클릭했을 때
                    setFragment(CommunityFragment())
                }
                R.id.moreFrag -> { //더보기 버튼 클릭했을때
                    if(userId == "" || userToken == "") { //userId가 비어 있을 경우 = 로그인 안한 경우
                        setActivity(this, HPDivisonActivity())
                    } else {  //userId가 있는 경우 = 로그인 한 경우
                        setActivity(this, MyProfileActivity())
                    }
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        Log.w("MainActivity", "favorite Button Boolean : ${userHospitalFavorite}")
    }

    //
}