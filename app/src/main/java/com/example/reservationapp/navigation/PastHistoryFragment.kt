package com.example.reservationapp.navigation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.PastHistoryAdapter
import com.example.reservationapp.MainActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.Model.UserReservationResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.FragmentPastHistoryBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



//과거 진료내역 프래그먼트
class PastHistoryFragment : Fragment() {
    private lateinit var binding: FragmentPastHistoryBinding

    private lateinit var adapter: PastHistoryAdapter
    private lateinit var pastHistoryList: ArrayList<HistoryItem>

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBody: List<UserReservationResponse>




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPastHistoryBinding.inflate(inflater)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.tokenCheck()

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)

        //진료내역 recyclerView
        adapter = PastHistoryAdapter()
        val recyclerView = binding.pastHistoryRecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager


        //과거 진료내역 데이터 넣기 (DB에서 불러오기)
        apiService.getUserReservation().enqueue(object: Callback<List<UserReservationResponse>> {
            override fun onResponse(call: Call<List<UserReservationResponse>>, response: Response<List<UserReservationResponse>>) {
                if(response.isSuccessful) {
                    responseBody = response.body()!!

                    //예약한 내역이 있으면
                    if(responseBody != null) {
                        pastHistoryList = ArrayList()
                        for(reservation in responseBody) {
                            //진행 단계 : 예약신청, 에약확정, 예약취소, 진료완료
                            if(reservation.status == "진료완료") {
                                Log.w("PastHistoryFragmetn", "$reservation")
                                pastHistoryList.add(HistoryItem(reservation.reservationId, reservation.hospitalId, reservation.status, reservation.hospitalName, reservation.className, reservation.reservationDate.toString(), reservation.reservationTime.toString(), reservation.reviewWriteBoolean))
                            }
                        }
                        adapter.updatelist(pastHistoryList)
                    }
                }

                else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("FAILURE Response", "UserReservation Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
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

            override fun onFailure(call: Call<List<UserReservationResponse>>, t: Throwable) {

            }
        })

        return binding.root
    }
}