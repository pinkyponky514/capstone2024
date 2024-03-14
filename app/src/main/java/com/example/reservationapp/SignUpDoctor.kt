package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class SignUpDoctor : AppCompatActivity() {
    private lateinit var registerId: EditText
    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerName: EditText
    private lateinit var registerPhone: EditText
    private lateinit var passwordWarning: TextView // 비밀번호 경고문을 표시할 TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_doctor)

        // EditText 참조 가져오기
        registerId = findViewById(R.id.register_id)
        registerEmail = findViewById(R.id.register_email)
        registerPassword = findViewById(R.id.register_password)
        registerName = findViewById(R.id.register_name)
        registerPhone = findViewById(R.id.register_phone)
        passwordWarning = findViewById(R.id.password_warning) // 비밀번호 경고문 TextView 참조


        // 회원가입 버튼 클릭 리스너 등록
        val registerButton: Button = findViewById(R.id.btn_register)
        registerButton.setOnClickListener { attemptSignUp() }
    }

    private fun attemptSignUp() {
        // EditText의 값 가져오기
        val id = registerId.text.toString()
        val email = registerEmail.text.toString()
        val password = registerPassword.text.toString()
        val name = registerName.text.toString()
        val phone = registerPhone.text.toString()

        // 각 필드가 비어 있는지 확인
        if (id.isEmpty() || email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            // 에러 메시지를 Snackbar를 통해 표시
            Snackbar.make(registerId, "모든 항목을 입력해주세요", Snackbar.LENGTH_SHORT).show()
            return
        }

        // 비밀번호가 7자리 이상인지 확인
        if (password.length < 7) {
            // 비밀번호가 7자리 미만이므로 경고 메시지 표시
            passwordWarning.text = "비밀번호는 7자리 이상이어야 합니다"
            passwordWarning.visibility = View.VISIBLE
            return
        }

        // 비밀번호가 조건을 충족하면 경고 메시지를 숨기고 회원가입 진행
        passwordWarning.visibility = View.GONE

        // 회원가입 성공 시 메인 화면으로 이동
        val intent = Intent(this@SignUpDoctor, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}
