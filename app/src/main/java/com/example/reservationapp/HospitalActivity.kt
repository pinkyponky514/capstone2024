package com.example.reservationapp

import ReservationAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalDetailResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReservationItem
import com.example.reservationapp.Model.Reservations
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
class HospitalActivity : AppCompatActivity() {

    private lateinit var reservationAdapter: ReservationAdapter
    private lateinit var reservationList: ArrayList<ReservationItem>
    private var selectedDate: String? = null
    private var hospitalId: Long? = null
    private lateinit var hospitalName:String

    private lateinit var reservations: List<Reservations>

    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)

        hospitalId = intent.getLongExtra("hospitalId", -1)
        Log.d("HospitalActivity", "hospitalId: $hospitalId") // hospitalId 로그 확인

        val hospitalNameTextView: TextView = findViewById(R.id.hospital_name)

        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()


        // 상태 복원을 위해 onRestoreInstanceState 호출을 대기
        if (savedInstanceState == null) {  //savedInstanceState가 null이 아닌 경우 hospitalName을 복원
            // 병원 정보를 가져와서 UI를 업데이트하는 메소드 호출
            fetchHospitalDetails(hospitalId, hospitalNameTextView)
        } else {
            // 액티비티가 다시 포그라운드로 돌아올 때 hospitalName이 유지
            hospitalName = savedInstanceState.getString("hospitalName", "")
            hospitalNameTextView.text = hospitalName

        }

        val reservationRecyclerView = findViewById<RecyclerView>(R.id.reservation_list)
        val calendarView = findViewById<CalendarView>(R.id.calendar_view)

//        // 예약 리스트 초기화 및 임의의 데이터 값 설정

            reservationList = ArrayList()
//        reservationList.add(ReservationItem("10:00", "김철수", "1990-04-11", "2024-04-29"))
//        reservationList.add(ReservationItem("14:30", "이영희", "1985-04-19", "2024-04-29"))
//        reservationList.add(ReservationItem("12:30", "김아무", "1985-04-30", "2024-05-19"))
//        reservationList.add(ReservationItem("11:30", "유재석", "1985-05-23", "2024-06-01"))
//        reservationList.add(ReservationItem("15:30", "이광수", "2024-05-20", "2024-03-01"))


        // 예약된 내역을 표시할 RecyclerView 설정
        reservationAdapter = ReservationAdapter()
        reservationRecyclerView.layoutManager = LinearLayoutManager(this)
        reservationRecyclerView.adapter = reservationAdapter


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val formattedMonth = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
            val formattedDay = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            val newSelectedDate = "$year-$formattedMonth-$formattedDay" // 새로 선택된 날짜 문자열로 변환
            if (selectedDate != newSelectedDate) {
                selectedDate = newSelectedDate
                updateReservationList()
            }
        }


        hospitalNameTextView.setOnClickListener {
            val intent = Intent(this, Hospital_Mypage::class.java)

            intent.putExtra("hospitalName", hospitalName)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {  //onSaveInstanceState를 오버라이드, hospitalName을 저장
        super.onSaveInstanceState(outState)
        outState.putString("hospitalName", hospitalName)  //
    }

    //병원 정보 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchHospitalDetails(hospitalId: Long?, hospitalNameTextView: TextView) {
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
                                date.toString()
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

    private fun updateReservationList() {
        val filteredList = filterReservationList(selectedDate)
        val sortedList = filteredList.sorted()
        reservationAdapter.updateList(sortedList)
    }

    private fun filterReservationList(selectedDate: String?): List<ReservationItem> {
        return reservationList.filter { it.reservationDate == selectedDate }
    }

}