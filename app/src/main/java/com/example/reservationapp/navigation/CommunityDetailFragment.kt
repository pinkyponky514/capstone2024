package com.example.reservationapp.navigation

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return inflater.inflate(R.layout.fragment_communitydetail, container, false)
    }
}
