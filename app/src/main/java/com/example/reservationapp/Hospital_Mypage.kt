package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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

        val classReserveList: List<String> = listOf("내과", "외과", "이비인후과", "피부과", "안과", "성형외과", "신경외과", "소아청소년과")

        // 진료과별 예약 버튼 설정
        for (i in 0 until classReserveList.size) {
            val classButtonId = resources.getIdentifier("class_button${i + 1}", "id", packageName)
            val button = findViewById<Button>(classButtonId)

            button.text = classReserveList[i]
            button.setOnClickListener {
                // 진료과 로그로 출력
                val selectedClass = button.text.toString()
                Log.d("Hospital_Mypage", "선택된 진료과: $selectedClass")

                // 버튼의 상태 확인
                val isSelected = button.isSelected

                // 버튼 상태 변경
                button.isSelected = !isSelected

                // 상태에 따라 배경색 변경
                if (isSelected) {
                    // 버튼이 선택된 상태라면 원래의 배경색으로 변경
                    button.setBackgroundResource(R.drawable.button_shadow) // 버튼 배경색 변경
                } else {
                    // 버튼이 선택되지 않은 상태라면 새로운 배경색으로 변경
                    button.setBackgroundResource(R.drawable.button_shadow) // 버튼 배경색 변경
                }
            }
        }


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
        val MonStartTime = findViewById<EditText>(R.id.MonStartTime).text.toString()
        val MonEndTime = findViewById<EditText>(R.id.MonEndTime).text.toString()
        val TueStartTime = findViewById<EditText>(R.id.TueStartTime).text.toString()
        val TueEndTime = findViewById<EditText>(R.id.TueEndTime).text.toString()
        val WedStartTime = findViewById<EditText>(R.id.WedStartTime).text.toString()
        val WedEndTime = findViewById<EditText>(R.id.WedEndTime).text.toString()
        val ThuStartTime = findViewById<EditText>(R.id.ThuStartTime).text.toString()
        val ThuEndTime = findViewById<EditText>(R.id.ThuEndTime).text.toString()
        val FriStartTime = findViewById<EditText>(R.id.FriStartTime).text.toString()
        val FriEndTime = findViewById<EditText>(R.id.FriEndTime).text.toString()
        val SatStartTime = findViewById<EditText>(R.id.SatStartTime).text.toString()
        val SatEndTime = findViewById<EditText>(R.id.SatEndTime).text.toString()
        val SunStartTime = findViewById<EditText>(R.id.SunStartTime).text.toString()
        val SunEndTime = findViewById<EditText>(R.id.SunEndTime).text.toString()

        val DayStartTime = findViewById<EditText>(R.id.DayStartTime).text.toString()
        val DayEndTime = findViewById<EditText>(R.id.DayEndTime).text.toString()
        val WeekendStartTime = findViewById<EditText>(R.id.WeekendStartTime).text.toString()
        val WeekendEndTime = findViewById<EditText>(R.id.WeekendEndTime).text.toString()


        Log.d("Hospital_Mypage", "월요일 진료시간: $MonStartTime ~ $MonEndTime")
        Log.d("Hospital_Mypage", "화요일 진료시간: $TueStartTime ~ $TueEndTime")
        Log.d("Hospital_Mypage", "수요일 진료시간: $WedStartTime ~ $WedEndTime")
        Log.d("Hospital_Mypage", "목요일 진료시간: $ThuStartTime ~ $ThuEndTime")
        Log.d("Hospital_Mypage", "금요일 진료시간: $FriStartTime ~ $FriEndTime")
        Log.d("Hospital_Mypage", "토요일 진료시간: $SatStartTime ~ $SatEndTime")
        Log.d("Hospital_Mypage", "일요일 진료시간: $SunStartTime ~ $SunEndTime")
        Log.d("Hospital_Mypage", "평일 점심시간: $DayStartTime ~ $DayEndTime")
        Log.d("Hospital_Mypage", "주말 진료시간: $WeekendStartTime ~ $WeekendEndTime")

        // 병원 화면으로 돌아가는 코드는 이 함수 안에 있어야 합니다.
        val intent = Intent(this, HospitalActivity::class.java)
        startActivity(intent)
    }
}
