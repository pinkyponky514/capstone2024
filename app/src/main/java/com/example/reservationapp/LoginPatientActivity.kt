package com.example.reservationapp

import com.example.reservationapp.Retrofit.RetrofitClient
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
import com.example.reservationapp.Model.UserLoginInfoRequest
import com.example.reservationapp.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//로그인 화면
class LoginPatientActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    lateinit var userId: String //유저가 입력한 아이디
    lateinit var userPassword: String //유저가 입력한 비밀번호

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService

    //onStart()할 때마다 입력칸 초기화 시키기 위해 전역변수 선언
    private lateinit var IdEditText: EditText //id EditText
    private lateinit var PasswordEditText: EditText //pw EditText
    private lateinit var PasswordCheckTextView: TextView

    private var idFlag: Boolean = false //id 감지 플래그
    private var pwFlag: Boolean = false //pw 감지 플래그
    private var flag: Boolean = false



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        IdEditText = binding.IdEditText
        PasswordEditText = binding.PasswordEditText
        PasswordCheckTextView = binding.PasswordCheckTextView

        val LoginButton = binding.LoginButton
        val SignUpButton = binding.SingupButton


        //email 변경 감지 되었을때
        val emailWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                idFlag = IdEditText.text.toString() != "" //emailEditText 비어 있지 않으면 true
            }
        }
        IdEditText.addTextChangedListener(emailWatcher) //email 변경 감지 이벤트 리스너

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
                LoginButton.isEnabled = idFlag && pwFlag
            }
        }
        IdEditText.addTextChangedListener(EmailPasswordWatcher)
        PasswordEditText.addTextChangedListener(EmailPasswordWatcher)
        //


        //로그인 버튼 눌렀을때 onClick
        LoginButton.setOnClickListener {
            userId = IdEditText.text.toString()
            userPassword = PasswordEditText.text.toString()
            Log.w("LoginPatientActivity", "userId: $userId, userPassword: $userPassword")

            App.prefs.token = null

            val userLoginInfo = UserLoginInfoRequest(userId, userPassword)
            retrofitClient = RetrofitClient.getInstance()
            apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)

            apiService.postPatientLogin(userLoginInfo).enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if(response.isSuccessful) {
                        val userToken = response.body().toString()
                        App.prefs.token = "Bearer "+userToken //로그인 시 받은 토큰 저장

                        val intent = Intent(this@LoginPatientActivity, MainActivity::class.java)

                        Log.d("LoginPatientActivity", "userId: ${userLoginInfo.id}, userToken: $userToken")
                        Log.d("Success Response", "userToken: ${userToken}, body: ${response.body().toString()}") //통신 성공한 경우

                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
                        startActivity(intent)
                        finish()

                    }
                    else {
                        //Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}")
                        val errorBody = response.errorBody()?.string()
                        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                        if (errorBody != null) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                val timestamp = jsonObject.optString("timestamp")
                                val status = jsonObject.optInt("status")
                                val error = jsonObject.optString("error")
                                val message = jsonObject.optString("message")
                                val path = jsonObject.optString("path")

                                Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                            } catch (e: JSONException) {
                                Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                            }
                        }
                    }
                }

                //통신 실패
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                }
            })
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