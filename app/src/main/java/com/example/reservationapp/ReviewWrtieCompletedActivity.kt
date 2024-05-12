package com.example.reservationapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reservationapp.databinding.ActivityReviewWrtieCompletedBinding

class ReviewWrtieCompletedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewWrtieCompletedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewWrtieCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val check_button = binding.checkButton
        check_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //intent가 있으면 지우고 맨위로 옮김
            startActivity(intent)
            finish()
        }
    }

    //
}