package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.databinding.ActivityCheckReservationBinding

//예약 확인 액티비티
class CheckReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckReservationBinding

    private lateinit var hospitalNameTextView: TextView
    private lateinit var classNameTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var reservationDateTextView: TextView
    private lateinit var reservationTimeTextView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        hospitalNameTextView = binding.textViewHospitalName
        classNameTextView = binding.textViewClassName
        statusTextView = binding.textViewReservationStatus
        reservationDateTextView = binding.textViewReservationDate
        reservationTimeTextView = binding.textViewReservationTime



        val intentDataItem = intent.getSerializableExtra("reserveDataItem") as HistoryItem

        hospitalNameTextView.text = intentDataItem.hospitalName
        classNameTextView.text = intentDataItem.className
        statusTextView.text = intentDataItem.status
        reservationDateTextView.text = intentDataItem.reserveDay
        reservationTimeTextView.text = intentDataItem.reserveTime
    }


    //뒤로가기 버튼 눌렀을때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) // 지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 인텐트 플래그 설정
        startActivity(intent) // 인텐트 이동
        finish() // 현재 액티비티 종료
    }

}