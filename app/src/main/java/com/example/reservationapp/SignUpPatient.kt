package com.example.reservationapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.databinding.ActivitySignUpPatientBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class SignUpPatient : AppCompatActivity() {
    private lateinit var sContext: Context
    private lateinit var binding: ActivitySignUpPatientBinding
    private lateinit var birthdateYear: String
    private lateinit var birthdateMonth: String
    private lateinit var birthdateDay: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        sContext = this
        val signUpButton = binding.btnRegister
        val birthdateYearSpinner = binding.birthdateYear
        val birthdateMonthSpinner = binding.birthdateMonth
        val birthdateDaySpinner = binding.birthdateDay

        // 생년월일 년도 스피너 설정
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (1900..currentYear).toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)
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
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)
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
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)
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
            val registerId = binding.registerId.text.toString()
            val registerEmail = binding.registerEmail.text.toString()
            val registerPassword = binding.registerPassword.text.toString()
            val registerName = binding.registerName.text.toString()
            val registerPhone = binding.registerPhone.text.toString()

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

            // 비밀번호가 7자리 이상인지 확인
            if (registerPassword.length < 7) {
                // 비밀번호가 7자리 미만이므로 경고 메시지 표시
                binding.passwordWarning.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                // 비밀번호가 7자리 이상이면 경고 메시지 숨기기
                binding.passwordWarning.visibility = View.GONE
            }

            // 모든 필드가 입력되었을 때 회원가입 성공 처리 수행
            Log.w("Birthdate : ", "$birthdateYear-$birthdateMonth-$birthdateDay")

            // 회원가입 성공 시 메인 화면으로 이동
            val intent = Intent(this@SignUpPatient, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료

        }
    }
}
