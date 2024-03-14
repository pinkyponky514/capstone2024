package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// 회원가입 메인화면
class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        
        // 회원가입 버튼
        val SignUpTextView: TextView = findViewById(R.id.textView6)
        SignUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpPatient::class.java)
            startActivity(intent)
            finish()
        }

        // 로그인 버튼
        val loginButton: Button = findViewById(R.id.button)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val button: Button = findViewById(R.id.button)
        button.contentDescription = "로그인 버튼"
    }
}
