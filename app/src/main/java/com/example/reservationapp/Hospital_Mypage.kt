package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Hospital_Mypage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hospital_mypage)

        // 라디오 버튼 그룹 찾기
        val restdayRadioGroup = findViewById<RadioGroup>(R.id.restday_radio_group)
        // 라디오 버튼 클릭 이벤트 처리
        restdayRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val restday = selectedRadioButton.text.toString()
            Log.d("Hospital_Mypage", "공휴일: $restday")
        }

        // 병원 소개 텍스트 창
        val hospitalIntroEditText = findViewById<EditText>(R.id.explanation2)
        // 병원 소개 입력값 로그에 출력
        hospitalIntroEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val hospitalIntro = hospitalIntroEditText.text.toString()
                Log.d("Hospital_Mypage", "병원 소개: $hospitalIntro")
            }
        }
    }

    fun onBackToHospitalClicked(view: View) {
        // 입력된 값을 로그에 출력
        val monTime = findViewById<EditText>(R.id.editTextMon).text.toString()
        val tueTime = findViewById<EditText>(R.id.editTextTue).text.toString()
        val wedTime = findViewById<EditText>(R.id.editTextWed).text.toString()
        val thuTime = findViewById<EditText>(R.id.editTextThu).text.toString()
        val friTime = findViewById<EditText>(R.id.editTextFri).text.toString()
        val satTime = findViewById<EditText>(R.id.editTextSat).text.toString()
        val sunTime = findViewById<EditText>(R.id.editTextSun).text.toString()
        val weekdayLunchTime = findViewById<EditText>(R.id.editTextWeekdayLunchTime).text.toString()
        val weekendLunchTime = findViewById<EditText>(R.id.editTextWeekendLunchTime).text.toString()

        Log.d("Hospital_Mypage", "월요일 진료시간: $monTime")
        Log.d("Hospital_Mypage", "화요일 진료시간: $tueTime")
        Log.d("Hospital_Mypage", "수요일 진료시간: $wedTime")
        Log.d("Hospital_Mypage", "목요일 진료시간: $thuTime")
        Log.d("Hospital_Mypage", "금요일 진료시간: $friTime")
        Log.d("Hospital_Mypage", "토요일 진료시간: $satTime")
        Log.d("Hospital_Mypage", "일요일 진료시간: $sunTime")
        Log.d("Hospital_Mypage", "평일 점심시간: $weekdayLunchTime")
        Log.d("Hospital_Mypage", "주말 점심시간: $weekendLunchTime")

        // 병원 화면으로 돌아가는 코드는 이 함수 안에 있어야 합니다.
        val intent = Intent(this, HospitalActivity::class.java)
        startActivity(intent)
    }
}
