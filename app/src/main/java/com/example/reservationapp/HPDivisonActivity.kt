package com.example.reservationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.reservationapp.databinding.ActivityHpdivisonBinding

//로그인시 환자, 병원 선택구분
class HPDivisonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHpdivisonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHpdivisonBinding.inflate(layoutInflater)
        setContentView(binding.root)  //setContentView(R.layout.activity_hpdivison)

        // 환자로 로그인할 경우, 환자 키번호 "2" 전달
        binding.PatientButton.setOnClickListener {
            MainActivity().setActivity(this, LoginActivity())
        }
    }
}