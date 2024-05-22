package com.example.reservationapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.Hospital
import com.example.reservationapp.Model.HospitalDetail
import com.example.reservationapp.Model.HospitalDetail2
import com.example.reservationapp.Model.HospitalDetailResponse
import com.example.reservationapp.Retrofit.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class Hospital_Mypage : AppCompatActivity() {

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService

    private var hospitalName:  String? = null
    private lateinit var department: String

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hospital_mypage)

        hospitalName = intent.getStringExtra("hospitalName")

        val hospitalNameTextView = findViewById<TextView>(R.id.textViewHospitalName)
        hospitalNameTextView.text = "병원이름: ${hospitalName}"
        val saveButton = findViewById<Button>(R.id.button)

        val classReserveList: List<String> = listOf("내과", "외과", "이비인후과", "피부과", "안과", "성형외과", "신경외과", "소아청소년과")

        // 진료과별 예약 버튼 설정
        for (i in 0 until classReserveList.size) {
            val classButtonId = resources.getIdentifier("class_button${i + 1}", "id", packageName)
            val button = findViewById<Button>(classButtonId)

            button.text = classReserveList[i]
            var isSelected = false


            button.setOnClickListener {
                // 진료과 로그로 출력
                val selectedClass = button.text.toString()
                Log.d("Hospital_Mypage", "선택된 진료과: $selectedClass")

                isSelected = !isSelected

                // 상태에 따라 배경색 변경
                if (isSelected) {
                    // 버튼이 선택된 상태라면 연두색으로 변경
                    button.setBackgroundResource(R.drawable.button_shadow)
                    button.setBackgroundColor(Color.GREEN)
                    department = selectedClass
                } else {
                    // 버튼이 선택되지 않은 상태라면 흰색으로 변경
                    button.setBackgroundResource(R.drawable.button_shadow)
                    button.setBackgroundColor(Color.WHITE)
                    department = ""
                }
            }
        }


        saveButton.setOnClickListener{
            val MonStartTime = findViewById<EditText>(R.id.MonStartTime).text.toString()
            val MonEndTime = findViewById<EditText>(R.id.MonEndTime).text.toString()
            val TueStartTime = findViewById<EditText>(R.id.TueStartTime).text.toString()
            val TueEndTime = findViewById<EditText>(R.id.TueEndTime).text.toString()
            val WedStartTime = findViewById<EditText>(R.id.WedStartTime).text.toString()
            val WedEndTime = findViewById<EditText>(R.id.WedEndTime).text.toString()
            val ThuStartTime = findViewById<EditText>(R.id.ThuStartTime).text.toString()
            val ThuEndTime = findViewById<EditText>(R.id.ThuEndTime).text.toString()
            val FriStartTime = findViewById<EditText>(R.id.FriStartTime).text.toString()
            val FriEndTime = findViewById<EditText>(R.id.FriEndTime).text.toString()
            val SatStartTime = findViewById<EditText>(R.id.SatStartTime).text.toString()
            val SatEndTime = findViewById<EditText>(R.id.SatEndTime).text.toString()
            val SunStartTime = findViewById<EditText>(R.id.SunStartTime).text.toString()
            val SunEndTime = findViewById<EditText>(R.id.SunEndTime).text.toString()

            val DayStartTime = findViewById<EditText>(R.id.DayStartTime).text.toString()
            val DayEndTime = findViewById<EditText>(R.id.DayEndTime).text.toString()
            val WeekendStartTime = findViewById<EditText>(R.id.WeekendStartTime).text.toString()
            val WeekendEndTime = findViewById<EditText>(R.id.WeekendEndTime).text.toString()


            val hospitalInfo = findViewById<EditText>(R.id.explanation2).text.toString()

            retrofitClient = RetrofitClient.getInstance()
            apiService = retrofitClient.getRetrofitInterface()

            val hospitalDetailRequest = HospitalDetail2(hospitalInfo = hospitalInfo, department = department, mon_open = MonStartTime, mon_close = MonEndTime, tue_open = TueStartTime, tue_close = TueEndTime, wed_open = WedStartTime, wed_close = WedEndTime , thu_open = ThuStartTime, thu_close= ThuEndTime, fri_open = FriStartTime , fri_close = FriEndTime , sat_open= SatStartTime, sat_close=SatEndTime,sun_open= SunStartTime, sun_close=SunEndTime, hol_close = null, hol_open = null)

            val token = App.prefs.token
            apiService.postHospitalDetail(hospitalDetailRequest).enqueue(object: Callback<HospitalDetailResponse> {
                override fun onResponse(call: Call<HospitalDetailResponse>, response: Response<HospitalDetailResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d("SUCCESS Response", "Message: ${responseBody.message}")
                            val reponse = response.body()!!
                            val message = response.message()

                            val intent = Intent(this@Hospital_Mypage, HospitalActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                        //통신 성공, 응답은 실패

                        if (errorBody != null) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                val timestamp = jsonObject.optString("timestamp")
                                val status = jsonObject.optInt("status")
                                val error = jsonObject.optString("error")
                                val message = jsonObject.optString("message")
                                val path = jsonObject.optString("path")

                                Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                            } catch (e: JSONException) {
                                Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<HospitalDetailResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
                }
            })
        }

        // 라디오 버튼 그룹 찾기
        val restdayRadioGroup = findViewById<RadioGroup>(R.id.restday_radio_group)
        // 라디오 버튼 클릭 이벤트 처리
        restdayRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val restday = selectedRadioButton.text.toString()
            Log.d("Hospital_Mypage", "공휴일: $restday")
        }

        // 병원 소개 텍스트 창
        val hospitalIntroEditText = findViewById<EditText>(R.id.explanation2)
        // 병원 소개 입력값 로그에 출력
        hospitalIntroEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val hospitalIntro = hospitalIntroEditText.text.toString()
                Log.d("Hospital_Mypage", "병원 소개: $hospitalIntro")
            }
        }
    }



    fun onBackToHospitalClicked(view: View) {
        val MonStartTime = findViewById<EditText>(R.id.MonStartTime).text.toString()
        val MonEndTime = findViewById<EditText>(R.id.MonEndTime).text.toString()
        val TueStartTime = findViewById<EditText>(R.id.TueStartTime).text.toString()
        val TueEndTime = findViewById<EditText>(R.id.TueEndTime).text.toString()
        val WedStartTime = findViewById<EditText>(R.id.WedStartTime).text.toString()
        val WedEndTime = findViewById<EditText>(R.id.WedEndTime).text.toString()
        val ThuStartTime = findViewById<EditText>(R.id.ThuStartTime).text.toString()
        val ThuEndTime = findViewById<EditText>(R.id.ThuEndTime).text.toString()
        val FriStartTime = findViewById<EditText>(R.id.FriStartTime).text.toString()
        val FriEndTime = findViewById<EditText>(R.id.FriEndTime).text.toString()
        val SatStartTime = findViewById<EditText>(R.id.SatStartTime).text.toString()
        val SatEndTime = findViewById<EditText>(R.id.SatEndTime).text.toString()
        val SunStartTime = findViewById<EditText>(R.id.SunStartTime).text.toString()
        val SunEndTime = findViewById<EditText>(R.id.SunEndTime).text.toString()

        val DayStartTime = findViewById<EditText>(R.id.DayStartTime).text.toString()
        val DayEndTime = findViewById<EditText>(R.id.DayEndTime).text.toString()
        val WeekendStartTime = findViewById<EditText>(R.id.WeekendStartTime).text.toString()
        val WeekendEndTime = findViewById<EditText>(R.id.WeekendEndTime).text.toString()


        val hospitalInfo = findViewById<EditText>(R.id.explanation2).text.toString()



    }

}
