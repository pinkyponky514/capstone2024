package com.example.reservationapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.databinding.ActivityMainBinding
import com.example.reservationapp.navigation.CommunityFragment
import com.example.reservationapp.navigation.HomeFragment
import com.example.reservationapp.navigation.MedicalHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarMenu
import com.google.android.material.navigation.NavigationView

//메인메뉴
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var searchRecentWordList = ArrayList<RecentItem>()

    private var userName: String = ""

    lateinit var navigation: BottomNavigationView
    lateinit var medicalHistoryFragment: MedicalHistoryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_main)

        searchRecentWordList = intent.getSerializableExtra("searchWordList") as? ArrayList<RecentItem> ?: ArrayList()
        medicalHistoryFragment = MedicalHistoryFragment.newInstance("")

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
    /*
    fun setFragment(context: Context,fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(context,fragment, null)
            addToBackStack(null)
        }
    }
    */

    fun navigationSetItem() {
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFrag -> { //홈 버튼 클릭했을 때
                    setFragment(HomeFragment())
                }
                R.id.checkupFrag -> { //나의 진료내역 버튼 클릭했을 때
                    //setFragment(MedicalHistoryFragment())
                    setFragment(medicalHistoryFragment)
                }
                R.id.communityFrag -> { //커뮤니티 버튼 클릭했을 때
                    setFragment(CommunityFragment())
                }
                R.id.moreFrag -> { //더보기 버튼 클릭했을때
                    if(userName != "") { //로그인 했을때 = 유저이름이 있으면

                    } else { //로그인 안했을때 = 유저이름 없을때
                        setActivity(this, HPDivisonActivity())
                    }

                    /*
                    val intent = Intent(this, HPDivisonActivity::class.java)
                    startActivity(intent)
                    finish()
                    */
                }
            }
            true
        }
    }

    //
}