package com.example.reservationapp.navigation

import CommunityImageAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.CommunityItem
import com.example.reservationapp.R
import com.example.reservationapp.navigation.CommunityPostFragment

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommunityImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        // RecyclerView 참조 가져오기
        recyclerView = requireView().findViewById(R.id.recyclerViewCommunity)

        // RecyclerView에 레이아웃 매니저 설정
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // RecyclerView에 Adapter 설정
        val itemList = listOf(
            CommunityItem(R.drawable.communityimage1, "세균 번식 속도가 얼마나 빠른지 아시나요 ?"),
            CommunityItem(R.drawable.communityimage2, "우리 아이가 어제 수술을 했어요"),
            CommunityItem(R.drawable.communityimage3, "마스크는 어른보다 아이들이 더 많이 착용, 아이들에게 본받아야 할 부분"),
            CommunityItem(R.drawable.communityimage4, "아플 때는 병원으로 바로 가세요. 참는 것은 독이 됩니다.")
            // 나머지 아이템 추가
        )
        adapter = CommunityImageAdapter(itemList) { position ->
            // RecyclerView의 아이템 클릭 시 동작 정의
            if (position == itemList.size) {
                // 마지막 아이템인 경우, CommunityPostFragment로 이동합니다.
                val fragment = CommunityPostFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // 아이템을 클릭한 경우, CommunityDetailFragment로 이동합니다.
                val item = itemList[position]
                val fragment = CommunityDetailFragment.newInstance(item.imageResource, item.title)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        recyclerView.adapter = adapter
    }
}
