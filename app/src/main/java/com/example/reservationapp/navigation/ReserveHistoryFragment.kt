package com.example.reservationapp.navigation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.ReserveHistoryAdapter
import com.example.reservationapp.MainActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.Model.HospitalDetailResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.PopularHospitalItem
import com.example.reservationapp.Model.SearchHospital
import com.example.reservationapp.Model.UserReservationResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.FragmentReserveHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//현재 진행중, 예약 진료내역 프래그먼트
class ReserveHistoryFragment : Fragment() {
    private lateinit var binding: FragmentReserveHistoryBinding

    private lateinit var adapter: ReserveHistoryAdapter
    private lateinit var reserveHistoryList: ArrayList<HistoryItem>

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBodyUserReservation: List<UserReservationResponse>
    private lateinit var responseBodyHospitalDetail: HospitalSignupInfoResponse

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReserveHistoryBinding.inflate(inflater)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.tokenCheck()

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


        //예약 진료내역 adapter, recyclerView 초기화
        adapter = ReserveHistoryAdapter()
        val recyclerView = binding.reserveHistoryRecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        //예약 진료내역 데이터 넣기 (DB 데이터 가져와서 넣기)
        apiService.getUserReservation().enqueue(object: Callback<List<UserReservationResponse>> {
            override fun onResponse(call: Call<List<UserReservationResponse>>, response: Response<List<UserReservationResponse>>) {
                //통신, 응답 성공
                if(response.isSuccessful) {
                    responseBodyUserReservation = response.body()!!

                    if(responseBodyUserReservation != null) {
                        reserveHistoryList = ArrayList()
                        for(reservation in responseBodyUserReservation) {
                            //진행 단계 : 예약신청, 에약확정, 예약취소, 진료완료
                            if(reservation.status == "예약신청" || reservation.status == "예약확정" || reservation.status == "예약취소") {
                                reserveHistoryList.add(HistoryItem(reservation.reservationId, reservation.hospitalId, reservation.status, reservation.hospitalName, reservation.className, reservation.reservationDate.toString(), reservation.reservationTime.toString(), reservation.reviewWriteBoolean))
                            }
                        }
                        adapter.updatelist(reserveHistoryList)
                    }
                }

                //통신 성공, 응답 실패
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

            //통신 실패
            override fun onFailure(call: Call<List<UserReservationResponse>>, t: Throwable) {
            }
        })


        // 코루틴을 사용하여 순차적으로 API 호출
        /*lifecycleScope.launch {
            try {
                //첫 번째 API 호출
                responseBodyUserReservation = apiService.getUserReservation()

                reserveHistoryList = ArrayList()
                for (reservation in responseBodyUserReservation) {
                    if (reservation.status == "예약신청" || reservation.status == "예약확정" || reservation.status == "예약취소") {
                        reserveHistoryList.add(HistoryItem(reservation.status, reservation.hospitalName, "", reservation.reservationDate.toString(), reservation.reservationTime.toString(), false))
                    }
                }
                adapter.updatelist(reserveHistoryList)
            } catch (e: Exception) {
                Log.e("API Error", "Error fetching data: ${e.localizedMessage}")
            }
        }*/

/*
        reserveHistoryList = ArrayList()
        reserveHistoryList.add(HistoryItem("예약","강남대학병원", "내과", "2024.2.14","15:00", false))
        reserveHistoryList.add(HistoryItem("예약","서울병원", "이비인후과", "2024.3.14", "14:00", false))
        reserveHistoryList.add(HistoryItem("예약", "별빛한의원", "외과", "2024.3.25", "18:30", false))
        reserveHistoryList.add(HistoryItem("대기중","강남성형외과", "성형외과", "2024.5.13", "13:30", false))
        reserveHistoryList.add(HistoryItem("대기중","버팀병원", "내과", "2024.05.28", "16:20", false))
        adapter.updatelist(reserveHistoryList)
*/


        return binding.root
    }
}