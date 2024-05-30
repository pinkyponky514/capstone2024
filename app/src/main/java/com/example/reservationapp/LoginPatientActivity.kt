package com.example.reservationapp

import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.UserLoginInfoRequest
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.Retrofit.RetrofitClient
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
    private lateinit var IdCheckTextview: TextView

    private var idFlag: Boolean = false //id 감지 플래그
    private var pwFlag: Boolean = false //pw 감지 플래그
    private var flag: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) { // 화면을 터치했을 때
            IdEditText.clearFocus()
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
        IdEditText = binding.IdEditText
        PasswordEditText = binding.PasswordEditText
        PasswordCheckTextView = binding.PasswordCheckTextView
        IdCheckTextview = binding.IdCheckTextView

        val LoginButton = binding.LoginButton
        val SignUpButton = binding.SingupButton


        //id 변경 감지 되었을때
        val idWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
/*
                if(!isValid(s.toString())) { //정규 표현식 만족하지 못하면
                    IdEditText.setText(s?.subSequence(0, s.length - 1))
                    IdEditText.setSelection(IdEditText.getText().length) // 커서 위치를 맨 끝으로 이동
                    idFlag = false
                } else {
                    idFlag = true
                }
*/
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //idFlag = IdEditText.text.toString() != "" //idEditText 비어 있지 않으면 true
                if(IdEditText.text.toString() != "") { //아이디 입력했을때
                    IdCheckTextview.text = "" //경고문구 표시
                    IdCheckTextview.visibility = View.GONE
                    idFlag = true
                } else { //아이디 8자리 미만인 경우
                    IdCheckTextview.text = "아이디를 입력하시오."
                    IdCheckTextview.visibility = View.VISIBLE
                    idFlag = false
                }
            }
        }
        IdEditText.addTextChangedListener(idWatcher) //id 변경 감지 이벤트 리스너

        //비밀번호 변경 감지 되었을때
        val pwWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (PasswordEditText.text.toString().length >= 8) { //비밀번호 8자리 이상인 경우
                    PasswordCheckTextView.text = "" //경고문구 표시
                    pwFlag = true
                } else { //비밀번호 8자리 미만인 경우
                    PasswordCheckTextView.text = "비밀번호 8자리 이상 입력하시오."
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
                if(!LoginButton.isEnabled) { //false이면 배경색 gray로
                    LoginButton.setBackgroundResource(R.drawable.style_gray_radius_20)
                } else {
                    LoginButton.setBackgroundResource(R.drawable.style_dark_green_radius_20_pressed_button)
                }
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

            val userLoginInfo = UserLoginInfoRequest(userId, userPassword)

            App.prefs.token = null
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
                        CustomToast(this@LoginPatientActivity, "로그인 하였습니다.")

                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
                        startActivity(intent)
                        finish()
                    }
                    else {
                        //CustomToast(this@LoginPatientActivity, "일치하는 유저의 계정이 없습니다.").show()
                        handleErrorResponse(response)
                    }
                }

                //통신 실패
                override fun onFailure(call: Call<String>, t: Throwable) {
                    CustomToast(this@LoginPatientActivity, "일치하는 유저의 계정이 없습니다.").show()
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

    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        IdEditText.clearFocus()
        PasswordEditText.clearFocus()
        currentFocus?.clearFocus()
        finish() //현재 액티비티 종료
    }

/*
    //입력이 올바른지 확인
    private fun isValid(input: String): Boolean {
        //정규 표현식을 사용하여 영어와 숫자만, 대문자는 허용되지 않음
        val pattern = "^[a-zA-Z0-9]*$"
        return Pattern.matches(pattern, input)
    }
*/
    //
}