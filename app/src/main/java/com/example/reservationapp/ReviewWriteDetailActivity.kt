package com.example.reservationapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReviewRequest
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityReviewWriteDetailBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ReviewWriteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewWriteDetailBinding

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBodyHospitalDetail: HospitalSignupInfoResponse


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewWriteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //초기화
        val hospital_name_textView = binding.hospitalName
        val class_name_textView = binding.className
        val reserve_date_textView = binding.reserveDate


        val rating_bar = binding.reviewWriteDetailRatingBar
        val review_content_editText = binding.reviewContentEditText

        val cancel_button = binding.cancelButton
        val register_button = binding.registerButton

        //전달받은 값
        val historyItem = intent.getSerializableExtra("historyItem") as HistoryItem
        Log.w("ReviewWriteDetailActivity", "historyItem: $historyItem")

        val hospitalId = historyItem.hospitalId //intent.getLongExtra("hospitalId", 0)

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


        //예약정보 표시
        apiService.getHospitalDetail(hospitalId).enqueue(object: Callback<HospitalSignupInfoResponse> {
            override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                if(response.isSuccessful) {
                    responseBodyHospitalDetail = response.body()!!
                    hospital_name_textView.text = responseBodyHospitalDetail.data.name
                    class_name_textView.text = historyItem.className

                    val dateSplit = historyItem.reserveDay.split("-")
                    val dayOfWeek = getDayOfWeek(dateSplit)
                    reserve_date_textView.text = "${dateSplit[0]}.${dateSplit[1]}.${dateSplit[2]} ($dayOfWeek) ${historyItem.reserveTime}"

                    Log.w("ReviewWriteDetailActivity", "hospitalName: ${hospital_name_textView.text}, className: ${class_name_textView.text}")
                }
                else Log.w("ReviewWriteDetailActivity", "getHospitalDetail Connect SUCCESS, Response FAILURE - response.body(): ${response.body()}")
            }

            override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
                Log.w("ReviewWriteDetailActivity CONNECTION FAILURE: ", "Review Connect FAILURE : ${t.localizedMessage}")
            }
        })


        //등록버튼 눌렀을때 onClick
        register_button.setOnClickListener {
            val rating_count = rating_bar.rating //별점
            val review_content = review_content_editText.text.toString() //리뷰내용

            val review = ReviewRequest(rating_count, review_content, historyItem.reservationId)
            apiService.postReviewWrite(hospitalId, review).enqueue(object: Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        Log.w("ReviewWrtieDetailActivity", "response.body() : ${response.body()}")
                        val intent = Intent(this@ReviewWriteDetailActivity, ReviewWrtieCompletedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    //통신 성공, 응답 실패
                    else {
                        //Log.w("ReviewWriteDetailActivity", "Review Connect SUCCESS, Response FAILURE - response.body(): ${response.body()}")
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

                //통신실패
                override fun onFailure(call: Call<Long>, t: Throwable) {
                    Log.w("ReviewWriteDetailActivity CONNECTION FAILURE: ", "Review Connect FAILURE : ${t.localizedMessage}")
                }
            })
        }

        //취소버튼 눌렀을때 onClick
        cancel_button.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        //
    }

    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // 현재 액티비티 종료
    }

    //
    //년, 월, 일 해당하는 날짜의 요일 구하기
    private fun getDayOfWeek(dateSplit: List<String>): String {
        val year = dateSplit[0].toInt()
        val month = dateSplit[1].toInt()-1
        val day = dateSplit[2].toInt()
        Log.w("PastHistoryAdapter", "dateSplit year: $year, month: ${month}, day: $day")

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }
    }


    //
}