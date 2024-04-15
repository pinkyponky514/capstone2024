package com.example.reservationapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.reservationapp.databinding.ActivityMainBinding
import com.example.reservationapp.navigation.HomeFragment

//메인메뉴
class MainActivity : AppCompatActivity() {
    lateinit var mContext: Context
    private lateinit var binding: ActivityMainBinding
    //var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_main)

        mContext = this //다른 class에서 main class의 함수를 쓸 수 있도록

        binding.navigation.selectedItemId = R.id.homeFrag //처음 실행시 홈 선택으로 시작
        setFragment(HomeFragment())
        //네비게이션바 선택에 따른 화면전환
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFrag -> {
                    setFragment(HomeFragment())
                }
                R.id.moreFrag -> { //더보기 버튼 클릭했을때
                        setActivity(mContext, HPDivisonActivity())
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

    //액티비티 전환 사용자정의 함수
    fun setActivity(context: Context, activity: Any) {
        val intent = Intent(context, activity::class.java)
        context.startActivity(intent)
        //(context as Activity).finish()
    }
    //프래그먼트 전환 사용자정의 함수
    fun setFragment(fragment: Any) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment as Fragment).commit()
    }

}