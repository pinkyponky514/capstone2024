package com.example.reservationapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reservationapp.databinding.ActivityHospitalMainBinding
import com.example.reservationapp.navigation.HospitalMypageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.reservationapp.navigation.HospitalFragment

class HospitalMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalMainBinding
    private lateinit var navigation: BottomNavigationView // 네비게이션 뷰 추가

    var hospitalId: Long = 0 //public

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        hospitalId = intent.getLongExtra("hospitalId", 0)
        Log.d("HospitalActivity", "hospitalId: $hospitalId") // hospitalId 로그 확인


        navigation = binding.navigation // 네비게이션 뷰 초기화
        navigation.selectedItemId = R.id.hospital_main

        // 인텐트에서 show_fragment 값을 확인하고 HospitalFragment를 표시
        val fragmentToShow = when (intent.getStringExtra("show_fragment")) {
            "hospital" -> HospitalFragment()
            else -> HospitalFragment() // 기본 프래그먼트 설정
        }
        hospitalSetFragment(HospitalFragment())
        hospitalNavigationSetItem()
    }



    // 네비게이션 아이템 설정 함수
    private fun hospitalNavigationSetItem() {
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.hospital_main -> {
                    hospitalSetFragment(HospitalFragment())
                    true
                }
                R.id.hospital_mypage -> {
                    hospitalSetFragment(HospitalMypageFragment())
                    true
                }
                else -> false
            }
        }
    }


    // 프래그먼트 전환 함수
    private fun hospitalSetFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()
    }

}