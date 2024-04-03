package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// 회원가입 메인화면
class SignUpActivity : AppCompatActivity() {


    private lateinit var HPDivison: String
    private lateinit var PatientButton: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        HPDivison = intent?.getStringExtra("HPDivison").toString()
        PatientButton = intent?.getStringExtra("PatientButton").toString()


        // 회원가입 버튼
        val SignUpTextView: TextView = findViewById(R.id.textView6)
        SignUpTextView.setOnClickListener {

            if(HPDivison == "1") { //병원
                val intent = Intent(this, SignUpDoctor::class.java)
                startActivity(intent)
            } else if(HPDivison == "2") { //환자
                val intent = Intent(this, SignUpPatient::class.java)
                startActivity(intent)
            } else {
                Log.w("HPDivison intent error : ", HPDivison)
            }
            finish()
        }

        // 로그인 버튼
        val loginButton: Button = findViewById(R.id.button)
        loginButton.setOnClickListener {
            if(HPDivison == "1") { //병원
                val intent = Intent(this, LoginDoctorActivity::class.java)
                startActivity(intent)
            } else if(HPDivison == "2") { //환자
                val intent = Intent(this, LoginPatientActivity::class.java)
                startActivity(intent)
            } else {
                Log.w("HPDivison intent error : ", HPDivison)
            }
            finish()
        }

        val button: Button = findViewById(R.id.button)
        button.contentDescription = "로그인 버튼"
    }
}
