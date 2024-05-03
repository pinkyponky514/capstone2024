package com.example.reservationapp.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.reservationapp.R

@Suppress("DEPRECATION") //컴파일러가 deprecated된 메서드 사용에 대한 경고를 무시
class CommunityPostFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_communitypost, container, false)

        // 이미지뷰를 참조합니다.
        imageView = view.findViewById(R.id.imageView)

        // 제출 버튼을 참조합니다.
        submitButton = view.findViewById(R.id.submit_button)

        // 이미지뷰 클릭 이벤트 처리
        imageView.setOnClickListener {
            // 갤러리에서 이미지 선택하는 Intent 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        // 제출 버튼 클릭 이벤트 처리
        submitButton.setOnClickListener {
            // FragmentManager를 사용하여 CommunityFragment로 이동
            fragmentManager?.popBackStack() // 이전 프래그먼트로 이동
        }

        return view
    }

    // 갤러리에서 이미지를 선택한 결과를 처리합니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            // 이미지 선택 결과를 이미지뷰에 설정합니다.
            val selectedImageUri = data.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}
