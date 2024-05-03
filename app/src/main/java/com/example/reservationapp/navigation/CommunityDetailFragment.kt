package com.example.reservationapp.navigation

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.reservationapp.R

class CommunityDetailFragment : Fragment() {
    companion object {
        private const val ARG_IMAGE_RESOURCE = "arg_image_resource"
        private const val ARG_IMAGE_TITLE = "arg_image_title"

        fun newInstance(imageResource: Int, imageTitle: String): CommunityDetailFragment {
            val fragment = CommunityDetailFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RESOURCE, imageResource)
            args.putString(ARG_IMAGE_TITLE, imageTitle)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_communitydetail, container, false)

        // 채팅하기 버튼에 대한 참조 가져오기
        val buttonChat = view.findViewById<Button>(R.id.buttonChat)

        // 채팅하기 버튼 클릭 이벤트 처리
        buttonChat.setOnClickListener {
            // CommunityChatFragment로 이동하기 위해 Intent를 사용하여 다른 액티비티나 프래그먼트로 이동
            val intent = Intent(requireContext(), CommunityChatFragment::class.java)
            startActivity(intent)
        }

        return view
    }

}
