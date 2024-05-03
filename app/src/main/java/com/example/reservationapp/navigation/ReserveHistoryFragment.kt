package com.example.reservationapp.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reservationapp.R
import com.example.reservationapp.databinding.FragmentReserveHistoryBinding


//현재 진행중, 예약 진료내역
class ReserveHistoryFragment : Fragment() {
    private lateinit var binding: FragmentReserveHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReserveHistoryBinding.inflate(inflater)

        return binding.root
    }
}