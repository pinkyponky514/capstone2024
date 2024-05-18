package com.example.capstone2024

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.ChatActivity
import com.example.reservationapp.MainActivity
import com.example.reservationapp.Model.ChatItem
import com.example.reservationapp.R

private var chatArray = ArrayList<ChatItem>()


//최근 검색단어 Adapter
class ChattingAdapter:
    RecyclerView.Adapter<ChattingAdapter.ViewHolder>() {
    //private lateinit var userName: String
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var chat_textview: TextView //채팅 텍스트뷰

        init {
            //userName = MainActivity().userName
            chat_textview = itemView.findViewById(R.id.chatTextView)
        }

        fun setContents(chat: ChatItem) {
            chat_textview.text = chat.text
        }
    }

    override fun getItemViewType(position: Int): Int {
        //if(chatArray[position].user.equals(MainActivity().userName)) {
        if(chatArray[position].user.equals("hansung")) {
            return 1; //나의 메세지
        } else {
            return 0; //AI 메세지
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_left, viewGroup, false)

        if(viewType.equals(1)) {
            layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_right, viewGroup, false)
        }
        return ViewHolder(layoutInflater)
    }

    //ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: ChattingAdapter.ViewHolder, position: Int) {
        holder.setContents(chatArray[position])
    }
    override fun getItemCount(): Int {
        return chatArray.size
    }

    //
    fun updateList(newList: ArrayList<ChatItem>) {
        chatArray = newList
        notifyDataSetChanged()
    }

}