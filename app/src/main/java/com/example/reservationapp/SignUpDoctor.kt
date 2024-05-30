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
import com.example.reservationapp.Model.HospitalSignUpInfoRequest
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivitySignUpDoctorBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpDoctor : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpDoctorBinding

    private lateinit var registerIdEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerhospitalNameEditText: EditText

    private lateinit var idCheckTextView: TextView
    private lateinit var pwCheckTextView: TextView
    private lateinit var hospitalNameCheckTextView: TextView

    private var idFlag: Boolean = false //id 감지 플래그
    private var pwFlag: Boolean = false //pw 감지 플래그
    private var hospitalNameFlag: Boolean = false //이름 감지 플래그


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService


    //화면을 터치했을 때
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            registerIdEditText.clearFocus()
            registerPasswordEditText.clearFocus()
            registerhospitalNameEditText.clearFocus()
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
        binding = ActivitySignUpDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // EditText 참조 가져오기
        registerIdEditText = binding.registerId
        registerPasswordEditText = binding.registerPassword
        registerhospitalNameEditText = binding.registerName
        pwCheckTextView = binding.pwCheckTextView

        idCheckTextView = binding.idCheckTextView
        pwCheckTextView = binding.pwCheckTextView
        hospitalNameCheckTextView = binding.nameCheckTextView


        // 회원가입 버튼 클릭 리스너 등록
        val registerButton = binding.registerBtn
        registerButton.setOnClickListener { attemptSignUp() }


        //id 변경 감지
        val idWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(registerIdEditText.text.toString() != "") { //아이디 입력했을때
                    idCheckTextView.text = ""
                    idCheckTextView.visibility = View.GONE
                    idFlag = true
                } else {
                    idCheckTextView.text = "아이디를 입력하시오."
                    idCheckTextView.visibility = View.VISIBLE
                    idFlag = false
                }
            }
        }
        registerIdEditText.addTextChangedListener(idWatcher) //id 변경 감지 이벤트 리스너

        //비밀번호 변경 감지
        val pwWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(registerPasswordEditText.text.toString().length >= 8) { //비밀번호 8자리 이상
                    pwCheckTextView.text = ""
                    pwCheckTextView.visibility = View.GONE
                    pwFlag = true
                } else { //비밀번호 8자리 미만
                    pwCheckTextView.text = "비밀번호 8자리 이상 입력하시오."
                    pwCheckTextView.visibility = View.VISIBLE
                    pwFlag = false
                }
            }
        }
        registerPasswordEditText.addTextChangedListener(pwWatcher) //pw 변경 감지 이벤트 리스너

        //이름 변경 감지
        val nameWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(registerhospitalNameEditText.text.toString() != "") { //이름 입력 했을때
                    hospitalNameCheckTextView.text = ""
                    hospitalNameCheckTextView.visibility = View.GONE
                    hospitalNameFlag = true
                } else { //비밀번호 8자리 미만
                    hospitalNameCheckTextView.text = "이름을 입력하시오."
                    hospitalNameCheckTextView.visibility = View.VISIBLE
                    hospitalNameFlag = false
                }
            }
        }
        registerhospitalNameEditText.addTextChangedListener(nameWatcher)

        //셋 다 감지 되었을때 버튼 활성화
        val idPwNameWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                registerButton.isEnabled = idFlag && pwFlag && hospitalNameFlag
                if(!registerButton.isEnabled) { //false
                    registerButton.setBackgroundResource(R.drawable.style_gray_radius_20)
                } else {
                    registerButton.setBackgroundResource(R.drawable.style_dark_green_radius_20_pressed_button)
                }
            }
        }
        registerIdEditText.addTextChangedListener(idPwNameWatcher)
        registerPasswordEditText.addTextChangedListener(idPwNameWatcher)
        registerhospitalNameEditText.addTextChangedListener(idPwNameWatcher)
    }

    //회원가입 처리 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun attemptSignUp() {
        // EditText의 값 가져오기
        val businessNumber = registerIdEditText.text.toString()
        val password = registerPasswordEditText.text.toString()
        val hospitalName = registerhospitalNameEditText.text.toString()

        // 각 필드가 비어 있는지 확인
        if (businessNumber.isEmpty() || password.isEmpty() || hospitalName.isEmpty()) {
            // 에러 메시지를 Snackbar를 통해 표시
            Snackbar.make(registerIdEditText, "모든 항목을 입력해주세요", Snackbar.LENGTH_SHORT).show()
            return
        }

        // 비밀번호가 8자리 이상인지 확인
        if (password.length < 8) {
            // 비밀번호가 8자리 미만이므로 경고 메시지 표시
            pwCheckTextView.text = "비밀번호는 8자리 이상이어야 합니다"
            pwCheckTextView.visibility = View.VISIBLE
            return
        } else {
            // 비밀번호가 조건을 충족하면 경고 메시지를 숨기고 회원가입 진행
            pwCheckTextView.visibility = View.GONE
        }


        val userSignUpInfo = HospitalSignUpInfoRequest(businessNumber, password, hospitalName) //, addNum)
        lateinit var responseBody: HospitalSignupInfoResponse

        App.prefs.token = null
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)

        apiService.postHospitalSignUp(userSignUpInfo).enqueue(object: Callback<HospitalSignupInfoResponse> {
            override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                if(response.isSuccessful) {
                    responseBody = response.body()!!
                    Log.d("Success Response", responseBody.toString()) //통신 성공한 경우
                    CustomToast(this@SignUpDoctor, "회원가입 완료되었습니다.")

                    //회원가입 성공시 병원 로그인 액티비티로 이동
                    val intent = Intent(this@SignUpDoctor, LoginDoctorActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                    Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
            }

            override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
            }
        })
    }


    //
    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        registerIdEditText.clearFocus()
        registerPasswordEditText.clearFocus()
        registerhospitalNameEditText.clearFocus()
        currentFocus?.clearFocus()
        finish() //현재 액티비티 종료
    }

}
