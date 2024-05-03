package com.example.reservationapp

import RetrofitClient
import android.content.Intent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.UserLoginInfo
import com.example.reservationapp.Model.UserSignUpInfo
import com.example.reservationapp.databinding.ActivityLoginPatientBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//로그인 화면
class LoginPatientActivity: AppCompatActivity() {
    lateinit var lContext: Context
    private lateinit var binding: ActivityLoginPatientBinding

    lateinit var userId: String //유저가 입력한 아이디
    lateinit var userPassword: String //유저가 입력한 비밀번호

    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService

    //onStart()할 때마다 입력칸 초기화 시키기 위해 전역변수 선언
    private lateinit var IdEditText: EditText //id EditText
    private lateinit var PasswordEditText: EditText //pw EditText
    private lateinit var PasswordCheckTextView: TextView

    private var idFlag: Boolean = false //id 감지 플래그
    private var pwFlag: Boolean = false //pw 감지 플래그
    private var flag: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        lContext = this
        IdEditText = binding.EmailEditText
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
        IdEditText.addTextChangedListener(EmailPasswordWatcher)
        PasswordEditText.addTextChangedListener(EmailPasswordWatcher)
        //


        //로그인 버튼 눌렀을때
        LoginButton.setOnClickListener {
            userId = IdEditText.text.toString()
            userPassword = PasswordEditText.text.toString()
            Log.w("userId, userPassword", ": $userId" + ", $userPassword")

            val userLoginInfo = UserLoginInfo(userId, userPassword)

            retrofitClient = RetrofitClient().getInstance()
            apiService = RetrofitClient().getRetrofitInterface()!!


            apiService.postLogin(userLoginInfo).enqueue(object: Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if(response.isSuccessful())
                            Log.d("Success Response", response.body().toString()) //통신 성공한 경우
                        else
                            Log.d("RESPONSE", "FAILURE") //통신 성공, 응답은 실패
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
                    }

            })

            val intent = Intent(this, MainActivity::class.java)
            //intent.putExtra("userId", userLoginInfo.id)
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
