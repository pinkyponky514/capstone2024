package com.example.reservationapp

import android.R
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.ChatBotResponse
import com.example.reservationapp.Model.PatientSignUpInfoRequest
import com.example.reservationapp.Model.PatientSignupInfoResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivitySignUpPatientBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class SignUpPatient : AppCompatActivity() {

    private lateinit var sContext: Context
    private lateinit var binding: ActivitySignUpPatientBinding

    private lateinit var birthdateYear: String
    private lateinit var birthdateMonth: String
    private lateinit var birthdateDay: String

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 초기화
        sContext = this
        val idEditText = binding.registerId
        val passwordEditText = binding.registerPassword
        val nameEditText = binding.registerName

        val signUpButton = binding.btnRegister

        val birthdateYearSpinner = binding.birthdateYear
        val birthdateMonthSpinner = binding.birthdateMonth
        val birthdateDaySpinner = binding.birthdateDay

        // 생년월일 년도 스피너 설정
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (1900..currentYear).toList()
        val yearAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, years)
        birthdateYearSpinner.adapter = yearAdapter

        birthdateYearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.w("Year : ", "년도를 입력해주세요")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.w("Year : ", years[position].toString())
                birthdateYear = years[position].toString()
            }
        }

        // 생년월일 월 스피너 설정
        val months = (1..12).toList()
        val monthAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, months)
        birthdateMonthSpinner.adapter = monthAdapter

        birthdateMonthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.w("Month : ", "월을 입력해주세요")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.w("Month : ", months[position].toString())
                birthdateMonth = months[position].toString()
            }
        }

        // 생년월일 일 스피너 설정 (단순히 1부터 31까지의 숫자로 설정)
        val days = (1..31).toList()
        val dayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, days)
        birthdateDaySpinner.adapter = dayAdapter

        birthdateDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.w("Day : ", "일을 입력해주세요")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.w("Day : ", days[position].toString())
                birthdateDay = days[position].toString()
            }
        }

        // 회원가입 버튼 눌렀을때
        signUpButton.setOnClickListener {
            // EditText의 값 가져오기
            val registerId = idEditText.text.toString()
            val registerEmail = binding.registerEmail.text.toString()
            val registerPassword = passwordEditText.text.toString()
            val registerName = nameEditText.text.toString()
            val registerPhone = binding.registerPhone.text.toString()
            //val registerBirthday = "$birthdateYear-$birthdateMonth-$birthdateDay"
            //val registerBirthday = LocalDate.of(birthdateYear.toInt(), birthdateDay.toInt(), birthdateDay.toInt())
            //Log.w("SignUpPatient", "Birthday: $registerBirthday")

            // 생년월일을 LocalDate로 변환
            val registerBirthday: LocalDate = LocalDate.of(birthdateYear.toInt(), birthdateMonth.toInt(), birthdateDay.toInt()) //2024-05-12

            // 각 필드가 비어 있는지 확인
            if (registerId.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty() || registerName.isEmpty() || registerPhone.isEmpty()) {
                // 에러 메시지를 Snackbar를 통해 표시
                Snackbar.make(binding.root, "모든 항목을 입력해주세요", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 생년월일이 선택되었는지 확인
            if (::birthdateYear.isInitialized && ::birthdateMonth.isInitialized && ::birthdateDay.isInitialized) {
                if (birthdateYear.isEmpty() || birthdateMonth.isEmpty() || birthdateDay.isEmpty()) {
                    // 에러 메시지를 Snackbar를 통해 표시
                    Snackbar.make(binding.root, "생년월일을 선택해주세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // 비밀번호가 8자리 이상인지 확인
            if (registerPassword.length < 8) {
                // 비밀번호가 8자리 미만이므로 경고 메시지 표시
                binding.passwordWarning.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                // 비밀번호가 8자리 이상이면 경고 메시지 숨기기
                binding.passwordWarning.visibility = View.GONE
            }


            //Retrofit, 모든 필드가 입력되었을 때 회원가입 성공 처리 수행
            val userSignupInfo = PatientSignUpInfoRequest(registerId, registerPassword, registerName, registerBirthday.toString())

            App.prefs.token = null

            lateinit var responseBody: PatientSignupInfoResponse
            retrofitClient = RetrofitClient.getInstance()
            apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)

            apiService.postPatientSignUp(userSignupInfo).enqueue(object: Callback<PatientSignupInfoResponse> {
                override fun onResponse(call: Call<PatientSignupInfoResponse>, response: Response<PatientSignupInfoResponse>) {
                    //연결, 응답 성공
                    if(response.isSuccessful) {
                        responseBody = response.body()!!
                        Log.d("SingUpPatient Success Response", "response: $responseBody") //통신 성공한 경우

                        //회원가입 성공시 메인 환자 로그인 액티비티로 이동
                        val intent = Intent(this@SignUpPatient, LoginPatientActivity::class.java)
                        startActivity(intent)
                        finish() //현재 액티비티 종료
                    }

                    //통신 성공, 응답은 실패
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

                override fun onFailure(call: Call<PatientSignupInfoResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
                }
            })
        }
        //
    }


    private fun handleErrorResponse(response: Response<PatientSignupInfoResponse>) {
        val errorBody = response.errorBody()?.string()
        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: $errorBody")
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
    //
}