package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.Model.HospitalSearchResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReservationResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityCheckReservationBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//예약 확인 액티비티
class CheckReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckReservationBinding

    private lateinit var hospitalNameTextView: TextView
    private lateinit var classNameTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var reservationDateTextView: TextView
    private lateinit var reservationTimeTextView: TextView

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBody: HospitalSearchResponse


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


        //초기화
        hospitalNameTextView = binding.textViewHospitalName
        classNameTextView = binding.textViewClassName
        statusTextView = binding.textViewReservationStatus
        reservationDateTextView = binding.textViewReservationDate
        reservationTimeTextView = binding.textViewReservationTime



        val intentDataItem = intent.getSerializableExtra("reservationResponse") as ReservationResponse

        apiService.getHospitalDetail(intentDataItem.hospitalId).enqueue(object: Callback<HospitalSearchResponse> {
            override fun onResponse(call: Call<HospitalSearchResponse>, response: Response<HospitalSearchResponse>) {
                //통신, 응답 성공
                if(response.isSuccessful) {
                    responseBody = response.body()!!
                    Log.d("CheckReservationActivity", "response.body(): $responseBody, intentDateItem: $intentDataItem")


                    for(i in responseBody.data.hospital.reservations.indices) {
                        //예약 날짜와 시간이 같으면
                        if(responseBody.data.hospital.reservations[i].reservationDate == intentDataItem.reservationDate && responseBody.data.hospital.reservations[i].reservationTime == intentDataItem.reservationTime) {
                            hospitalNameTextView.text = responseBody.data.hospital.name
                            classNameTextView.text = responseBody.data.hospital.hospitalDetail.department
                            statusTextView.text = responseBody.data.hospital.reservations[i].status
                            reservationDateTextView.text = responseBody.data.hospital.reservations[i].reservationDate.toString()
                            reservationTimeTextView.text = responseBody.data.hospital.reservations[i].reservationTime.toString()
                            break
                        }
                    }
                }

                //통신 성공, 응답 실패
                else {
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

            //통신 실패
            override fun onFailure(call: Call<HospitalSearchResponse>, t: Throwable) {
                Log.w("CheckReservationActivity CONNECTION FAILURE: ", "Connect FAILURE : ${t.localizedMessage}")
            }
        })
    }


    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) // 지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
        startActivity(intent) // 인텐트 이동
        finish() // 현재 액티비티 종료
    }

}