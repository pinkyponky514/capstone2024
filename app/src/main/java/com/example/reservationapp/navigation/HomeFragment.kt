package com.example.reservationapp.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.ReserveAlarmAdapter
import com.example.reservationapp.DrugstoreMap
import com.example.reservationapp.HospitalMap
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
    private lateinit var adapter: ReserveAlarmAdapter
    private lateinit var userReserveAlarm: ArrayList<ReserveItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_client_id))

        mapViewHospital = view.findViewById(R.id.destinationmap)
        mapViewHospital.onCreate(savedInstanceState)

        mapViewMedicine = view.findViewById(R.id.medicinemap)
        mapViewMedicine.onCreate(savedInstanceState)

        val main = activity as MainActivity

        adapter = ReserveAlarmAdapter()

        val recyclerView = view.findViewById<RecyclerView>(R.id.reserve_alarm_RecyclerView)
        val linearLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        // 가까운 예약 순으로 정렬 필요
        userReserveAlarm = ArrayList()
        userReserveAlarm.add(ReserveItem("강남대학병원", "수 15:00"))
        userReserveAlarm.add(ReserveItem("서울병원", "목 14:00"))
        userReserveAlarm.add(ReserveItem("별빛한의원", "월 18:40"))
        userReserveAlarm.add(ReserveItem("강남성형외과", "월 13:30"))
        userReserveAlarm.add(ReserveItem("버팀병원", "화 16:20"))

        adapter.updateList(userReserveAlarm)

        val textViewMap = view.findViewById<TextView>(R.id.textView8)
        textViewMap.setOnClickListener {
            val intent = Intent(requireActivity(), HospitalMap::class.java)
            startActivity(intent)
        }

        val textViewMap2 = view.findViewById<TextView>(R.id.textView9)
        textViewMap2.setOnClickListener {
            val intent = Intent(requireActivity(), DrugstoreMap::class.java)
            startActivity(intent)
        }

        // 버튼 생성 및 클릭 이벤트 처리
        val hospitalMapButton = view.findViewById<Button>(R.id.hospitalMapButton)
        hospitalMapButton.setOnClickListener {
            val intent = Intent(requireActivity(), Hospital_DetailPage::class.java)
            startActivity(intent)
        }

        return view
    }

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
