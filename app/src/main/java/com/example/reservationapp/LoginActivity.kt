package com.example.reservationapp

import android.content.Intent
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.example.reservationapp.databinding.ActivityLoginBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//로그인 화면
class LoginActivity: AppCompatActivity() {
    lateinit var lContext: Context
    private lateinit var binding: ActivityLoginBinding
    private lateinit var DomainAddress: String //유저가 선택한 도메인주소
    private lateinit var userEmail: String //유저가 직접 입력한 이메일
    private lateinit var userPassword: String //유저가 입력한 비밀번호

    //onStart()할 때마다 입력칸 초기화 시키기 위해 전역변수 선언
    private lateinit var EmailEditText: EditText
    private lateinit var PasswordEditText: EditText
    private lateinit var PasswordCheckTextView: TextView

    private var emailFlag: Boolean = false //email 감지 플래그
    private var pwFlag: Boolean = false //pw 감지 플래그
    private var flag: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //초기화
        lContext = this
        EmailEditText = binding.EmailEditText
        PasswordEditText = binding.PasswordEditText
        PasswordCheckTextView = binding.PasswordCheckTextView

        val LoginButton = binding.LoginButton
        val SignUpButton = binding.SingupButton



        //spinner 이메일 도메인 주소
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



        //email 변경 감지 되었을때
        val emailWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailFlag = EmailEditText.text.toString() != "" //emailEditText 비어 있지 않으면 true
            }
        }
        EmailEditText.addTextChangedListener(emailWatcher) //email 변경 감지 이벤트 리스너

        //비밀번호 변경 감지 되었을때
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
        val EmailPasswordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                LoginButton.isEnabled = emailFlag && pwFlag
                /*
                if(emailFlag && pwFlag) {//버튼 활성화
                    LoginButton.isEnabled = true
                    //LoginButton.setBackgroundColor(Color.parseColor("#8AF311"))
                } else {//버튼 비활성화
                    LoginButton.isEnabled = false
                    //LoginButton.setBackgroundColor(Color.parseColor("#666A73"))
                }
                 */
            }
        }
        EmailEditText.addTextChangedListener(EmailPasswordWatcher)
        PasswordEditText.addTextChangedListener(EmailPasswordWatcher)
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //회원가입 버튼 눌렀을때
        SignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpPatient::class.java)
            startActivity(intent)
            finish()
        }
        //
    }
}