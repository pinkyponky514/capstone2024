package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reservationapp.databinding.ActivityHospitalMainBinding
import com.example.reservationapp.navigation.HospitalFragment
import com.example.reservationapp.navigation.HospitalMypageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HospitalMainActivity : AppCompatActivity() {
    // View Binding을 사용하여 레이아웃의 뷰를 초기화하기 위한 변수 선언
    private lateinit var binding: ActivityHospitalMainBinding
    // BottomNavigationView를 사용할 변수 선언
    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding 초기화
        binding = ActivityHospitalMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션 뷰 초기화
        navigation = binding.navigation
        // 초기 선택 아이템 설정
        navigation.selectedItemId = R.id.hospital_main

        /*
        // WebView를 사용하기 위한 초기 설정 코드 (현재 주석 처리됨)
        webView = WebView(this)

        // WebView 설정
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true // JavaScript 활성화

        // HTML 파일 로드
        webView.loadUrl("file:///android_asset/daum.html")

        // 안드로이드 앱에서 WebView에 JavaScript Interface 제공
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun sendDataToAndroid(data: String) {
                // JavaScript에서 데이터를 받아 처리하는 코드
            }
        }, "Android")

        // WebView에서 안드로이드 앱의 메서드 호출
        webView.loadUrl("javascript:sendDataToAndroid('Hello from JavaScript!')")
        */

        // 인텐트에서 show_fragment 값을 확인하고 HospitalFragment를 표시
        val fragmentToShow = when (intent.getStringExtra("show_fragment")) {
            "hospital" -> HospitalFragment()
            else -> HospitalFragment() // 기본 프래그먼트 설정
        }
        // 선택된 프래그먼트를 표시
        hospitalSetFragment(fragmentToShow)

        // 네비게이션 아이템 클릭 리스너 설정
        hospitalNavigationSetItem()
    }

    // 네비게이션 아이템 클릭 리스너 설정 함수
    private fun hospitalNavigationSetItem() {
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.hospital_main -> {
                    // hospital_main 아이템이 선택되면 HospitalFragment를 표시
                    hospitalSetFragment(HospitalFragment())
                    true
                }
                R.id.hospital_mypage -> {
                    // hospital_mypage 아이템이 선택되면 HospitalMypageFragment를 표시
                    hospitalSetFragment(HospitalMypageFragment())
                    true
                }
                else -> false
            }
        }
    }

    // 프래그먼트를 전환하는 함수
    private fun hospitalSetFragment(fragment: Fragment) {
        // 선택된 프래그먼트로 main_content를 교체
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()
    }
}
