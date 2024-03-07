package com.example.reservationapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.reservationapp.databinding.ActivityLoginBinding

//로그인 화면
class LoginActivity: AppCompatActivity() {
    lateinit var lContext: Context
    private lateinit var binding: ActivityLoginBinding
    private lateinit var DomainAddress: String //유저가 선택한 도메인주소
    private lateinit var userEmail: String //유저가 직접 입력한 이메일
    private lateinit var userPassword: String //유저가 입력한 비밀번호

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_login)

        //초기화
        lContext = this
        val LoginButton = binding.LoginButton
        val EmailEditText = binding.EmailEditText
        val PasswordEditText = binding.PasswordEditText

        //
        // spinner 이메일 도메인 주소
        val emailSpinner = arrayOf("@gmail.com", "@naver.com", "@nate.com", "@hansung.ac.kr", "직접입력")
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, emailSpinner)

        //배열과 spinner 연결
        val spinner = binding.EmailSpinner
        spinner.adapter = spinnerAdapter //== binding.EmailSpinner.adapter = spinnerAdapter

        //spinner 선택 데이터 받아오기
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            //선택했을때
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.w("Domain : ", emailSpinner[position])
                DomainAddress = emailSpinner[position]
            }
            //아무것도 선택 안했을때
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.w("선택안함, Domain : ", emailSpinner[4])
            }
        }
        //

        //로그인 버튼 눌렀을때
        LoginButton.setOnClickListener {
            if(DomainAddress == emailSpinner[4]) { //직접입력일 경우
                userEmail = EmailEditText.text.toString()
            } else {
                userEmail = EmailEditText.text.toString() + DomainAddress
            }
            userPassword = PasswordEditText.text.toString()
            Log.w("userEmail, userPassword", ": $userEmail" + ", $userPassword")
        }

        //회원가입 버튼 눌렀을때
        //
    }
}