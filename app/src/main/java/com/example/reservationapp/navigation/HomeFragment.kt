package com.example.reservationapp.navigation


import com.example.reservationapp.CustomMoreDialogActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservationapp.Adapter.PopularHospitalAdapter
import com.example.reservationapp.Adapter.ReserveAlarmAdapter
import com.example.reservationapp.ChatActivity
import com.example.reservationapp.DrugstoreMap
import com.example.reservationapp.HospitalListActivity
import com.example.reservationapp.HospitalMap
import com.example.reservationapp.HospitalSearchActivity
import com.example.reservationapp.MainActivity
import com.example.reservationapp.Model.PopularHospitalItem
import com.example.reservationapp.Model.ReserveItem
import com.example.reservationapp.Model.filterList
import com.example.reservationapp.R
import com.example.reservationapp.databinding.FragmentHomeBinding
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapSdk

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var mapViewHospital: MapView
    private lateinit var mapViewMedicine: MapView

    private lateinit var reserveAlarmAdapter: ReserveAlarmAdapter //병원 예약 알림 adapter
    private lateinit var popularHospitalAdapter: PopularHospitalAdapter //인기 순위 병원 adapter
    private lateinit var userReserveAlarm: ArrayList<ReserveItem> //유저가 예약한 병원 리스트


    private val classReserveList: List<String> = listOf("내과", "외과", "이비인후과이비", "피부과", "안과", "성형외과", "신경외과", "소아청소년과") //진료과별 예약 리스트
    private val syptomReserveList: List<String> = listOf("발열", "기침", "가래", "인후통", "가슴 통증", "호흡 곤란", "두통", "구토 및 설사", "소화불량", "배탈", "가려움증", "피부 발진", "관절통", "근육통", "시력문제") //증상, 질환별 예약 리스트


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater) //val view = inflater.inflate(R.layout.fragment_home, container, false)

        val mainActivity = requireActivity() as MainActivity //MainActivity 접근
        mainActivity.medicalHistoryFragment = MedicalHistoryFragment.newInstance("")


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
                mainActivity.setActivity(requireActivity(), HospitalSearchActivity())
                reserveSearchView.clearFocus()
            }
        }
        //검색창(이미지) 눌렀을때 이벤트 처리
        reserveSearchView.setOnClickListener {
            mainActivity.setActivity(requireActivity(), HospitalSearchActivity())
        }


        //병원 예약 알림 recyclerView
        reserveAlarmAdapter = ReserveAlarmAdapter()
        val reserveAlarmRecyclerView = binding.reserveAlarmRecyclerView //view.findViewById<RecyclerView>(R.id.reserve_alarm_RecyclerView)
        val reserveAlarmLinearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        reserveAlarmRecyclerView.adapter = reserveAlarmAdapter
        reserveAlarmRecyclerView.layoutManager = reserveAlarmLinearLayoutManager
        //recyclerView.suppressLayout(true) //리사이클러뷰 스크롤 불가

        // 가까운 예약 순으로 정렬 필요
        // DB 연결 필요
        userReserveAlarm = ArrayList()
        userReserveAlarm.add(ReserveItem("강남대학병원", "수 15:00"))
        userReserveAlarm.add(ReserveItem("서울병원", "목 14:00"))
        userReserveAlarm.add(ReserveItem("별빛한의원", "월 18:40"))
        userReserveAlarm.add(ReserveItem("강남성형외과", "월 13:30"))
        userReserveAlarm.add(ReserveItem("버팀병원", "화 16:20"))



        //예약 리스트에 아무것도 없으면 보이지 않게
        val commingReserveTextView = binding.commingReserveTextView
        val commingMoreTextView = binding.commingMoreTextView

        reserveAlarmRecyclerView.visibility = View.GONE
        commingReserveTextView.visibility = View.GONE
        commingMoreTextView.visibility = View.GONE


        if(userReserveAlarm .isEmpty()) {
            reserveAlarmRecyclerView.visibility = View.GONE
            commingReserveTextView.visibility = View.GONE
            commingMoreTextView.visibility = View.GONE
        } else {
            reserveAlarmRecyclerView.visibility = View.VISIBLE
            commingReserveTextView.visibility = View.VISIBLE
            commingMoreTextView.visibility = View.VISIBLE
            reserveAlarmAdapter.updateList(userReserveAlarm)
        }


        //예약 더보기 버튼
        val reserveMoreButton = binding.commingMoreTextView
        reserveMoreButton.setOnClickListener {
            mainActivity.navigation.selectedItemId = R.id.checkupFrag
            mainActivity.medicalHistoryFragment = MedicalHistoryFragment.newInstance("reserve")
            mainActivity.navigationSetItem()
        }


        //진료과별 예약 버튼
        for(i in 0..3) {
            var classButtonId = resources.getIdentifier("class_button${i+1}", "id", context?.packageName)
            var button = binding.root.findViewById<Button>(classButtonId) //var button = view.findViewById<Button>(classButtonId)

            button.text = classReserveList[i]
            button.setOnClickListener {
                val intent = Intent(requireActivity(), HospitalListActivity::class.java)
                intent.putExtra("searchWord", button.text)
                startActivity(intent)
            }

            Log.w("Class Button Info", "Button ID: $classButtonId, Text: ${classReserveList[i]}, Button Object: $button") //Log 찍어보는 부분
        }
        //진료과별 더보기 버튼
        val classMoreTextView = binding.classMoreTextView
        classMoreTextView.setOnClickListener {
            val dialog = CustomMoreDialogActivity.newInstance(classReserveList) //val dialog = CustomMoreDialogActivity(classReserveList)
            dialog.show(parentFragmentManager,  "CustomMoreDialog")
        }


        //증상 질환별 예약 버튼
        for(i in 0..3) {
            var syptomButtonId = resources.getIdentifier("symptom_button${i+1}", "id", context?.packageName)
            var button = binding.root.findViewById<Button>(syptomButtonId) //var button = view.findViewById<Button>(classButtonId)

            button.text = syptomReserveList[i]
            button.setOnClickListener {
                val intent = Intent(requireActivity(), HospitalListActivity::class.java)
                intent.putExtra("searchWord", button.text)
                startActivity(intent)
            }

            Log.w("Syptom Button Info", "Button ID: $syptomButtonId, Text: ${syptomReserveList[i]}, Button Object: $button") //Log 찍어보는 부분
        }
        //증상 질환별 더보기 버튼
        val syptomMoreTextView = binding.symptomMoreTextView
        syptomMoreTextView.setOnClickListener {
            val dialog = CustomMoreDialogActivity.newInstance(syptomReserveList)
            dialog.show(parentFragmentManager,  "CustomDialog")
        }


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
        val chatServiceButton = binding.floatingActionButton
        chatServiceButton.setOnClickListener {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            startActivity(intent)
        }

        return binding.root //return view
    }

    override fun onResume() {
        super.onResume()

        //인기 순위 병원 adapter, recyclerView
        popularHospitalAdapter = PopularHospitalAdapter()
        val popularHospitalRecyclerView = binding.popularHospitalRecyclerView
        val popularHospitalLinearLayoutManger = LinearLayoutManager(activity)
        popularHospitalRecyclerView.adapter = popularHospitalAdapter
        popularHospitalRecyclerView.layoutManager = popularHospitalLinearLayoutManger
        popularHospitalRecyclerView.setHasFixedSize(true)
        popularHospitalRecyclerView.suppressLayout(true) //스트롤 불가능


        val sortedFilterList = filterList.sortedByDescending { it.favoriteCount }
        Log.w("HomeFragment", "sortedFilterList: $sortedFilterList")

        val popularHospitalItemList = sortedFilterList.take(5).mapIndexed { index, filterItem -> //순위 5위까지만
            PopularHospitalItem(
                index + 1, // 순위는 1부터 시작하므로 index에 1을 더해줍니다.
                filterItem.hospitalName
            )
        }
        popularHospitalAdapter.updatelist(popularHospitalItemList)

    }

    //
}
