package com.example.reservationapp.navigation

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
import com.example.reservationapp.PharmacyMapActivity
import com.example.reservationapp.Adapter.PopularHospitalAdapter
import com.example.reservationapp.Adapter.ReserveAlarmAdapter
import com.example.reservationapp.App
import com.example.reservationapp.ChatActivity
import com.example.reservationapp.HospitalListActivity
import com.example.reservationapp.HospitalMapActivity
import com.example.reservationapp.HospitalSearchActivity
import com.example.reservationapp.MainActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.AllBookmarkResponse
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.Model.PopularHospitalItem
import com.example.reservationapp.Model.RecentSearchWordResponseData
import com.example.reservationapp.Model.ReserveItem
import com.example.reservationapp.Model.UserReservationResponse
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.FragmentHomeBinding
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var mapViewHospital: MapView
    private lateinit var mapViewMedicine: MapView

    private lateinit var reserveAlarmAdapter: ReserveAlarmAdapter //병원 예약 알림 adapter
    private lateinit var popularHospitalAdapter: PopularHospitalAdapter //인기 순위 병원 adapter
    private lateinit var userReserveAlarm: ArrayList<ReserveItem> //유저가 예약한 병원 리스트

    private val classReserveList: List<String> = listOf("내과", "외과", "이비인후과", "피부과", "안과", "성형외과", "신경외과", "소아청소년과") //진료과별 예약 리스트
    private val symptomReserveList: List<String> = listOf("발열", "기침", "가래", "인후통", "가슴 통증", "호흡 곤란", "두통", "구토 및 설사", "소화불량", "배탈", "가려움증", "피부 발진", "관절통", "근육통", "시력문제") //증상, 질환별 예약 리스트

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


        //예약 더보기 버튼
        val reserveMoreButton = binding.commingMoreTextView
        reserveMoreButton.setOnClickListener {
            mainActivity.navigation.selectedItemId = R.id.checkupFrag
            mainActivity.navigationSetItem()
        }

        //진료과, 증상별 버튼 설정
        val classButtonList: List<Button> = listOf(binding.classButton1, binding.classButton2, binding.classButton3, binding.classButton4)
        val symptomButtonList: List<Button> = listOf(binding.symptomButton1, binding.symptomButton2, binding.symptomButton3, binding.symptomButton4)
        for(i in classButtonList.indices) {
            //val intent = Intent(requireActivity(), HospitalListActivity::class.java)
            classButtonList[i].text = classReserveList[i]
            classButtonList[i].setOnClickListener {
                recentSearchWord(classReserveList, i)
            }

            symptomButtonList[i].text = symptomReserveList[i]
            symptomButtonList[i].setOnClickListener {
                recentSearchWord(symptomReserveList, i)
            }
        }

        //진료과별 더보기 버튼
        val classMoreTextView = binding.classMoreTextView
        classMoreTextView.setOnClickListener {
            mainActivity.showBottomSheet("진료과")
        }
        //증상 질환별 더보기 버튼
        val syptomMoreTextView = binding.symptomMoreTextView
        syptomMoreTextView.setOnClickListener {
            mainActivity.showBottomSheet("증상별")
        }


        //주변에 위치한 병원지도
        val textViewMap = binding.textView8 //view.findViewById<TextView>(R.id.textView8)
        textViewMap.setOnClickListener {
            val intent = Intent(requireActivity(), HospitalMapActivity::class.java)
            startActivity(intent)
        }

        //주변에 위치한 약국지도
        val textViewMap2 = binding.textView9 //view.findViewById<TextView>(R.id.textView9)
        textViewMap2.setOnClickListener {
            val intent = Intent(requireActivity(), PharmacyMapActivity::class.java)
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


    //사라졌다가 다시 보여질때
    override fun onResume() {
        super.onResume()

        //병원 예약 알림 recyclerView
        reserveAlarmAdapter = ReserveAlarmAdapter()
        val reserveAlarmRecyclerView = binding.reserveAlarmRecyclerView //view.findViewById<RecyclerView>(R.id.reserve_alarm_RecyclerView)
        val reserveAlarmLinearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        reserveAlarmRecyclerView.adapter = reserveAlarmAdapter
        reserveAlarmRecyclerView.layoutManager = reserveAlarmLinearLayoutManager
        //recyclerView.suppressLayout(true) //리사이클러뷰 스크롤 불가


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
                        //if(reservation.status == "예약신청" || reservation.status == "예약확정") {
                        if(reservation.status == "예약확정") {
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

                else handleErrorResponse(response)
            }

            override fun onFailure(call: Call<List<UserReservationResponse>>, t: Throwable) {

            }
        })

        //인기 순위 병원 adapter, recyclerView 초기화
        popularHospitalAdapter = PopularHospitalAdapter()
        val popularHospitalRecyclerView = binding.popularHospitalRecyclerView
        val popularHospitalLinearLayoutManger = LinearLayoutManager(activity)
        popularHospitalRecyclerView.adapter = popularHospitalAdapter
        popularHospitalRecyclerView.layoutManager = popularHospitalLinearLayoutManger
        popularHospitalRecyclerView.setHasFixedSize(true)

        val bookmarkHospitalScoreTextView = binding.textViewPopularHospitalScore
        val bookmarkHospitalImageView = binding.bookmarkImageView

        //인기 순위 병원 설정
        apiService.getAllHospitalBookmark().enqueue(object: Callback<AllBookmarkResponse> {
            @RequiresApi(Build.VERSION_CODES.N)
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
                        if(bookmarkItemList.isEmpty()) { //인기순위 비어있으면
                            bookmarkHospitalScoreTextView.visibility = View.GONE
                            popularHospitalRecyclerView.visibility = View.GONE
                            bookmarkHospitalImageView.visibility = View.GONE
                        } else {
                            bookmarkHospitalScoreTextView.visibility = View.VISIBLE
                            popularHospitalRecyclerView.visibility = View.VISIBLE
                            bookmarkHospitalImageView.visibility = View.VISIBLE
                        }
                        Log.w("HomeFragment", "updateList bookmarkItemList: $bookmarkItemList")
                    }
                }

                //통신 성공, 응답 실패
                else handleErrorResponse(response)
            }

            //통신 실패
            override fun onFailure(call: Call<AllBookmarkResponse>, t: Throwable) {
                Log.w("HomeFragment CONNECTION FAILURE: ", "Connect FAILURE : ${t.localizedMessage}")
            }
        })
    }


    //검색어저장
    private fun recentSearchWord(list: List<String>, i: Int) {
        //로그인 되어 있으면 저장되게
        if(App.prefs.token != null) {
            apiService.postRecentSearchWord(list[i]).enqueue(object: Callback<RecentSearchWordResponseData> {
                override fun onResponse(call: Call<RecentSearchWordResponseData>, response: Response<RecentSearchWordResponseData>) {
                    if(response.isSuccessful) {
                        val responseBodyPost = response.body()!!
                        Log.w("HospitalSearchActivity", "검색어 저장! responseBodyPost : $responseBodyPost")

                        val intent = Intent(requireActivity(), HospitalListActivity::class.java)
                        intent.putExtra("searchWord", list[i])
                        startActivity(intent)
                    }

                    else handleErrorResponse(response)
                }

                override fun onFailure(call: Call<RecentSearchWordResponseData>, t: Throwable) {
                    Log.w("HospitalSearchActivity", "post Recent Search Word API call failed: ${t.localizedMessage}")
                }
            })
        }

        //로그인 안되어 있으면 전달만 하기
        else {
/*
            recentSearchWordList.add(0, RecentItem(searchWord))
            adapter.updateList(recentSearchWordList)
*/

            val intent = Intent(requireActivity(), HospitalListActivity::class.java)
            intent.putExtra("searchWord", list[i])
            startActivity(intent)
        }
    }

    //
}
