package com.example.reservationapp.navigation


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.ReserveAlarmAdapter
import com.example.reservationapp.ChatActivity
import com.example.reservationapp.DrugstoreMap
import com.example.reservationapp.HospitalMap
import com.example.reservationapp.HospitalSearchActivity
import com.example.reservationapp.Hospital_DetailPage
import com.example.reservationapp.MainActivity
import com.example.reservationapp.Model.ReserveItem
import com.example.reservationapp.R
import com.example.reservationapp.databinding.FragmentHomeBinding
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapSdk

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var mapViewHospital: MapView
    private lateinit var mapViewMedicine: MapView

    private lateinit var adapter: ReserveAlarmAdapter //병원 예약 알림 adapter
    private lateinit var userReserveAlarm: ArrayList<ReserveItem> //유저가 예약한 병원 리스트

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater) //val view = inflater.inflate(R.layout.fragment_home, container, false)

        //지도 API
        NaverMapSdk.getInstance(requireContext()).client = NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))

        mapViewHospital = binding.destinationmap //view.findViewById(R.id.destinationmap)
        mapViewHospital.onCreate(savedInstanceState)

        mapViewMedicine = binding.medicinemap //view.findViewById(R.id.medicinemap)
        mapViewMedicine.onCreate(savedInstanceState)



        //검색창(텍스트) 눌렀을때 이벤트 처리
        val reserveSearchView = binding.reserveSearchView
        reserveSearchView.setOnQueryTextFocusChangeListener { reserveSearchView, hasFocus ->
            if(hasFocus) { //포커스를 가지고 있으면
                MainActivity().setActivity(requireActivity(), HospitalSearchActivity())
                reserveSearchView.clearFocus()
            }
        }
        //검색창(이미지) 눌렀을때 이벤트 처리
        reserveSearchView.setOnClickListener {
            MainActivity().setActivity(requireActivity(), HospitalSearchActivity())
        }


        //병원 예약 알림 recyclerView
        adapter = ReserveAlarmAdapter()
        val recyclerView = binding.reserveAlarmRecyclerView //view.findViewById<RecyclerView>(R.id.reserve_alarm_RecyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager


        // 가까운 예약 순으로 정렬 필요
        // DB 연결 필요
        userReserveAlarm = ArrayList()
        userReserveAlarm.add(ReserveItem("강남대학병원", "수 15:00"))
        userReserveAlarm.add(ReserveItem("서울병원", "목 14:00"))
        userReserveAlarm.add(ReserveItem("별빛한의원", "월 18:40"))
        userReserveAlarm.add(ReserveItem("강남성형외과", "월 13:30"))
        userReserveAlarm.add(ReserveItem("버팀병원", "화 16:20"))

        adapter.updateList(userReserveAlarm)


        //주변에 위치한 병원지도
        val textViewMap = binding.textView8 //view.findViewById<TextView>(R.id.textView8)
        textViewMap.setOnClickListener {
            val intent = Intent(requireActivity(), HospitalMap::class.java)
            startActivity(intent)
        }

        //주변에 위치한 약국지도
        val textViewMap2 = binding.textView9 //view.findViewById<TextView>(R.id.textView9)
        textViewMap2.setOnClickListener {
            val intent = Intent(requireActivity(), DrugstoreMap::class.java)
            startActivity(intent)
        }

        //채팅 서비스 버튼 클릭 이벤트 처리
        val chatServiceButton = binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            startActivity(intent)
        }

        return binding.root //return view
    }



    //
    override fun onResume() {
        super.onResume()
        mapViewHospital.onResume()
        mapViewMedicine.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewHospital.onPause()
        mapViewMedicine.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapViewHospital.onDestroy()
        mapViewMedicine.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewHospital.onLowMemory()
        mapViewMedicine.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewHospital.onSaveInstanceState(outState)
        mapViewMedicine.onSaveInstanceState(outState)
    }
}
