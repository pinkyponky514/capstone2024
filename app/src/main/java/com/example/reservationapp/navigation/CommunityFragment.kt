package com.example.reservationapp.navigation

import CommunityImageAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.CommunityItem
import com.example.reservationapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommunityImageAdapter
    private lateinit var timestamp: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // item_community 레이아웃을 inflate하여 View 객체를 생성합니다.
        val view = inflater.inflate(R.layout.fragment_community, container, false)

//        // 작성 시간을 표시하는 TextView를 찾아 변수에 할당합니다.
//        timestamp = view.findViewById(R.id.timestamp)

        return view
    }

    // onViewCreated 메서드는 그대로 유지합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()

        // floatingActionButton 클릭 시 CommunityPostFragment로 이동
        view.findViewById<View>(R.id.floatingActionButton2)?.setOnClickListener {
            val fragment = CommunityPostFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initializeRecyclerView() {
        // RecyclerView 참조 가져오기
        recyclerView = requireView().findViewById(R.id.recyclerViewCommunity)

        // RecyclerView에 레이아웃 매니저 설정
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // 현재 시간을 가져와서 형식을 맞춘 후 TextView에 설정합니다.
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedTime = sdf.format(currentTime)

        // RecyclerView에 Adapter 설정
        val itemList = listOf(
            CommunityItem(R.drawable.ex_communityimage1, "세균 번식 속도에 대해 알아보자", "내가기자다", "1", "2",formattedTime),
            CommunityItem(R.drawable.ex_communityimage2, "우리 아이가 어제 수술을 했어요", "맘카페대장", "1", "2", formattedTime),
            CommunityItem(R.drawable.ex_communityimage3, "마스크는 어른보다 아이들이 더 많이 착용", "마스크회사사장", "1", "2", formattedTime),
            CommunityItem(R.drawable.ex_communityimage4, "아플 때는 병원으로 바로 가세요", "아무개", "1", "2", formattedTime),
            CommunityItem(R.drawable.ex_communityimage1, "이번달 판매율 1등 약국은 어디 ?", "내가기자다", "1", "2", formattedTime),
            CommunityItem(R.drawable.ex_communityimage2, "아이스크림 너무 먹어 배탈났다 ?!", "맘카페대장", "1", "2", formattedTime),
            CommunityItem(R.drawable.ex_communityimage3, "신종 벌레 화제, 대체 방안 모색 중", "마스크회사사장", "1", "2", formattedTime),
            CommunityItem(R.drawable.ex_communityimage4, "엄마가 좋아, 아빠가 좋아 ?", "아무개", "1", "2",formattedTime)
        )
        adapter = CommunityImageAdapter(itemList) { position ->
            // RecyclerView의 아이템 클릭 시 동작 정의
            val item = itemList[position]
            val fragment = CommunityDetailFragment.newInstance(item.imageResource, item.title)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
    }

}
