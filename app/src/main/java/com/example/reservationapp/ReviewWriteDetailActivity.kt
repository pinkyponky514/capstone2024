package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Model.filterList
import com.example.reservationapp.databinding.ActivityReviewWriteDetailBinding
import com.example.reservationapp.navigation.pastHistoryList
import java.util.Calendar

class ReviewWriteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewWriteDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewWriteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //초기화
        val hospital_name_textView = binding.hospitalName
        val reserve_date_textView = binding.reserveDate
        val class_name_textView = binding.className

        val rating_bar = binding.reviewWriteDetailRatingBar
        val review_content_editText = binding.reviewContentEditText

        val cancel_button = binding.cancelButton
        val register_button = binding.registerButton

        //전달받은 값
        val hospital_name_string = intent.getStringExtra("hospitalName").toString()
        val reserve_date_string = intent.getStringExtra("reserveDate").toString()
        val class_name_string = intent.getStringExtra("className").toString()

        hospital_name_textView.text = hospital_name_string
        reserve_date_textView.text = reserve_date_string
        class_name_textView.text = class_name_string


        //등록버튼 눌렀을때 onClick
        register_button.setOnClickListener {
            for(i in filterList.indices) {
                if(filterList[i].hospitalName == hospital_name_string) { //넘겨받은 병원이름을 DB 병원 리스트에서 검색, 같으면 if문 수행하고 중단
                    val rating_count = rating_bar.rating //별점
                    val review_content = review_content_editText.text.toString() //리뷰내용

                    val calendar = Calendar.getInstance()
                    val currentYear = calendar.get(Calendar.YEAR)
                    val currentMonth = calendar.get(Calendar.MONTH)+1
                    val currentDay = calendar.get(Calendar.DATE)
                    Log.w("ReviewWrtieDetailActivity", "year: $currentYear, month: $currentMonth, day: $currentDay")
                    //val dayOfWeek = getDayOfWeek(currentYear, currentMonth, currentDay) //현재 요일 구하기

                    val date = "$currentYear.$currentMonth.$currentDay"//리뷰 쓴 날짜
                    val username = "hansung" //리뷰 쓴 사용자 이름

                    val review = ReviewItem(rating_count.toString(), review_content, date, username)
                    Log.w("ReviewWrtieDetailActivity", "review: $review")

                    //DB에 리뷰 추가하기
                    Log.w("ReviewWriteDetailActivity", "리뷰추가:  ${filterList[i].reviewList}")
                    filterList[i].reviewList.add(review)
                    Log.w("ReviewWriteDetailActivity", "리뷰추가:  ${filterList[i].reviewList}")


                    //과거진료내역 리뷰쓰기버튼 리뷰작성완료로 변경
                    for(j in pastHistoryList.indices) {
                        if(pastHistoryList[j].hospitalName == hospital_name_string) {
                            pastHistoryList[j].reviewWriteBoolean = true

                            //등록하고 액티비티 전환
                            val intent = Intent(this, ReviewWrtieCompletedActivity::class.java)
                            startActivity(intent)
                            finish()

                            break
                        }
                    }

                    break
                }
            }
        }

        //취소버튼 눌렀을때 onClick
        cancel_button.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        //
    }

    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // 현재 액티비티 종료
    }


    //
}