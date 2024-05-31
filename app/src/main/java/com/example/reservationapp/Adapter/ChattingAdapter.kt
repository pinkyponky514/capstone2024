package com.example.capstone2024

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.ChatItem
import com.example.reservationapp.R

private var chatArray = ArrayList<ChatItem>()

// 최근 검색단어 Adapter
class ChattingAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var itemClickListener: ((ChatItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (ChatItem) -> Unit) {
        itemClickListener = listener
    }

    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var chatTextView: TextView
        private var chatImageView: ImageView?
        private var chatProgressBar: ProgressBar?
        init {
            chatTextView = itemView.findViewById(R.id.chatTextView)
            chatImageView = itemView.findViewById(R.id.chatImageView)
            chatProgressBar = itemView.findViewById(R.id.chatprogressBar)

            itemView.setOnClickListener {
                itemClickListener?.invoke(chatArray[adapterPosition])
            }
        }

        fun setContents(chat: ChatItem) {
            if (chat.isLoading) {
                chatTextView.visibility = View.GONE
                chatImageView?.visibility = View.GONE
                chatProgressBar?.visibility = View.VISIBLE
            } else {
                chatProgressBar?.visibility = View.GONE
                chatTextView.visibility = View.VISIBLE
                chatTextView.text = chat.text
                chat.imageResource?.let { resourceId ->
                    chatImageView?.apply {
                        setImageBitmap(resourceId)
                        visibility = View.VISIBLE
                    }
                } ?: run {
                    chatImageView?.visibility = View.GONE
                }
            }
        }
    }

    inner class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageView: ImageView = itemView.findViewById(R.id.imageView)
        private var hospitalNameTextView: TextView = itemView.findViewById(R.id.textView_hospital_name)
        private var hospitalPosTextView: TextView = itemView.findViewById(R.id.textView_hospital_pos)

        init {
            itemView.setOnClickListener {
                itemClickListener?.invoke(chatArray[adapterPosition])
            }
        }

        fun setContents(chat: ChatItem) {
            // 이 부분에서 실제 병원 이름과 위치 정보를 설정하도록 변경
            if (chat.imageResource != null) {
                imageView.setImageBitmap(chat.imageResource)
                imageView.visibility = View.VISIBLE
            } else {
                imageView.visibility = View.GONE
            }
            hospitalNameTextView.text =   chat.hospitalName ?: "Unknown Hospital" // 여기를 실제 데이터로 변경
            hospitalPosTextView.text =  chat.hospitalName ?: "Unknown Hospital"  // 여기를 실제 데이터로 변경
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatArray[position].user == "hansung") {
            1 // 나의 메세지
        } else if (chatArray[position].imageResource != null || chatArray[position].hospitalName != null) {
            2 // 병원 정보
        } else {
            0 // AI 메세지
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return when (viewType) {
            1 -> {
                val itemView = layoutInflater.inflate(R.layout.item_chat_right, viewGroup, false)
                MessageViewHolder(itemView)
            }
            2 -> {
                val itemView = layoutInflater.inflate(R.layout.item_chat_hospital, viewGroup, false)
                HospitalViewHolder(itemView)
            }
            else -> {
                val itemView = layoutInflater.inflate(R.layout.item_chat_left, viewGroup, false)
                MessageViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder -> {
                holder.setContents(chatArray[position])
                holder.itemView.setOnClickListener {
                    itemClickListener?.invoke(chatArray[position])
                }
            }
            is HospitalViewHolder -> {
                holder.setContents(chatArray[position])
                holder.itemView.setOnClickListener {
                    itemClickListener?.invoke(chatArray[position])
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return chatArray.size
    }

    fun updateList(newList: ArrayList<ChatItem>) {
        chatArray = newList
        notifyDataSetChanged()
    }
}
