package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.databinding.ActivityHospitalSecurityBinding

class HospitalSecurityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalSecurityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.securityButton.setOnClickListener {
            onBackToHospitalClicked()
        }
    }

    private fun onBackToHospitalClicked() {
        val intent = Intent(this, HospitalMainActivity::class.java)
        intent.putExtra("show_fragment", "hospital")
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}
