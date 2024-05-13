package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.UserSignUpInfoRequest
import com.example.reservationapp.Retrofit.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpDoctor : AppCompatActivity() {
    private lateinit var registerId: EditText
    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerName: EditText
    private lateinit var registerPhone: EditText
    private lateinit var passwordWarning: TextView // 비밀번호 경고문을 표시할 TextView

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService



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

    //회원가입 처리 함수
    private fun attemptSignUp() {
        // EditText의 값 가져오기
        val businessNumber = registerId.text.toString()
        val password = registerPassword.text.toString()
        val hospitalName = registerName.text.toString()

        val email = registerEmail.text.toString()
        val phone = registerPhone.text.toString()

        // 각 필드가 비어 있는지 확인
        if (businessNumber.isEmpty() || email.isEmpty() || password.isEmpty() || hospitalName.isEmpty() || phone.isEmpty()) {
            // 에러 메시지를 Snackbar를 통해 표시
            Snackbar.make(registerId, "모든 항목을 입력해주세요", Snackbar.LENGTH_SHORT).show()
            return
        }

        // 비밀번호가 8자리 이상인지 확인
        if (password.length < 8) {
            // 비밀번호가 8자리 미만이므로 경고 메시지 표시
            passwordWarning.text = "비밀번호는 8자리 이상이어야 합니다"
            passwordWarning.visibility = View.VISIBLE
            return
        } else {
            // 비밀번호가 조건을 충족하면 경고 메시지를 숨기고 회원가입 진행
            passwordWarning.visibility = View.GONE
        }


        val userSignUpInfo = UserSignUpInfoRequest(businessNumber, password, hospitalName)
        lateinit var responseBody: HospitalSignupInfoResponse

        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)
        apiService.postHospitalSignUp(userSignUpInfo).enqueue(object: Callback<HospitalSignupInfoResponse> {
            override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                if(response.isSuccessful()) {
                    responseBody = response.body()!!
                    Log.d("Success Response", responseBody.toString()) //통신 성공한 경우

                    //회원가입 성공시 병원 로그인 액티비티로 이동
                    val intent = Intent(this@SignUpDoctor, LoginDoctorActivity::class.java)
                    intent.putExtra("responseData", responseBody)
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
}
