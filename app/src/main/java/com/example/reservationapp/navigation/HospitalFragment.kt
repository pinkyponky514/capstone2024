package com.example.reservationapp.navigation

import ReservationAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.HospitalMainActivity
import com.example.reservationapp.Hospital_Mypage
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.Hospital
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReservationItem
import com.example.reservationapp.Model.Reservations
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.FragmentHospitalBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class HospitalFragment : Fragment() {
    private lateinit var binding: FragmentHospitalBinding

    private lateinit var reservationAdapter: ReservationAdapter
    private lateinit var reservationList: ArrayList<ReservationItem>
    private var selectedDate: String = ""

    private lateinit var hospitalName:String
    private lateinit var reservations: List<Reservations>


    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHospitalBinding.inflate(inflater)

        val reservationRecyclerView = binding.reservationList //requireView().findViewById<RecyclerView>(R.id.reservation_list)
        val calendarView = binding.calendarView //requireView().findViewById<CalendarView>(R.id.calendar_view)
        val hospitalNameTextView =  binding.hospitalName

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()


        // 예약 리스트 초기화 및 임의의 데이터 값 설정
        reservationList = ArrayList()


        // 예약된 내역을 표시할 RecyclerView 설정
        reservationAdapter = ReservationAdapter()
        reservationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reservationRecyclerView.adapter = reservationAdapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val newSelectedDate = String.format("%d-%02d-%02d", year, month+1, dayOfMonth) // 새로 선택된 날짜 문자열로 변환

            if (selectedDate != newSelectedDate) {
                selectedDate = newSelectedDate
                updateReservationList()
            }
        }


        // 상태 복원을 위해 onRestoreInstanceState 호출을 대기
        if (savedInstanceState == null) {  //savedInstanceState가 null이 아닌 경우 hospitalName을 복원
            // 병원 정보를 가져와서 UI를 업데이트하는 메소드 호출
            fetchHospitalDetails(HospitalMainActivity().hospitalId, hospitalNameTextView)
        } else {
            // 액티비티가 다시 포그라운드로 돌아올 때 hospitalName이 유지
            hospitalName = savedInstanceState.getString("hospitalName", "")
            hospitalNameTextView.text = hospitalName
        }

        hospitalNameTextView.setOnClickListener {
            val intent = Intent(requireContext(), Hospital_Mypage::class.java)
            intent.putExtra("hospitalName", hospitalName)
            startActivity(intent)
        }


        return binding.root
    }


    //
    override fun onSaveInstanceState(outState: Bundle) {  //onSaveInstanceState를 오버라이드, hospitalName을 저장
        super.onSaveInstanceState(outState)
        outState.putString("hospitalName", hospitalName)  //
    }


    //병원 정보 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchHospitalDetails(hospitalId: Long, hospitalNameTextView: TextView) {
        apiService.getHospitalDetail(hospitalId).enqueue(object : Callback<HospitalSignupInfoResponse> {
            override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                if (response.isSuccessful) {
                    val responseBodyDetail = response.body()!!
                    Log.d("SUCCESS Response", "Message: ${responseBodyDetail.message}")
                    hospitalName = responseBodyDetail.data.name
                    hospitalNameTextView.text = hospitalName

                    reservations = responseBodyDetail.data.reservations

                    for(reservation in reservations) {

                        val time = reservation.reservationTime
                        val date = reservation.reservationDate
                        reservationList.add(
                            ReservationItem(
                                time.toString(),
                                "kk",
                                "2024-05-20",
                                date.toString(),
                                ""
                            )
                        )

                    }
                    reservationAdapter.updateList(reservationList)

                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }


    //
    private fun handleErrorResponse(response: Response<HospitalSignupInfoResponse>) {
        val errorBody = response.errorBody()?.string()
        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: $errorBody")
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

    //
    private fun updateReservationList() {
        val filteredList = filterReservationList(selectedDate)
        val sortedList = filteredList.sorted()
        reservationAdapter.updateList(sortedList)
    }

    //
    private fun filterReservationList(selectedDate: String?): List<ReservationItem> {
        return reservationList.filter { it.reservationDate == selectedDate }
    }


}