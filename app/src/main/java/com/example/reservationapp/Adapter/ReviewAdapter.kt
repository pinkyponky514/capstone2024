package com.example.reservationapp.Adapter

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
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalReviewAllResponse
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.Model.UserReservationResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
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
            //userId = 0

            ratingBar = itemView.findViewById(R.id.ratingBar)
            starScoreTextView = itemView.findViewById(R.id.starScore_textView)
            commentTextView = itemView.findViewById(R.id.comment_textView)
            reviewDateTextView = itemView.findViewById(R.id.reviewDate_textView)
            userIdTextView = itemView.findViewById(R.id.userId_textView)
            deleteButton = itemView.findViewById(R.id.review_delete_ImageView)

            itemView.setOnClickListener {

            }
        }


        //데이터 설정
        fun setContents(list: ReviewItem) {
            hospitalId = list.hospitalId
            deleteButton.visibility = View.GONE

            //리뷰 레이아웃에 값 설정해주기
            apiService.getHospitalReviewAll(hospitalId).enqueue(object: Callback<List<HospitalReviewAllResponse>> {
                override fun onResponse(call: Call<List<HospitalReviewAllResponse>>, response: Response<List<HospitalReviewAllResponse>>) {
                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        responseBodyReviewALl = response.body()!!

                        for(review in responseBodyReviewALl) {
                            if(review.reviewId == list.reviewId) { //전달받은 리뷰 아이디와 response로 받은 리뷰 아이디가 같으면
                                reservationId = review.reservationId
                                reviewId = review.reviewId
                                ratingBar.rating = review.grade
                                starScoreTextView.text = review.grade.toString()
                                commentTextView.text = review.comment
                                reviewDateTextView.text = review.registerDate.toString()
                                userIdTextView.text = review.userName
                                break
                            }
                        }
                        //
                    }

                    //통신 성공, 응답 실패
                    else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("ReviewAdapter FAILURE Response", "List<HospitalReviewAllResponse> Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                        if (errorBody != null) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                val timestamp = jsonObject.optString("timestamp")
                                val status = jsonObject.optInt("status")
                                val error = jsonObject.optString("error")
                                val message = jsonObject.optString("message")
                                val path = jsonObject.optString("path")

                                Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                            } catch (e: JSONException) {
                                Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                            }
                        }
                    }
                }

                //통신 실패
                override fun onFailure(call: Call<List<HospitalReviewAllResponse>>, t: Throwable) {
                    Log.w("ReviewAdapter CONNECTION FAILURE: ", "ReviewAll CONNECT FAILURE : ${t.localizedMessage}")
                }

            })

            //내가 쓴 리뷰면 삭제버튼 나타나게
            apiService.getUserReservation().enqueue(object: Callback<List<UserReservationResponse>> {
                override fun onResponse(call: Call<List<UserReservationResponse>>, response: Response<List<UserReservationResponse>>) {
                    //통신, 응답 성공
                    if(response.isSuccessful) {
                        responseBodyUserReservation = response.body()!!

                        for(reservation in responseBodyUserReservation) {
                            if(reservation.reservationId == reservationId && reservation.status == "진료완료" && reservation.reviewWriteBoolean) { //진료완료, 리뷰를 쓴 상태이고, response로 받아온 예약 아이디와 현재 저장되어 있는 예약 아이디가 같으면
                                deleteButton.visibility = View.VISIBLE

                                //리뷰 삭제버튼 onClick
                                deleteButton.setOnClickListener {
                                    try {
                                        val reservations = apiService.deleteReview(reviewId, reservationId)
                                        // 결과 처리
                                        Log.w("ReviewAdapter", "List<UserReservationResponse>: $reservations")
                                    } catch (e: Exception) {
                                        // 오류 처리
                                        val errorBody = response.errorBody()?.string()
                                        Log.d("ReviewAdapter FAILURE Response", "List<UserReservationResponse> 오류 Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                                        if (errorBody != null) {
                                            try {
                                                val jsonObject = JSONObject(errorBody)
                                                val timestamp = jsonObject.optString("timestamp")
                                                val status = jsonObject.optInt("status")
                                                val error = jsonObject.optString("error")
                                                val message = jsonObject.optString("message")
                                                val path = jsonObject.optString("path")

                                                Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                                            } catch (e: JSONException) {
                                                Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                                            }
                                        }
                                    }
                                }
                                break
                            }
                        }
                    }

                    //통신 성공, 응답 실패
                    else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("ReviewAdapter FAILURE Response", "List<UserReservationResponse> 통신 성공, 응답실패 Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                        if (errorBody != null) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                val timestamp = jsonObject.optString("timestamp")
                                val status = jsonObject.optInt("status")
                                val error = jsonObject.optString("error")
                                val message = jsonObject.optString("message")
                                val path = jsonObject.optString("path")

                                Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                            } catch (e: JSONException) {
                                Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                            }
                        }
                    }
                }

                //통신 실패
                override fun onFailure(call: Call<List<UserReservationResponse>>, t: Throwable) {
                    Log.w("ReviewAdapter CONNECTION FAILURE: ", " UserReservation 통신 실패 FAILURE : ${t.localizedMessage}")
                }

            })

            //리뷰 삭제버튼 onClick
            deleteButton.setOnClickListener {
            }
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
