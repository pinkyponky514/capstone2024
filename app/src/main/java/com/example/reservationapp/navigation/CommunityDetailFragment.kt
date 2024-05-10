package com.example.reservationapp.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.reservationapp.R
import com.example.reservationapp.ReviewDialogActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    private lateinit var scaleAnimation: ScaleAnimation
    private lateinit var bounceInterpolator: BounceInterpolator
    private lateinit var buttonFavorite: CompoundButton
    private lateinit var timestamp2: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_communitydetail, container, false)

        // 좋아요 버튼에 대한 참조 가져오기
        buttonFavorite = view.findViewById(R.id.button_favorite)

        // 좋아요 버튼의 상태 변화 이벤트 처리
        buttonFavorite.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.startAnimation(scaleAnimation)
            // 버튼이 체크되어 있으면 하트를 채운 이미지로 설정
            if (isChecked) {
                buttonView.setBackgroundResource(R.drawable.ic_likes)
                Log.d("CommunityDetailFragment", "좋아요꺼짐")
            } else {
                // 버튼이 체크되어 있지 않으면 하트를 빈 이미지로 설정
                buttonView.setBackgroundResource(R.drawable.ic_favoritelikes)
                Log.d("CommunityDetailFragment", "좋아요")
            }
        }

        // 현재 시간을 가져옵니다.
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedTime = sdf.format(currentTime)

        // 작성 시간을 표시하는 TextView를 참조합니다.
        timestamp2 = view.findViewById(R.id.timestamp2)
        timestamp2.text = "작성 시간: $formattedTime"

        // 애니메이션 설정
        scaleAnimation = ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f)
        scaleAnimation.duration = 500
        bounceInterpolator = BounceInterpolator()
        scaleAnimation.interpolator = bounceInterpolator

//        // buttonSend 클릭 이벤트 핸들러 추가
//        val buttonSend = view.findViewById<Button>(R.id.buttonSend)
//        buttonSend.setOnClickListener {
//            // ReviewDialogActivity로 이동
//            val comment = "사용자의 댓글" // 여기에 사용자의 댓글을 가져오세요.
//            val intent = Intent(requireContext(), ReviewDialogActivity::class.java).apply {
//                putExtra("comment", comment)
//            }
//            startActivity(intent)
//        }

        // buttonSend 클릭 이벤트 핸들러 추가
        val buttonSend = view.findViewById<Button>(R.id.buttonSend)
        buttonSend.setOnClickListener {
            // ReviewDialogActivity로 이동
            val comment = "사용자의 댓글" // 여기에 사용자의 댓글을 가져오세요.
            val intent = Intent(requireContext(), ReviewDialogActivity::class.java).apply {
                putExtra("comment", comment)
            }
            startActivity(intent)
        }


        return view
    }
}
