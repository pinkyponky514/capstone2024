package com.example.reservationapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.ReviewAdapter
import com.example.reservationapp.Model.ReviewItem

class ReviewDialogActivity : AppCompatActivity() {

    private lateinit var reviewRecyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton: Button
    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_dialog)

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView)
        editText = findViewById(R.id.editText)
        sendButton = findViewById(R.id.sendButton)

        adapter = ReviewAdapter()
        reviewRecyclerView.adapter = adapter
        reviewRecyclerView.layoutManager = LinearLayoutManager(this)

        sendButton.setOnClickListener {
            val commentContent = editText.text.toString().trim()
            if (commentContent.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                val formattedTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", currentTime).toString()
                val comment = ReviewItem(commentContent, "작성자", formattedTime) // 작성자 정보는 나중에 변경 가능
                adapter.addComment(comment)
                editText.text.clear()
            }
        }
    }
}
