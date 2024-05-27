package com.example.reservationapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalUserLoginResponse
import com.example.reservationapp.Model.UserLoginInfoRequest
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//병원측 로그인 화측
class LoginDoctorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var userId: String //유저가 직접 입력한 사업자번호
    private lateinit var userPassword: String //유저가 입력한 비밀번호


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService


    //onStart() 할 때마다 입력칸 초기화 시키기 위해 전역변수 선언
    private lateinit var idEditText: EditText
    private lateinit var PasswordEditText: EditText
    private lateinit var PasswordCheckTextView: TextView
    private lateinit var IdCheckTextview: TextView

    //감지 플래그
    private var BusinessNumberFlag: Boolean = false //사업자번호 감지 플래그
    private var pwFlag: Boolean = false //비밀번호 감지 플래그
    private var flag: Boolean = false


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) { // 화면을 터치했을 때 포커스 해제
            idEditText.clearFocus()
            PasswordEditText.clearFocus()
            currentFocus?.clearFocus()
            hideKeyboard()
        }
        return super.onTouchEvent(event)
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        idEditText = binding.IdEditText
        PasswordEditText = binding.PasswordEditText
        PasswordCheckTextView = binding.PasswordCheckTextView

        val LoginButton = binding.LoginButton
        val SignUpButton = binding.SingupButton


        //
        //아이디 textview 변경 감지되었을때
        val BusinessNumberWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(idEditText.text.toString() != "") {
                    IdCheckTextview.text = ""
                    IdCheckTextview.visibility = View.GONE
                    BusinessNumberFlag = true
                } else {
                    IdCheckTextview.text = "아이디를 입력하시오."
                    IdCheckTextview.visibility = View.VISIBLE
                    BusinessNumberFlag = false
                }
            }
        }
        idEditText.addTextChangedListener(BusinessNumberWatcher) //사업자번호 변경 감지 이벤트 리스너

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
                if(!LoginButton.isEnabled) { //false이면 배경색 gray로
                    LoginButton.setBackgroundResource(R.drawable.style_gray_radius_20)
                } else {
                    LoginButton.setBackgroundResource(R.drawable.style_dark_green_radius_20_pressed_button)
                }
            }
        }
        idEditText.addTextChangedListener(BusinessPasswordWatcher)
        PasswordEditText.addTextChangedListener(BusinessPasswordWatcher)
        //


        //
        //로그인 버튼 눌렀을때
        LoginButton.setOnClickListener {
            userId = idEditText.text.toString()
            userPassword = PasswordEditText.text.toString()
            Log.w("LoginDoctorActivity", "userBusinessNumber: $userId, userPassword: $userPassword")

            val userLoginInfo = UserLoginInfoRequest(userId, userPassword)

            App.prefs.token = null
            retrofitClient = RetrofitClient.getInstance()
            apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)

            apiService.postHospitalLogin(userLoginInfo).enqueue(object: Callback<HospitalUserLoginResponse> {
                override fun onResponse(call: Call<HospitalUserLoginResponse>, response: Response<HospitalUserLoginResponse>) {
                    if(response.isSuccessful) {
                        val response = response.body()!!
                        val data = response.data
                        val userToken = data.token

                        App.prefs.token = "Bearer "+userToken //로그인 시 받은 토큰 저장

                        val intent = Intent(this@LoginDoctorActivity, HospitalMainActivity::class.java)
                        intent.putExtra("hospitalId", data.hospitalId) //hospitalId(기본키) 넘겨주기
                        Log.d("LoginDoctorActivity", "userId: ${userLoginInfo.id}, userToken: $userToken")

                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
                        startActivity(intent)
                        finish()
                    }
                    else
                        Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                }

                override fun onFailure(call: Call<HospitalUserLoginResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
                }
            })
        }
        //회원가입 버튼 눌렀을때
        SignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpDoctor::class.java)
            startActivity(intent)
            finish()
        }
        //
    }

    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        idEditText.clearFocus()
        PasswordEditText.clearFocus()
        currentFocus?.clearFocus()
        finish() //현재 액티비티 종료
    }

}