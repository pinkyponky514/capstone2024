package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.Model.ChatBotResponse
import com.example.reservationapp.Model.ChatItem
import com.example.reservationapp.Model.HospitalSearchResponse
import com.example.reservationapp.Model.SetOpenApiResponse
import com.example.reservationapp.databinding.ActivityHospitalSecurityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalSecurityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalSecurityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hospitalId = intent.getLongExtra("hospitalId",0)
        binding.edithospital.setText(App.hospitalName)
        App.apiService.getHospitalDetail(hospitalId = hospitalId).enqueue(object :
            Callback<HospitalSearchResponse> {
            @SuppressLint("ResourceType")
            override fun onResponse(call: Call<HospitalSearchResponse>, response: Response<HospitalSearchResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if(responseBody.data.hospital.openApiHospital != null){

                        binding.textView5.setText("이미 인증 완료된 병원입니다.")
                        binding.securityButton.isEnabled = false

                        val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_button_background5)
                        binding.securityButton.background = drawable

                    }
                } else {

                    Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                }
            }
            override fun onFailure(call: Call<HospitalSearchResponse>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
        binding.securityButton.setOnClickListener {
            val addnum = binding.editaddress.text.toString()

            App.apiService.postSetOpenApi(addnum = addnum).enqueue(object :
                Callback<SetOpenApiResponse> {
                override fun onResponse(call: Call<SetOpenApiResponse>, response: Response<SetOpenApiResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()!!
                        Log.d("Success Response", responseBody.toString())
                        CustomToast(this@HospitalSecurityActivity, "병원 인증에 성공하셨습니다.").show()
                        onBackToHospitalClicked()
                    } else {
                        CustomToast(this@HospitalSecurityActivity, "올바른 우편번호로 입력해주세요.").show()
                        Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                    }
                }
                override fun onFailure(call: Call<SetOpenApiResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                }
            })

        }
    }

    private fun onBackToHospitalClicked() {
        val intent = Intent(this, HospitalMainActivity::class.java)
        intent.putExtra("show_fragment", "hospital")
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}