package com.example.reservationapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Model.ReviewRequest
import com.example.reservationapp.Model.filterList
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityReviewWriteDetailBinding
import com.example.reservationapp.navigation.pastHistoryList
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
        val hospitalId = intent.getLongExtra("hospitalId", 0)


        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


        //예약정보 표시
        apiService.getHospitalDetail(hospitalId).enqueue(object: Callback<HospitalSignupInfoResponse> {
            override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                if(response.isSuccessful) {
                    responseBodyHospitalDetail = response.body()!!
                    hospital_name_textView.text = responseBodyHospitalDetail.data.name
                    class_name_textView.text = responseBodyHospitalDetail.data.hospitalDetail.department
                    Log.w("ReviewWriteDetailActivity", "hospitalName: ${hospital_name_textView.text}, className: ${class_name_textView.text}")
                }
                else Log.w("ReviewWriteDetailActivity", "getHospitalDetail Connect SUCCESS, Response FAILURE - response.body(): ${response.body()}")
            }

            override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
                Log.w("ReviewWriteDetailActivity CONNECTION FAILURE: ", "Review Connect FAILURE : ${t.localizedMessage}")
            }
        })


        //등록버튼 눌렀을때 onClick
/*
        register_button.setOnClickListener {
            for(i in filterList.indices) {
                if(filterList[i].hospitalName == hospital_name_string) { //넘겨받은 병원이름을 DB 병원 리스트에서 검색, 같으면 if문 수행하고 중단
                    val rating_count = rating_bar.rating //별점
                    val review_content = review_content_editText.text.toString() //리뷰내용

                    val calendar = Calendar.getInstance()
                    val currentYear = calendar.get(Calendar.YEAR)
                    val currentMonth = calendar.get(Calendar.MONTH)+1
                    val currentDay = calendar.get(Calendar.DATE)
                    Log.w("ReviewWrtieDetailActivity", "year: $currentYear, month: $currentMonth, day: $currentDay")
                    //val dayOfWeek = getDayOfWeek(currentYear, currentMonth, currentDay) //현재 요일 구하기

                    val date = "$currentYear.$currentMonth.$currentDay"//리뷰 쓴 날짜
                    val username = "hansung" //리뷰 쓴 사용자 이름

                    val review = ReviewItem(rating_count.toString(), review_content, date, username)
                    Log.w("ReviewWrtieDetailActivity", "review: $review")

                    //DB에 리뷰 추가하기
                    Log.w("ReviewWriteDetailActivity", "리뷰추가:  ${filterList[i].reviewList}")
                    filterList[i].reviewList.add(review)
                    Log.w("ReviewWriteDetailActivity", "리뷰추가:  ${filterList[i].reviewList}")


                    //과거진료내역 리뷰쓰기버튼 리뷰작성완료로 변경
                    for(j in pastHistoryList.indices) {
                        if(pastHistoryList[j].hospitalName == hospital_name_string) {
                            pastHistoryList[j].reviewWriteBoolean = true

                            //등록하고 액티비티 전환
                            val intent = Intent(this, ReviewWrtieCompletedActivity::class.java)
                            startActivity(intent)
                            finish()

                            break
                        }
                    }

                    break
                }
            }
        }
*/

        register_button.setOnClickListener {
            val rating_count = rating_bar.rating //별점
            val review_content = review_content_editText.text.toString() //리뷰내용

            val review = ReviewRequest(rating_count, review_content)
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
}