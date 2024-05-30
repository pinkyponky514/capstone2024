package com.example.reservationapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.alpha
import androidx.fragment.app.Fragment
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.databinding.ActivityMainBinding
import com.example.reservationapp.navigation.CommunityFragment
import com.example.reservationapp.navigation.HomeFragment
import com.example.reservationapp.navigation.MedicalHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback

/*
var userMapx: Double = App.mylat //사용자 위도
var userMapy: Double = App.mylng //사용자 경도
*/
var userMapx: Double = 37.5 //사용자 위도
var userMapy: Double = 126.9 //사용자 경도

//메인메뉴
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var searchRecentWordList = ArrayList<RecentItem>()
    lateinit var navigation: BottomNavigationView

    private lateinit var classBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var symptomBottomSheetBehavior: BottomSheetBehavior<View>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_main)

        tokenCheck() //토큰 만료됐는지 확인


        if(App.prefs.token != null) {
        Log.w("MainActivity", "App.prefs.token: ${App.prefs.token}")
        }

        //최근검색어
        //searchRecentWordList = intent.getSerializableExtra("searchWordList") as? ArrayList<RecentItem> ?: ArrayList()


        //처음 실행시 홈 선택으로 시작
        navigation = binding.navigation
        navigation.selectedItemId = R.id.homeFrag
        setFragment(HomeFragment())


        //네비게이션바 선택에 따른 화면전환
        navigationSetItem()

        //뒷배경
        val backgroundView = binding.backgroundView
        val bottomSheetCallback = object: BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //상태가 EXPANDED, COLLAPSED일때 뒷배경 불투명
                if(newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_EXPANDED) {
                    backgroundView.visibility = View.VISIBLE
                } else {
                    backgroundView.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if(-1<slideOffset || slideOffset<=1) {
                    backgroundView.visibility = View.VISIBLE
                } else {
                    backgroundView.visibility = View.GONE
                }
            }
        }


        //class Bottom Sheet 초기화
        val classBottomSheetView = binding.classBottomSheet
        classBottomSheetBehavior = BottomSheetBehavior.from(classBottomSheetView)
        classBottomSheetBehavior.isHideable = true //시작할때 숨김
        classBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        classBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        //symptom Bottom Sheet 초기화
        val symptomBottomSheetView = binding.symptomBottomSheet
        symptomBottomSheetBehavior = BottomSheetBehavior.from(symptomBottomSheetView)
        symptomBottomSheetBehavior.isHideable = true
        symptomBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        symptomBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)


        //배경 클릭했을때 Bottom Sheet 사라지게
        backgroundView.setOnClickListener {
            classBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            symptomBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        //Bottom Sheet 버튼 onClcik
        val classButtonList =
            listOf(binding.classButton1, binding.classButton2, binding.classButton3, binding.classButton4,
                binding.classButton5, binding.classButton6, binding.classButton7, binding.classButton8)
        val symptomButtonList =
            listOf(binding.symptomButton1, binding.symptomButton2, binding.symptomButton3, binding.symptomButton4,
                binding.symptomButton5, binding.symptomButton6, binding.symptomButton7, binding.symptomButton8,
                binding.symptomButton9, binding.symptomButton10, binding.symptomButton11, binding.symptomButton12,
                binding.symptomButton13, binding.symptomButton14, binding.symptomButton15)

        for(classButton in classButtonList) {
            val intent = Intent(this@MainActivity, HospitalListActivity::class.java)
            classButton.setOnClickListener {
                classBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                intent.putExtra("searchWord", classButton.text.toString())
                startActivity(intent)
            }
        }
        for(symptomButton in symptomButtonList) {
            val intent = Intent(this@MainActivity, HospitalListActivity::class.java)
            symptomButton.setOnClickListener {
                symptomBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                intent.putExtra("searchWord", symptomButton.text.toString())
                startActivity(intent)
            }
        }

        //
    }

    //액티비티 전환 사용자정의 함수
    fun setActivity(context: Context, activity: Any) {
        val intent = Intent(context, activity::class.java)
        context.startActivity(intent)
        //(context as Activity).finish()
    }
    //프래그먼트 전환 사용자정의 함수
    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()
    }

    //네이게이션바 선택에 따른 화면전환
    fun navigationSetItem() {
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFrag -> { //홈 버튼 클릭했을 때
                    setFragment(HomeFragment())
                }
                R.id.checkupFrag -> { //나의 진료내역 버튼 클릭했을 때
                    setFragment(MedicalHistoryFragment())
                }
                R.id.communityFrag -> { //커뮤니티 버튼 클릭했을 때
                    setFragment(CommunityFragment())
                }
                R.id.moreFrag -> { //더보기 버튼 클릭했을때
                    //if(userId == "" || userToken == "") { //userId가 비어 있을 경우 = 로그인 안한 경우
                    if(App.prefs.token == null) {
                        setActivity(this, HPDivisonActivity())
                    } else {  //userId가 있는 경우 = 로그인 한 경우
                        setActivity(this, MyProfileActivity())
                    }
                }
            }
            true
        }
    }

    //토큰 체크
    fun tokenCheck() {
        val token = App.prefs.token ?: ""
        var isExpired = isTokenExpired(token)
        if (isExpired) {
            Log.w("MainActivity", "로그인 토큰 만료 or 이상한 토큰임")
            App.prefs.clearToken(this)
        } else {
            Log.w("MainActivity", "로그인 토큰 만료 안됐음")
        }
    }

    //더보기 버튼 클릭시 bottomSheet 보여줌
    fun showBottomSheet(more: String) {
        if(more == "진료과") {
            classBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED //접혀져 있는 상태
        } else if(more == "증상별") {
            symptomBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED //접혀져 있는 상태
        }
    }
    //
}