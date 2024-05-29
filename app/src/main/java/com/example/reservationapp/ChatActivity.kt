package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone2024.ChattingAdapter
import com.example.reservationapp.Model.ChatItem
import com.example.reservationapp.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var adapter: ChattingAdapter
    private lateinit var chatList: ArrayList<ChatItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChattingAdapter()

        val recyclerView = binding.chatRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        chatList = ArrayList()
        chatList.add(ChatItem("AI", "안녕하세요. 캐치닥터 챗봇입니다. 당신의 증상을 알려주세요. 캐치닥터 챗봇이 진료과목을 추천해드립니다!"))
        chatList.add(ChatItem("hansung", "머리가 아프고, 기침이 나와. 어디로 가야하니?"))
        chatList.add(ChatItem("AI", "당신의 증상에 대해 캐치닥터 챗봇이 추천하는 진료과목은 '내과'입니다."))
        chatList.add(ChatItem("AI", "현 위치에서 가까운 내과를 추천드리겠습니다."))
        chatList.add(ChatItem("AI", null, R.drawable.ex_hospital))
//        chatList.add(ChatItem("AI", "빨리 쾌차하시기 바랍니다. 좋은 하루 되세요 ~"))

        adapter.updateList(chatList)

        val messageEditText = binding.messageEditText
        val sendButton = binding.sendButton

        sendButton.isEnabled = false

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    sendButton.isEnabled = true
                    sendButton.setBackgroundResource(R.drawable.rounded_button_active)
                } else {
                    sendButton.isEnabled = false
                    sendButton.setBackgroundResource(R.drawable.rounded_button_inactive)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        sendButton.setOnClickListener {
            val messageString = messageEditText.text.toString()
            if (messageString.trim().isNotEmpty()) {
                chatList.add(ChatItem("hansung", messageString))
                adapter.updateList(chatList)
                messageEditText.setText("")
            } else {
                Log.w("Message Send Error", "$messageString")
            }
            recyclerView.scrollToPosition(chatList.size - 1)
        }

        val backIcon: ImageView = findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
