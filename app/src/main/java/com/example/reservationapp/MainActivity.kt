package com.example.reservationapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.databinding.ActivityMainBinding
import com.example.reservationapp.navigation.CommunityFragment
import com.example.reservationapp.navigation.HomeFragment
import com.example.reservationapp.navigation.MedicalHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

var userMapx: Double = 37.5 //사용자 위도
var userMapy: Double = 126.9 //사용자 경도


//메인메뉴
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var searchRecentWordList = ArrayList<RecentItem>()
    lateinit var navigation: BottomNavigationView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_main)

        tokenCheck() //토큰 만료됐는지 확인


        if(App.prefs.token != null) {
        Log.w("MainActivity", "App.prefs.token: ${App.prefs.token}")
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
                    //if(userId == "" || userToken == "") { //userId가 비어 있을 경우 = 로그인 안한 경우
                    if(App.prefs.token == null) {
                        setActivity(this, HPDivisonActivity())
                    } else {  //userId가 있는 경우 = 로그인 한 경우
                        setActivity(this, MyProfileActivity())
                    }
                }
            }
            true
        }
    }

    fun tokenCheck() {
        val token = App.prefs.token ?: ""
        var isExpired = isTokenExpired(token)
        if (isExpired) {
            Log.w("MainActivity", "로그인 토큰 만료 or 이상한 토큰임")
            App.prefs.clearToken(this)
        } else {
            Log.w("MainActivity", "로그인 토큰 만료 안됐음")
        }
    }
    //
}