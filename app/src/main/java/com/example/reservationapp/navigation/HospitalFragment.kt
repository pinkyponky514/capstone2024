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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.reservationapp.App
import com.example.reservationapp.HospitalMainActivity
import com.example.reservationapp.HospitalSecurityActivity
//import com.example.reservationapp.Hospital_Mypage
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.Hospital
import com.example.reservationapp.Model.HospitalSearchResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.ReservationItem
import com.example.reservationapp.Model.Reservations
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.FragmentHospitalBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates


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

    private var hospitalid by Delegates.notNull<Long>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHospitalBinding.inflate(inflater)

        val reservationRecyclerView = binding.reservationList //requireView().findViewById<RecyclerView>(R.id.reservation_list)
        val calendarView = binding.calendarView //requireView().findViewById<CalendarView>(R.id.calendar_view)
        val hospitalNameTextView =  binding.hospitalName
        val securityButton = binding.hospitalSecurity
        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()


        // 예약 리스트 초기화 및 임의의 데이터 값 설정
        reservationList = ArrayList()


        // 예약된 내역을 표시할 RecyclerView 설정
        reservationAdapter = ReservationAdapter()
        reservationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reservationRecyclerView.adapter = reservationAdapter

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(currentDate)

        // 캘린더 초기 선택 날짜 설정
        calendarView.date = currentDate.time


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val newSelectedDate = String.format("%d-%02d-%02d", year, month+1, dayOfMonth) // 새로 선택된 날짜 문자열로 변환

            if (selectedDate != newSelectedDate) {
                selectedDate = newSelectedDate
                updateReservationList()
            }
        }

        securityButton.setOnClickListener{
            val intent = Intent(requireContext(), HospitalSecurityActivity::class.java)
            intent.putExtra("hospitalId", hospitalid)
            startActivity(intent)
        }

        // 상태 복원을 위해 onRestoreInstanceState 호출을 대기
        if (savedInstanceState == null) {  //savedInstanceState가 null이 아닌 경우 hospitalName을 복원
            // 병원 정보를 가져와서 UI를 업데이트하는 메소드 호출
            val hospitalMainActivity = requireActivity() as HospitalMainActivity
            val hospitalId = hospitalMainActivity.hospitalId
            fetchHospitalDetails(hospitalId, hospitalNameTextView)
        } else {
            // 액티비티가 다시 포그라운드로 돌아올 때 hospitalName이 유지
            hospitalName = savedInstanceState.getString("hospitalName", "")
            hospitalNameTextView.text = hospitalName
        }

        //새로고침
        var RefreshLayout: SwipeRefreshLayout = binding.refreshLayout
        RefreshLayout.setOnRefreshListener {
            RefreshLayout.isRefreshing = false

            // 상태 복원을 위해 onRestoreInstanceState 호출을 대기
            if (savedInstanceState == null) {  //savedInstanceState가 null이 아닌 경우 hospitalName을 복원
                // 병원 정보를 가져와서 UI를 업데이트하는 메소드 호출
                val hospitalMainActivity = requireActivity() as HospitalMainActivity
                val hospitalId = hospitalMainActivity.hospitalId
                fetchHospitalDetails(hospitalId, hospitalNameTextView)
            } else {
                // 액티비티가 다시 포그라운드로 돌아올 때 hospitalName이 유지
                hospitalName = savedInstanceState.getString("hospitalName", "")
                hospitalNameTextView.text = hospitalName
            }
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
        apiService.getHospitalDetail(hospitalId).enqueue(object : Callback<HospitalSearchResponse> {
            override fun onResponse(call: Call<HospitalSearchResponse>, response: Response<HospitalSearchResponse>) {
                if (response.isSuccessful) {
                    val responseBodyDetail = response.body()!!
                    Log.d("SUCCESS Response", "Message: ${responseBodyDetail.message}")
                    hospitalName = responseBodyDetail.data.hospital.name
                    hospitalNameTextView.text = hospitalName
                    App.hospitalName = hospitalName
                    App.prefs.hospitalName = hospitalName
                    hospitalid = responseBodyDetail.data.hospital.hospitalId

                    reservations = responseBodyDetail.data.hospital.reservations

                    reservationList.clear() // 기존 리스트 초기화

                    if (reservations.isNotEmpty()) {
                        for (reservation in reservations) {
                            val time = reservation.reservationTime
                            val date = reservation.reservationDate
                            val status = reservation.status
                            val user = reservation.user
                            reservationList.add(
                                ReservationItem(
                                    time.toString(),
                                    user.name,
                                    user.birthday,
                                    date.toString(),
                                    status
                                )
                            )
                        }
                        updateReservationList() // 초기 데이터 가져온 후 업데이트
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<HospitalSearchResponse>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
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