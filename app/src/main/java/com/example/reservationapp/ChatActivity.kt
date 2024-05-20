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

// 챗gpt 채팅 페이지 액티비티
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var adapter: ChattingAdapter
    private lateinit var chatList: ArrayList<ChatItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        adapter = ChattingAdapter()

        // 채팅 recyclerView
        val recyclerView = binding.chatRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true // 가장 최근 대화 맨 아래로 정렬
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager

        // 주고 받은 채팅 임의의 데이터 초기화
        chatList = ArrayList()
        chatList.add(ChatItem("AI", "안녕하세요. 캐치닥터 챗봇입니다. 당신의 증상을 알려주세요. 캐치닥터 챗봇이 진료과목을 추천해드립니다!"))
        chatList.add(ChatItem("hansung", "머리가 아프고, 기침이 나와. 어디로 가야하니?"))
        chatList.add(ChatItem("AI", "해당 증상에 대한 진료과목은 '내과'입니다."))
        chatList.add(ChatItem("hansung", "그러면 병원 좀 추천해줘"))
        chatList.add(ChatItem("AI", "현 위치에서 가까운 병원을 추천드리겠습니다."))
        chatList.add(ChatItem("AI",null, R.drawable.ex_hospital))
        chatList.add(ChatItem("AI", "윤홍선내과가 제일 가까운 병원으로 파악됩니다."))
        chatList.add(ChatItem("AI", "빨리 쾌차하시기 바랍니다. 좋은 하루 되세요 ~"))

        adapter.updateList(chatList)

        // 메세지 보내기
        val messageEditText = binding.messageEditText
        val sendButton = binding.sendButton

        sendButton.isEnabled = false // 초기 상태에서 비활성화

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

        // 아이콘 클릭 이벤트 설정
        val backIcon: ImageView = findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Optional: if you want to finish the current activity
        }
    }
}
