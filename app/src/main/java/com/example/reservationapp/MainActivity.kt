package com.example.reservationapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.reservationapp.SignUpActivity
import com.example.reservationapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var mContext: Context
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_main)

        mContext = this //다른 class에서 main class의 함수를 쓸 수 있도록

        //네비게이션바 선택에 따른 화면전환
        binding.navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
             R.id.moreFrag -> { //더보기 버튼 클릭했을때
                 //setActivity(mContext, RegisterActivity())
                 val intent = Intent(this, HPDivisonActivity::class.java)
                 startActivity(intent)
                 finish()
                }
            }
            true
        }
    }

    //액티비티 전환 사용자정의 함수
    fun setActivity(T:Any, activity: Any) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
        finish()
    }
}


/*
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
*/