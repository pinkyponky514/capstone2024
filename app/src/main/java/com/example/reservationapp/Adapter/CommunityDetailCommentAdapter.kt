package com.example.reservationapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.CommentItem
import com.example.reservationapp.Model.CommunityItem
import com.example.reservationapp.R

class CommunityDetailCommentAdapter(
    private val items: MutableList<Any> = mutableListOf(),
    private val onItemClick: (Int) -> Unit = {},
    private val onItemDelete: (Int) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_COMMUNITY = 0
        private const val TYPE_COMMENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CommunityItem -> TYPE_COMMUNITY
            is CommentItem -> TYPE_COMMENT
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_COMMUNITY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
                CommunityViewHolder(view)
            }
            TYPE_COMMENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
                CommentViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CommunityItem -> (holder as CommunityViewHolder).bind(item)
            is CommentItem -> (holder as CommentViewHolder).bind(item)
        }
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addComment(comment: CommentItem) {
        items.add(comment)
        notifyItemInserted(items.size - 1)
    }

    fun removeComment(position: Int) {
        if (items[position] is CommentItem) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val writer: TextView = itemView.findViewById(R.id.writer)
        private val likes: TextView = itemView.findViewById(R.id.likes)
        private val reviews: TextView = itemView.findViewById(R.id.reviews)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)

        fun bind(item: CommunityItem) {
            imageView.setImageBitmap(item.imageResource)
            title.text = item.title
            writer.text = item.writer
            likes.text = item.likes
            reviews.text = item.reviews
            timestamp.text = item.timestamp
        }
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.commentContent)
        private val authorTextView: TextView = itemView.findViewById(R.id.commentAuthor)
        private val timestampTextView: TextView = itemView.findViewById(R.id.commentTimestamp)
        private val deleteButton: Button = itemView.findViewById(R.id.commentDeleteButton)

        fun bind(item: CommentItem) {
            contentTextView.text = item.content
            authorTextView.text = item.author
            // 댓글 작성 시간을 timestampTextView에 표시
            timestampTextView.text = item.timestamp
            deleteButton.setOnClickListener {
                onItemDelete(adapterPosition)
            }
        }
    }
}
