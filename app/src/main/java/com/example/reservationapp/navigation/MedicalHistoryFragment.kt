package com.example.reservationapp.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reservationapp.MainActivity
import com.example.reservationapp.R
import com.example.reservationapp.databinding.FragmentMedicalHistoryBinding

//진료내역 프래그먼트
class MedicalHistoryFragment : Fragment() {

    private lateinit var binding: FragmentMedicalHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMedicalHistoryBinding.inflate(inflater)


        setFragment(PastHistoryFragment())
        //과거 진료내역, 예약 내역 네비게이션바 선택에 따른 화면전환
        binding.topNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.past_history -> { //과거 진료내역 클릭했을 때
                    setFragment(PastHistoryFragment())
                }
                R.id.reserve_history -> { //진행중인, 예약 진료내역 클릭했을 때
                    setFragment(ReserveHistoryFragment())
                }
            }
            true
        }

        return binding.root
    }

    fun setFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.medical_history_main_content, fragment).commit()
    }
}