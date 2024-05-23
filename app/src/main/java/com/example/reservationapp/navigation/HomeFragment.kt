package com.example.reservationapp.navigation


import com.example.reservationapp.Custom.CustomMoreDialogFragment
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
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.AllBookmarkResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.PopularHospitalItem
import com.example.reservationapp.Model.ReserveItem
import com.example.reservationapp.Model.UserReservationResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.FragmentHomeBinding
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var mapViewHospital: MapView
    private lateinit var mapViewMedicine: MapView

    private lateinit var reserveAlarmAdapter: ReserveAlarmAdapter //병원 예약 알림 adapter
    private lateinit var popularHospitalAdapter: PopularHospitalAdapter //인기 순위 병원 adapter
    private lateinit var userReserveAlarm: ArrayList<ReserveItem> //유저가 예약한 병원 리스트


    private val classReserveList: List<String> = listOf("내과", "외과", "이비인후과이비", "피부과", "안과", "성형외과", "신경외과", "소아청소년과") //진료과별 예약 리스트
    private val syptomReserveList: List<String> = listOf("발열", "기침", "가래", "인후통", "가슴 통증", "호흡 곤란", "두통", "구토 및 설사", "소화불량", "배탈", "가려움증", "피부 발진", "관절통", "근육통", "시력문제") //증상, 질환별 예약 리스트

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService
    private lateinit var responseBody: AllBookmarkResponse
    private lateinit var responseBodyReservation: List<UserReservationResponse>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater) //val view = inflater.inflate(R.layout.fragment_home, container, false)

        val mainActivity = requireActivity() as MainActivity //MainActivity 접근
        mainActivity.tokenCheck()

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface() // = retrofit.create(APIService::class.java)


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

        // 가까운 예약 순으로 정렬
