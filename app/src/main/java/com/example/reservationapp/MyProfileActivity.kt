package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reservationapp.databinding.ActivityMainBinding
import com.example.reservationapp.databinding.ActivityMyProfileBinding

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textView = binding.infoTextView
        textView.text = "userToken: ${App.prefs.token}" //textView.text = "userId: ${userId}, userToken: ${userToken}, userName: ${userName}"


        //로그아웃 버튼 onClick
        val logoutButton = binding.logoutButton
        logoutButton.setOnClickListener {
            App.prefs.clearToken(this)
            val intent = Intent(this, HPDivisonActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}