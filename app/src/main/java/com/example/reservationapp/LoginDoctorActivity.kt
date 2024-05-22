package com.example.reservationapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
    private lateinit var BusinessNumberText: EditText
    private lateinit var PasswordEditText: EditText
    private lateinit var PasswordCheckTextView: TextView

    //감지 플래그
    private var BusinessNumberFlag: Boolean = false //사업자번호 감지 플래그
    private var pwFlag: Boolean = false //비밀번호 감지 플래그
    private var flag: Boolean = false



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        BusinessNumberText = binding.IdEditText
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
            userId = BusinessNumberText.text.toString()
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
}