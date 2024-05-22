package com.example.capstone2024

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.ChatItem
import com.example.reservationapp.R

private var chatArray = ArrayList<ChatItem>()

// 최근 검색단어 Adapter
class ChattingAdapter :
    RecyclerView.Adapter<ChattingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var chatTextView: TextView
        private var chatImageView: ImageView?

        init {
            chatTextView = itemView.findViewById(R.id.chatTextView)
            chatImageView = itemView.findViewById(R.id.chatImageView)
        }

        fun setContents(chat: ChatItem) {
            chatTextView.text = chat.text
            chat.imageResource?.let { resourceId ->
                chatImageView?.apply {
                    setImageResource(resourceId)
                    visibility = View.VISIBLE
                }
            } ?: run {
                chatImageView?.visibility = View.GONE
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (chatArray[position].user == "hansung") {
            1 // 나의 메세지
        } else {
            0 // AI 메세지
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val layoutResId = if (viewType == 1) {
            R.layout.item_chat_right
        } else {
            R.layout.item_chat_left
        }
        val itemView = layoutInflater.inflate(layoutResId, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(chatArray[position])
    }

    override fun getItemCount(): Int {
        return chatArray.size
    }

    fun updateList(newList: ArrayList<ChatItem>) {
        chatArray = newList
        notifyDataSetChanged()
    }
}