/*
        userReserveAlarm = ArrayList()
        userReserveAlarm.add(ReserveItem("강남대학병원", "수 15:00"))
        userReserveAlarm.add(ReserveItem("서울병원", "목 14:00"))
        userReserveAlarm.add(ReserveItem("별빛한의원", "월 18:40"))
        userReserveAlarm.add(ReserveItem("강남성형외과", "월 13:30"))
        userReserveAlarm.add(ReserveItem("버팀병원", "화 16:20"))
*/

        //예약 리스트에 아무것도 없으면 보이지 않게
        val commingReserveTextView = binding.commingReserveTextView
        val commingMoreTextView = binding.commingMoreTextView

        reserveAlarmRecyclerView.visibility = View.GONE
        commingReserveTextView.visibility = View.GONE
        commingMoreTextView.visibility = View.GONE


        apiService.getUserReservation().enqueue(object: Callback<List<UserReservationResponse>> {
            override fun onResponse(call: Call<List<UserReservationResponse>>, response: Response<List<UserReservationResponse>>) {
                if(response.isSuccessful) {
                    userReserveAlarm = ArrayList()
                    responseBodyReservation = response.body()!!

                    for(reservation in responseBodyReservation) {
                        if(reservation.status == "예약신청" || reservation.status == "예약확정") {
                            userReserveAlarm.add(ReserveItem(reservation.hospitalName, reservation.reservationDate.toString(), reservation.reservationTime.toString()))
                        }
                    }

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
                }

                else {

                }
            }

            override fun onFailure(call: Call<List<UserReservationResponse>>, t: Throwable) {

            }
        })


        //예약 더보기 버튼
        val reserveMoreButton = binding.commingMoreTextView
        reserveMoreButton.setOnClickListener {
            mainActivity.navigation.selectedItemId = R.id.checkupFrag
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

            Log.w("HomeFragment", "class Button ID: $classButtonId, Text: ${classReserveList[i]}, Button Object: $button") //Log 찍어보는 부분
        }
        //진료과별 더보기 버튼
        val classMoreTextView = binding.classMoreTextView
        classMoreTextView.setOnClickListener {
            val dialog = CustomMoreDialogFragment.newInstance(classReserveList) //val dialog = CustomMoreDialogActivity(classReserveList)
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

            Log.w("HomeFragment", "syptom Button ID: $syptomButtonId, Text: ${syptomReserveList[i]}, Button Object: $button") //Log 찍어보는 부분
        }
        //증상 질환별 더보기 버튼
        val syptomMoreTextView = binding.symptomMoreTextView
        syptomMoreTextView.setOnClickListener {
            val dialog = CustomMoreDialogFragment.newInstance(syptomReserveList)
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




        //인기 순위 병원 adapter, recyclerView 초기화
        popularHospitalAdapter = PopularHospitalAdapter()
        val popularHospitalRecyclerView = binding.popularHospitalRecyclerView
        val popularHospitalLinearLayoutManger = LinearLayoutManager(activity)
        popularHospitalRecyclerView.adapter = popularHospitalAdapter
        popularHospitalRecyclerView.layoutManager = popularHospitalLinearLayoutManger
        popularHospitalRecyclerView.setHasFixedSize(true)


        //인기 순위 병원 설정
        apiService.getAllHospitalBookmark().enqueue(object: Callback<AllBookmarkResponse> {
            override fun onResponse(call: Call<AllBookmarkResponse>, response: Response<AllBookmarkResponse>) {
                //통신, 응답 성공
                if(response.isSuccessful) {
                    responseBody = response.body()!!
                    Log.w("HomeFragment", "responseBody: $responseBody")

                    val hospitalBookmarkCountMap = mutableMapOf<Long, Int>()
                    for(bookmark in responseBody.data) {
                        val hospitalId = bookmark.hospitalId
                        hospitalBookmarkCountMap[hospitalId] = hospitalBookmarkCountMap.getOrDefault(hospitalId, 0) +1
                    }
                    Log.w("HomeFragment", "hospitalId에 따른 즐겨찾기 개수 hospitalBookmarkCountMap: $hospitalBookmarkCountMap")


                    //즐겨찾기 내림차순 정렬
                    val sortedHospitalIdList = hospitalBookmarkCountMap.entries.sortedByDescending { it.value }.map { it.key } // [1, 3, 4, 2, 5]
                    Log.w("HomeFragment", "즐겨찾기 내림차순 hospitalId 정렬 sortedFilterList: $sortedHospitalIdList")


                    //병원 상세정보 가져오기, 순차적으로 진행
                    GlobalScope.launch(Dispatchers.Main) {
                        val sortedHospitalDetailList = ArrayList<HospitalSignupInfoResponse>() //정렬된 병원 상세정보 리스트

                        sortedHospitalIdList.forEach { hospitalId ->
                            val response = withContext(Dispatchers.IO) { apiService.getHospitalDetail(hospitalId).execute() }
                            if (response.isSuccessful) { response.body()?.let { sortedHospitalDetailList.add(it) } }
                        }

                        var bookmarkItemList = ArrayList<PopularHospitalItem>() //adapter와 연결할 ItemList
                        if(sortedHospitalDetailList.size >= 5) { //병원 목록 리스트가 5개 이상이면
                            bookmarkItemList = sortedHospitalDetailList.take(5).mapIndexed { takeIndex, hospitalSignupInfoResponse ->
                                    PopularHospitalItem(takeIndex+1, hospitalSignupInfoResponse.data.hospitalId, hospitalSignupInfoResponse.data.name)
                                } as ArrayList
                            Log.w("HomeFragment", "take문 bookmarkItemList: $bookmarkItemList")
                        }
                        else { //병원 목록 리스트가 5개 미만이면
                            for(forIndex in sortedHospitalDetailList.indices) {
                                bookmarkItemList.add(PopularHospitalItem(forIndex+1, sortedHospitalDetailList[forIndex].data.hospitalId, sortedHospitalDetailList[forIndex].data.name))
                                Log.w("HomeFragment", "for문 bookmarkItemList: $bookmarkItemList")
                            }
                        }

                        popularHospitalAdapter.updatelist(bookmarkItemList)
                        Log.w("HomeFragment", "updateList bookmarkItemList: $bookmarkItemList")
                    }
                }

                //통신 성공, 응답 실패
                else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("HomeFragment FAILURE Response", "Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
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
            override fun onFailure(call: Call<AllBookmarkResponse>, t: Throwable) {
                Log.w("HomeFragment CONNECTION FAILURE: ", "Connect FAILURE : ${t.localizedMessage}")
            }
        })


        return binding.root //return view
    }

    //
}
