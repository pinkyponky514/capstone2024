package com.example.reservationapp.Custom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.reservationapp.R
import com.example.reservationapp.databinding.ItemCustomToastBinding

@SuppressLint("MissingInflatedId")
class CustomToast(context: Context, message: String) : Toast(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.item_custom_toast, null)
        view.findViewById<TextView>(R.id.textView_toast).apply {text = message }
        setView(view)
        duration = LENGTH_SHORT
    }
}
