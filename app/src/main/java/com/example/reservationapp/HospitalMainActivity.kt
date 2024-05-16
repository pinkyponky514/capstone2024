package com.example.reservationapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.reservationapp.R
import com.example.reservationapp.databinding.ActivityHospitalMainBinding
import com.example.reservationapp.navigation.HospitalFragment
import com.example.reservationapp.navigation.HospitalMypageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HospitalMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalMainBinding
    private lateinit var navigation: BottomNavigationView // 네비게이션 뷰 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigation = binding.navigation // 네비게이션 뷰 초기화
        navigation.selectedItemId = R.id.hospital_main

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
