package com.example.reservationapp

import android.R
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.PatientSignUpInfoRequest
import com.example.reservationapp.Model.PatientSignupInfoResponse
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivitySignUpPatientBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.*


class SignUpPatient : AppCompatActivity() {

    private lateinit var sContext: Context
    private lateinit var binding: ActivitySignUpPatientBinding

    private lateinit var idEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText

    private lateinit var idCheckTextView: TextView
    private lateinit var pwCheckTextView: TextView
    private lateinit var nameCheckTextView: TextView

    private lateinit var birthdateYear: String
    private lateinit var birthdateMonth: String
    private lateinit var birthdateDay: String

    private var idFlag: Boolean = false //id 감지 플래그
    private var pwFlag: Boolean = false //pw 감지 플래그
    private var nameFlag: Boolean = false //이름 감지 플래그



    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService


    //화면을 터치했을 때
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            idEditText.clearFocus()
            passwordEditText.clearFocus()
            nameEditText.clearFocus()
            currentFocus?.clearFocus()
            hideKeyboard()
        }
        return super.onTouchEvent(event)
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    //
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 초기화
        sContext = this
        idEditText = binding.registerId
        passwordEditText = binding.registerPassword
        nameEditText = binding.registerName

        idCheckTextView = binding.idCheckTextView
        pwCheckTextView = binding.pwCheckTextView
        nameCheckTextView = binding.nameCheckTextView

        val birthdateYearSpinner = binding.birthdateYear
        val birthdateMonthSpinner = binding.birthdateMonth
        val birthdateDaySpinner = binding.birthdateDay

        val signUpButton = binding.registerBtn


        //id 변경 감지
        val idWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(idEditText.text.toString() != "") { //아이디 입력했을때
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
        idEditText.addTextChangedListener(idWatcher) //id 변경 감지 이벤트 리스너

        //비밀번호 변경 감지
        val pwWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(passwordEditText.text.toString().length >= 8) { //비밀번호 8자리 이상
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
        passwordEditText.addTextChangedListener(pwWatcher) //pw 변경 감지 이벤트 리스너

        //이름 변경 감지
        val nameWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(nameEditText.text.toString() != "") { //이름 입력 했을때
                    nameCheckTextView.text = ""
                    nameCheckTextView.visibility = View.GONE
                    nameFlag = true
                } else { //비밀번호 8자리 미만
                    nameCheckTextView.text = "이름을 입력하시오."
                    nameCheckTextView.visibility = View.VISIBLE
                    nameFlag = false
                }
            }
        }
        nameEditText.addTextChangedListener(nameWatcher)

        //셋 다 감지 되었을때 버튼 활성화
        val idPwNameWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            //변경된 순간
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                signUpButton.isEnabled = idFlag && pwFlag && nameFlag
                if(!signUpButton.isEnabled) { //false
                    signUpButton.setBackgroundResource(com.example.reservationapp.R.drawable.style_gray_radius_20)
                } else {
                    signUpButton.setBackgroundResource(com.example.reservationapp.R.drawable.style_dark_green_radius_20_pressed_button)
                }
            }
        }
        idEditText.addTextChangedListener(idPwNameWatcher)
        passwordEditText.addTextChangedListener(idPwNameWatcher)
        nameEditText.addTextChangedListener(idPwNameWatcher)


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
        //birthdateYearSpinner.setSelection(0, false)

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
            val registerPassword = passwordEditText.text.toString()
            val registerName = nameEditText.text.toString()

            // 생년월일을 LocalDate로 변환
            val registerBirthday = LocalDate.of(birthdateYear.toInt(), birthdateMonth.toInt(), birthdateDay.toInt()) //2024-05-12

            // 각 필드가 비어 있는지 확인
            if (registerId.isEmpty() || registerPassword.isEmpty() || registerName.isEmpty()) {
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
                binding.pwCheckTextView.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                // 비밀번호가 8자리 이상이면 경고 메시지 숨기기
                binding.pwCheckTextView.visibility = View.GONE
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
                        CustomToast(this@SignUpPatient, "회원가입 완료되었습니다.")

                        //회원가입 성공시 메인 환자 로그인 액티비티로 이동
                        val intent = Intent(this@SignUpPatient, LoginPatientActivity::class.java)
                        startActivity(intent)
                        finish() //현재 액티비티 종료
                    }

                    //통신 성공, 응답은 실패
                    else handleErrorResponse(response)
                }

                override fun onFailure(call: Call<PatientSignupInfoResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
                }
            })
        }
        //
    }

    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        finish() //현재 액티비티 종료
    }

    //
}