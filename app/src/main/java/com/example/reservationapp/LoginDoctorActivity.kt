package com.example.reservationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.example.reservationapp.databinding.ActivityLoginDoctorBinding

//병원측 로그인 화측
class LoginDoctorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginDoctorBinding
    private lateinit var userBusinessNumber: String //유저가 직접 입력한 사업자번호
    private lateinit var userPassword: String //유저가 입력한 비밀번호


    //onStart() 할 때마다 입력칸 초기화 시키기 위해 전역변수 선언
    private lateinit var BusinessNumberText: EditText
    private lateinit var PasswordEditText: EditText
    private lateinit var PasswordCheckTextView: TextView

    //감지 플래그
    private var BusinessNumberFlag: Boolean = false //사업자번호 감지 플래그
    private var pwFlag: Boolean = false //비밀번호 감지 플래그
    private var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        BusinessNumberText = binding.BusinessNumberEditText
        PasswordEditText = binding.PasswordEditText
        PasswordCheckTextView = binding.PasswordCheckTextView

        val LoginButton = binding.LoginButton
        val SignUpButton = binding.SingupButton


        //
        //사업자번호 textview 변경 감지되었을때
        val BusinessNumberWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                BusinessNumberFlag = BusinessNumberText.text.toString() != "" //BusinessNumberText 비어 있지 않으면 true
            }
        }
        BusinessNumberText.addTextChangedListener(BusinessNumberWatcher) //사업자번호 변경 감지 이벤트 리스너

        //비밀번호 editText 변경 감지되었을때
        val pwWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (PasswordEditText.text.toString().length >= 8) { //비밀번호 8자리 이상인 경우
                    //경고문구 표시
                    PasswordCheckTextView.text = ""
                    pwFlag = true
                } else { //비밀번호 8자리 미만인 경우
                    PasswordCheckTextView.text = "8자리 이상 입력하시오."
                    pwFlag = false
                }
            }
        }
        PasswordEditText.addTextChangedListener(pwWatcher) //비밀번호 감지할 수 있도록 이벤트 리스너
        //둘다 감지 되었을때 버튼 활성화
        val BusinessPasswordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                LoginButton.isEnabled = BusinessNumberFlag && pwFlag
            }
        }
        BusinessNumberText.addTextChangedListener(BusinessPasswordWatcher)
        PasswordEditText.addTextChangedListener(BusinessPasswordWatcher)
        //


        //
        //로그인 버튼 눌렀을때
        LoginButton.setOnClickListener {
            userBusinessNumber = BusinessNumberText.text.toString()
            userPassword = PasswordEditText.text.toString()

            Log.w("userBusinessNumber, userPassword", ": $userBusinessNumber" + ", $userPassword")
            val intent = Intent(this, HospitalActivity::class.java)
            startActivity(intent)
            finish()
        }
        //회원가입 버튼 눌렀을때
        SignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpDoctor::class.java)
            startActivity(intent)
            finish()
        }
        //

    }
}