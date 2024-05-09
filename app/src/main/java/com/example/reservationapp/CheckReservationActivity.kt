package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.databinding.ActivityCheckReservationBinding

class CheckReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckReservationBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentDataItem = intent.getSerializableExtra("reserveDataItem") as HistoryItem
        val textView = binding.checkTextView
        textView.text = "진료상태: ${intentDataItem.status}, 병원이름: ${intentDataItem.hospitalName}, 진료과명: ${intentDataItem.className}, 예약날짜: ${intentDataItem.reserveDay}, 예약시간: ${intentDataItem.reserveTime}"
    }


    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) // 지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
        startActivity(intent) // 인텐트 이동
        finish() // 현재 액티비티 종료
    }

}