//ReviewAdapter

package com.example.reservationapp.Adapter

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.App
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalReviewAllResponse
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Model.UserReservationResponse
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var review_list_data = ArrayList<ReviewItem>()

class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    @RequiresApi(Build.VERSION_CODES.O)
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var ratingBar: RatingBar //별점개수
        private var starScoreTextView: TextView //별점
        private var commentTextView: TextView //리뷰내용
        private var reviewDateTextView: TextView //날짜
        private var userIdTextView: TextView //유저이름
        private var deleteButton: ImageView //삭제 버튼

        private var hospitalId: Long //병원 레이블 번호
        private var reviewId: Long //리뷰 레이블 번호
        private var reservationId: Long //예약 레이블 번호

        //Retrofit
        private var retrofitClient: RetrofitClient = RetrofitClient.getInstance()
        private var apiService: APIService = retrofitClient.getRetrofitInterface()
        private lateinit var responseBodyReviewALl: List<HospitalReviewAllResponse>
        private lateinit var responseBodyUserReservation: List<UserReservationResponse>

        init {
            hospitalId = 0
            reviewId = 0
            reservationId = 0

            ratingBar = itemView.findViewById(R.id.hospital_list_ratingBar)
            starScoreTextView = itemView.findViewById(R.id.starScore_textView)
            commentTextView = itemView.findViewById(R.id.comment_textView)
            reviewDateTextView = itemView.findViewById(R.id.reviewDate_textView)
            userIdTextView = itemView.findViewById(R.id.userId_textView)
            deleteButton = itemView.findViewById(R.id.review_delete_ImageView)

            deleteButton.visibility = View.GONE

            //삭제 버튼 누르면
            deleteButton.setOnClickListener {
                apiService.deleteReview(reviewId, reservationId).enqueue(object: Callback<Int> {
                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        if(response.isSuccessful) {
                            Log.w("ReviewAdapter", "리뷰 삭제 함 response.body() = ${response.body()}")
                        }
                        else handleErrorResponse(response)
                    }

                    override fun onFailure(call: Call<Int>, t: Throwable) {

                    }
                })
            }
        }


        //데이터 설정
        fun setContents(list: ReviewItem) {
            hospitalId = list.hospitalId
            reviewId = list.reviewId

            //리뷰 레이아웃에 값 설정해주기
            apiService.getHospitalReviewAll(hospitalId).enqueue(object: Callback<List<HospitalReviewAllResponse>> {
                override fun onResponse(call: Call<List<HospitalReviewAllResponse>>, response: Response<List<HospitalReviewAllResponse>>) {
                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        responseBodyReviewALl = response.body()!!

                        for(review in responseBodyReviewALl) {
                            //if(review.reviewId == list.reviewId) { //전달받은 리뷰 아이디와 response로 받은 리뷰 아이디가 같으면
                            if(review.reviewId == reviewId) { //전달받은 리뷰 아이디와 response로 받은 리뷰 아이디가 같으면
                                Log.w("ReviewAdapter", "병원 모든 리뷰 가져오기 list.reviewId = ${list.reviewId}, review.reviewId = ${review.reviewId}, reviewId = $reviewId")
                                reservationId = review.reservationId
                                //reviewId = review.reviewId
                                ratingBar.rating = review.grade
                                starScoreTextView.text = review.grade.toString()
                                commentTextView.text = review.comment
                                reviewDateTextView.text = review.registerDate.toString()
                                userIdTextView.text = review.userName

                                Log.w("ReviewAdapter", "if문 마지막 줄 review.reviewId = $reviewId")

                                if(App.prefs.token != null) {
                                    GlobalScope.launch {
                                        val responseBodyReservation = ArrayList<UserReservationResponse>()
                                        val response = apiService.getUserReservation().execute()

                                        if(response.isSuccessful) {
                                            response.body()?.let {
                                                for(i in it.indices) {
                                                    responseBodyReservation.add(it[i])
                                                }
                                            }
                                            Log.w("ReviewAdpater", "GlobalScope responseBodyReservation : $responseBodyReservation")

                                            for(reservation in responseBodyReservation) {
                                                if (reservation.reservationId == review.reservationId && reservation.status == "진료완료" && reservation.reviewWriteBoolean) {
                                                    Log.w("ReviewAdapter", "내가 쓴 리뷰 reviewId = $reviewId, reservationId = $reservationId, GlobalScope response body = $responseBodyUserReservation")
                                                    deleteButton.visibility = View.VISIBLE
                                                }
                                            }
                                        }
                                    }
                                }

                                //
                                break
                            }
                        }

                        //
                    }

                    //통신 성공, 응답 실패
                    else handleErrorResponse(response)
                }

                //통신 실패
                override fun onFailure(call: Call<List<HospitalReviewAllResponse>>, t: Throwable) {
                    Log.w("ReviewAdapter CONNECTION FAILURE: ", "getHospitalReviewAll CONNECT FAILURE : ${t.localizedMessage}")
                }

            })

        }


        //
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_review, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    //ViewHolder에 데이터 연결
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(review_list_data[position])
    }
    override fun getItemCount(): Int {
        return review_list_data.size
    }

    //
    fun updatelist(newList: ArrayList<ReviewItem>) {
        review_list_data = newList// as ArrayList<ReviewItem>
        notifyDataSetChanged()
    }

}
