package com.example.reservationapp.Adapter

import android.media.Rating
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.R


private var review_list_data = ArrayList<ReviewItem>()

class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var ratingBar: RatingBar //별점개수
        private var starScoreTextView: TextView //별점
        private var commentTextView: TextView //리뷰내용
        private var reviewDateTextView: TextView //날짜
        private var userIdTextView: TextView //유저이름

        init {
            ratingBar = itemView.findViewById(R.id.ratingBar)
            starScoreTextView = itemView.findViewById(R.id.starScore_textView)
            commentTextView = itemView.findViewById(R.id.comment_textView)
            reviewDateTextView = itemView.findViewById(R.id.reviewDate_textView)
            userIdTextView = itemView.findViewById(R.id.userId_textView)

            itemView.setOnClickListener {

            }
        }


        //데이터 설정
        fun setContents(list: ReviewItem) {
            ratingBar.rating = list.starScore.toFloat()
            starScoreTextView.text = list.starScore
            commentTextView.text = list.comment
            reviewDateTextView.text = list.reviewDate
            userIdTextView.text = list.userId
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_review, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    //ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(review_list_data[position])
    }
    override fun getItemCount(): Int {
        return review_list_data.size
    }

    //
    fun updatelist(newList: ArrayList<ReviewItem>) {
        review_list_data = newList
        Log.w("ReviewAdapter", "newList: $newList, updateList: $review_list_data")
        notifyDataSetChanged()
    }

}