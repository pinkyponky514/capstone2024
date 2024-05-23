package com.example.reservationapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reservationapp.databinding.ActivityNotAbleReviewWriteBinding
import java.util.zip.Inflater

class NotAbleReviewListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotAbleReviewWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotAbleReviewWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}