package com.example.reservationapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.ReviewAdapter
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.databinding.ActivityHospitalDetailpageExampleAddBinding

class Hospital_DetailPage : AppCompatActivity() {
    private lateinit var binding: ActivityHospitalDetailpageExampleAddBinding

    private var not_review_constraint_flag: Boolean = false //리뷰 개수가 0일때의 constraintLayout flag
    private var review_constraint_flag: Boolean = false //리뷰가 있을 경우 constraintLayout flag

    private lateinit var notReviewConstraintLayout: ConstraintLayout //리뷰가 없을때 constraintLayout
    private lateinit var reviewConstraintLayout: RecyclerView //리뷰가 있을때 constraintLayout

    private lateinit var adapter: ReviewAdapter
    private lateinit var reviewList: ArrayList<ReviewItem>

    //DB에서 가져온 데이터 넣을 view
    private lateinit var hospitalNameTextView: TextView //병원이름
    private lateinit var classNameTextView: TextView //진료과명
    private lateinit var statusTextView: TextView //진료가능 상태
    private lateinit var waitCountTextView: TextView //대기인원
    private lateinit var hospitalPositionTextView: TextView //병원위치
    private lateinit var todayTimeTextView: TextView //금일 운영시간
    private lateinit var hospitalCallTextView: TextView //병원 연락처
    private lateinit var reservationButton: Button //예약 버튼

    private var reviewCount: Int = 0 //리뷰개수


    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalDetailpageExampleAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //넘겨받은 병원이름으로 DB에서 데이터 가져올 것임
        val hospitalName = intent.getStringExtra("hospitalName").toString()
        val className = binding.textViewClassName.text.toString()
        hospitalNameTextView = binding.textViewHospitalName
        hospitalNameTextView.text = hospitalName
        Log.w("GetExtra Hospital Name", "GetExtra hospital Name: ${hospitalName}")


        // 예약 버튼 클릭 이벤트 설정
        reservationButton = binding.ReservationButton
        reservationButton.setOnClickListener {
            val dialog = CustomReserveDialogActivity.newInstance(hospitalName, className)
            dialog.show(supportFragmentManager, "CustomReserveDialog")
        }


        //리뷰 설정
        adapter = ReviewAdapter()

        val recyclerView = binding.reviewRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

        //리뷰 목록 리스트 초기화 (DB에서 가져오기)
        reviewList = ArrayList()
        reviewList.add(ReviewItem("4.0", "병원이 너무 좋아요", "2024.04.01", "hansung"))
        reviewList.add(ReviewItem("3.0", "솔직하게 말하자면 병원이 크고 시설도 다 좋은데, 의사 선생님이나 간호사 분들이 너무 불친절합니다.", "2024.04.05", "user1"))
        reviewList.add(ReviewItem("4.5", "다닐만 합니다", "2024.04.20", "user2"))
        reviewList.add(ReviewItem("2.0", "대기 시간도 너무 길고 신설 병원이라 그런지 너무 체계가 엉망입니다", "2024.05.01", "user3"))
        reviewList.add(ReviewItem("4.0", "병원이 너무 좋아요", "2024.05.03", "user4"))
        reviewList.add(ReviewItem("4.5", "병원이 너무 좋아요", "2024.05.05", "user5"))

        //
        reviewCount = reviewList.size
        val reviewCountTextView = binding.reviewCountTextView
        reviewCountTextView.text = reviewCount.toString() //리뷰개수 textView에 표시

        notReviewConstraintLayout = binding.notReviewConstraintLayout //리뷰가 없을 경우 constraintLayout
        reviewConstraintLayout = binding.reviewRecyclerView //리뷰가 있을 경우 constraintLayout
        notReviewConstraintLayout.visibility = View.GONE
        reviewConstraintLayout.visibility = View.GONE

        if(reviewCount == 0) { //리뷰가 하나도 없을 경우
            not_review_constraint_flag = true
            notReviewConstraintLayout.visibility = View.VISIBLE

            review_constraint_flag = false
            reviewConstraintLayout.visibility = View.GONE

        } else { //리뷰가 한개이상 있을 경우
            review_constraint_flag = true
            reviewConstraintLayout.visibility = View.VISIBLE

            not_review_constraint_flag = false
            notReviewConstraintLayout.visibility = View.GONE

            adapter.updatelist(reviewList)
            recyclerView.suppressLayout(true) //스트롤 불가능
        }
    }

    override fun onResume() {
        super.onResume()
    }

    //int를 dp로 바꾸기
    fun changeDP(value : Int) : Int{
        var displayMetrics = resources.displayMetrics
        var dp = (value * displayMetrics.density).toInt() //var dp = Math.round(value * displayMetrics.density)
        return dp
    }
    //
}
