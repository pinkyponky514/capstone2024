package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.databinding.ActivityHospitalDetailpageBinding

class Hospital_DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalDetailpageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHospitalDetailpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 예약 버튼 클릭 이벤트 설정
        binding.ReservationButton.setOnClickListener {
            val intent = Intent(this, Final_Reservation::class.java)
            startActivity(intent)
            finish()
        }
    }
}
