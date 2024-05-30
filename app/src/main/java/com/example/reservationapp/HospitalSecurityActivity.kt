package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.ChatBotResponse
import com.example.reservationapp.Model.ChatItem
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

        binding.securityButton.setOnClickListener {
            val addnum = binding.editaddress.text.toString()
            App.apiService.postSetOpenApi(addnum = addnum)

            App.apiService.postSetOpenApi(addnum = addnum).enqueue(object :
                Callback<SetOpenApiResponse> {
                override fun onResponse(call: Call<SetOpenApiResponse>, response: Response<SetOpenApiResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()!!
                        Log.d("Success Response", responseBody.toString())

                    } else {
                        Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                    }
                }
                override fun onFailure(call: Call<SetOpenApiResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                }
            })
            onBackToHospitalClicked()
        }
    }

    private fun onBackToHospitalClicked() {
        val intent = Intent(this, HospitalMainActivity::class.java)
        intent.putExtra("show_fragment", "hospital")
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}