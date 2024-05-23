package com.example.reservationapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.AbleReviewWriteAdapter
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.Model.UserReservationResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityMainBinding
import com.example.reservationapp.databinding.ActivityReviewAbleListBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//병원 상세페이지에서 리뷰 작성 버튼 눌렀을 경우, 나의 진료내역 불러와서 리뷰 작성 가능한 것만 보여주는 액티비티
class ReviewAbleListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewAbleListBinding

    private lateinit var ableReviewList: ArrayList<HistoryItem>
    private lateinit var notAbleReviewConstraintLayout: ConstraintLayout //작성 가능한 리뷰가 없을때 constraintLayout
    private lateinit var ableReviewConstraintLayout: ConstraintLayout //작성 가능한 리뷰가 있을때 constraintLayout

    private lateinit var adapter: AbleReviewWriteAdapter


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBody: List<UserReservationResponse>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewAbleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


        //recyclerview, adapter 초기화
        adapter = AbleReviewWriteAdapter()
        val recyclerView = binding.ableReviewWriteRecyclerview
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

        val hospitalId = intent.getLongExtra("hospitalId", 0)
        val hospitalName = intent.getStringExtra("hospitalName")

        ableReviewConstraintLayout = binding.ableReviewWriteConstraintLayout
        notAbleReviewConstraintLayout = binding.notAbleReviewWriteConstraintLayout
        ableReviewConstraintLayout.visibility = View.GONE
        notAbleReviewConstraintLayout.visibility = View.GONE

        apiService.getUserReservation().enqueue(object: Callback<List<UserReservationResponse>> {
            override fun onResponse(call: Call<List<UserReservationResponse>>, response: Response<List<UserReservationResponse>>) {
                //통신, 응답 성공
                if(response.isSuccessful) {
                    responseBody = response.body()!!

                    //예약한 병원이 있으면
                    if(responseBody != null) {
                        ableReviewList = ArrayList()
                        ableReviewConstraintLayout.visibility = View.VISIBLE
                        notAbleReviewConstraintLayout.visibility = View.GONE

                        for(reservation in responseBody) {
                            if(reservation.hospitalId == hospitalId && !reservation.reviewWriteBoolean && reservation.status == "진료완료") { //진료완료 되고, 작성 안되어 있는 것만 리뷰 작성할 수 있게
                                ableReviewList.add(HistoryItem(reservation.reservationId, reservation.hospitalId, reservation.status, reservation.hospitalName, reservation.className, reservation.reservationDate.toString(), reservation.reservationTime.toString(), reservation.reviewWriteBoolean))
                            }
                        }

                        if(ableReviewList == null || ableReviewList.isEmpty()) { //예약한 병원은 있지만, 해당 병원이 아닐 경우
                            ableReviewConstraintLayout.visibility = View.GONE
                            notAbleReviewConstraintLayout.visibility = View.VISIBLE
                        } else adapter.updatelist(ableReviewList)
                    }

                    //예약한 병원이 없으면
                    else {
                        ableReviewConstraintLayout.visibility = View.GONE
                        notAbleReviewConstraintLayout.visibility = View.VISIBLE
                    }
                }

                //통신 성공, 응답 실패
                else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("ReviewAbleListActivity FAILURE Response", "Able Review Write Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
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

            //통신 실팰
            override fun onFailure(call: Call<List<UserReservationResponse>>, t: Throwable) {
            }
        })
    }
}