package com.example.reservationapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.ReviewItem
import com.example.reservationapp.R

class ReviewAdapter(private val reviews: MutableList<ReviewItem> = mutableListOf()) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.contentTextView)
        val writerTextView = itemView.findViewById<TextView>(R.id.writerTextView)
        val timestampTextView = itemView.findViewById<TextView>(R.id.timestampTextView)

        fun bind(review: ReviewItem) {
            titleTextView.text = review.title
            writerTextView.text = review.writer
            timestampTextView.text = review.timestamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun addComment(comment: ReviewItem) {
        reviews.add(comment)
        notifyDataSetChanged()
    }
}
