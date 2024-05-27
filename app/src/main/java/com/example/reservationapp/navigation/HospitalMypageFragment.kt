package com.example.reservationapp.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.reservationapp.R

class HospitalMypageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hospital_mypage, container, false)

        val classReserveList: List<String> =
            listOf("내과", "외과", "이비인후과", "피부과", "안과", "성형외과", "신경외과", "소아청소년과")

        // 진료과별 예약 버튼 설정
        for (i in classReserveList.indices) {
            val classButtonId =
                resources.getIdentifier("class_button${i + 1}", "id", requireContext().packageName)
            val button = view.findViewById<Button>(classButtonId)

            button.text = classReserveList[i]
            button.setOnClickListener {
                // 진료과 로그로 출력
                val selectedClass = button.text.toString()
                Log.d("Hospital_Mypage", "선택된 진료과: $selectedClass")

                // 버튼의 상태 확인
                val isSelected = button.isSelected

                // 버튼 상태 변경
                button.isSelected = !isSelected

                // 상태에 따라 배경색 변경
                if (isSelected) {
                    // 버튼이 선택된 상태라면 원래의 배경색으로 변경
                    button.setBackgroundResource(R.drawable.rounded_button_background3) // 버튼 배경색 변경
                } else {
                    // 버튼이 선택되지 않은 상태라면 새로운 배경색으로 변경
                    button.setBackgroundResource(R.drawable.button_shadow) // 버튼 배경색 변경
                }
            }
        }

        // 월요일 영업/휴무 체크박스 그룹 찾기
        val workingCheckbox_mon = view.findViewById<CheckBox>(R.id.checkbox_working_mon)
        val restCheckbox_mon = view.findViewById<CheckBox>(R.id.checkbox_rest_mon)
        val startMonEditText = view.findViewById<EditText>(R.id.StartMon)
        val finishMonEditText = view.findViewById<EditText>(R.id.FinishMon)

        // 초기에는 EditText 뷰를 숨김
        startMonEditText.visibility = View.GONE
        finishMonEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes_mon = listOf(workingCheckbox_mon, restCheckbox_mon)
        checkboxes_mon.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes_mon.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == R.id.checkbox_working_mon
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startMonEditText.visibility = visibility
                    finishMonEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "화요일: ${buttonView.text}")
                }
            }
        }
        // 화요일 영업/휴무 체크박스 그룹 찾기
        val workingCheckbox_tue = view.findViewById<CheckBox>(R.id.checkbox_working_tue)
        val restCheckbox_tue = view.findViewById<CheckBox>(R.id.checkbox_rest_tue)
        val startTueEditText = view.findViewById<EditText>(R.id.StartTue)
        val finishTueEditText = view.findViewById<EditText>(R.id.FinishTue)

        // 초기에는 EditText 뷰를 숨김
        startTueEditText.visibility = View.GONE
        finishTueEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes_tue = listOf(workingCheckbox_tue, restCheckbox_tue)
        checkboxes_tue.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes_tue.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == R.id.checkbox_working_tue
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startTueEditText.visibility = visibility
                    finishTueEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "화요일: ${buttonView.text}")
                }
            }
        }

        // 수요일 영업/휴무 체크박스 그룹 찾기
        val workingCheckbox_wed = view.findViewById<CheckBox>(R.id.checkbox_working_wed)
        val restCheckbox_wed = view.findViewById<CheckBox>(R.id.checkbox_rest_wed)
        val startWedEditText = view.findViewById<EditText>(R.id.StartWed)
        val finishWedEditText = view.findViewById<EditText>(R.id.FinishWed)

        // 초기에는 EditText 뷰를 숨김
        startWedEditText.visibility = View.GONE
        finishWedEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes_wed = listOf(workingCheckbox_wed, restCheckbox_wed)
        checkboxes_wed.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes_wed.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == R.id.checkbox_working_wed
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startWedEditText.visibility = visibility
                    finishWedEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "수요일: ${buttonView.text}")
                }
            }
        }

        // 목요일 영업/휴무 체크박스 그룹 찾기
        val workingCheckbox_thu = view.findViewById<CheckBox>(R.id.checkbox_working_thu)
        val restCheckbox_thu = view.findViewById<CheckBox>(R.id.checkbox_rest_thu)
        val startThuEditText = view.findViewById<EditText>(R.id.StartThu)
        val finishThuEditText = view.findViewById<EditText>(R.id.FinishThu)

        // 초기에는 EditText 뷰를 숨김
        startThuEditText.visibility = View.GONE
        finishThuEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes_thu = listOf(workingCheckbox_thu, restCheckbox_thu)
        checkboxes_thu.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes_thu.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == R.id.checkbox_working_thu
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startThuEditText.visibility = visibility
                    finishThuEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "목요일: ${buttonView.text}")
                }
            }
        }

        // 금요일 영업/휴무 체크박스 그룹 찾기
        val workingCheckbox_fri = view.findViewById<CheckBox>(R.id.checkbox_working_fri)
        val restCheckbox_fri = view.findViewById<CheckBox>(R.id.checkbox_rest_fri)
        val startFriEditText = view.findViewById<EditText>(R.id.StartFri)
        val finishFriEditText = view.findViewById<EditText>(R.id.FinishFri)

        // 초기에는 EditText 뷰를 숨김
        startFriEditText.visibility = View.GONE
        finishFriEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes_fri = listOf(workingCheckbox_fri, restCheckbox_fri)
        checkboxes_fri.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes_fri.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == R.id.checkbox_working_fri
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startFriEditText.visibility = visibility
                    finishFriEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "금요일: ${buttonView.text}")
                }
            }
        }

        // 토요일 영업/휴무 체크박스 그룹 찾기
        val workingCheckbox_sat = view.findViewById<CheckBox>(R.id.checkbox_working_sat)
        val restCheckbox_sat = view.findViewById<CheckBox>(R.id.checkbox_rest_sat)
        val startSatEditText = view.findViewById<EditText>(R.id.StartSat)
        val finishSatEditText = view.findViewById<EditText>(R.id.FinishSat)

        // 초기에는 EditText 뷰를 숨김
        startSatEditText.visibility = View.GONE
        finishSatEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes_sat = listOf(workingCheckbox_sat, restCheckbox_sat)
        checkboxes_sat.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes_sat.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == R.id.checkbox_working_sat
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startSatEditText.visibility = visibility
                    finishSatEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "토요일: ${buttonView.text}")
                }
            }
        }

        // 일요일 영업/휴무 체크박스 그룹 찾기
        val workingCheckbox_sun = view.findViewById<CheckBox>(R.id.checkbox_working_sun)
        val restCheckbox = view.findViewById<CheckBox>(R.id.checkbox_rest_sun)
        val startSunEditText = view.findViewById<EditText>(R.id.StartSun)
        val finishSunEditText = view.findViewById<EditText>(R.id.FinishSun)

        // 초기에는 EditText 뷰를 숨김
        startSunEditText.visibility = View.GONE
        finishSunEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes_sun = listOf(workingCheckbox_sun, restCheckbox)
        checkboxes_sun.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes_sun.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == R.id.checkbox_working_sun
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startSunEditText.visibility = visibility
                    finishSunEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "일요일: ${buttonView.text}")
                }
            }
        }

        // 병원 소개 텍스트 창
        val hospitalIntroEditText = view.findViewById<EditText>(R.id.explanation2)
        // 병원 소개 입력값 로그에 출력
        hospitalIntroEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val hospitalIntro = hospitalIntroEditText.text.toString()
                Log.d("Hospital_Mypage", "병원 소개: $hospitalIntro")
            }
        }

        // Save 버튼 클릭 이벤트 처리
        val saveButton = view.findViewById<Button>(R.id.SaveButton)
        saveButton.setOnClickListener {
            onSaveButtonClicked()
        }

        return view
    }

//    private fun onSaveButtonClicked() {
//        // 병원 화면으로 이동하는 코드
//        val intent = Intent(requireContext(), HospitalFragment::class.java)
//        startActivity(intent)
//    }

    private fun onSaveButtonClicked() {
        // 병원 화면으로 이동하는 코드
        val hospitalFragment = HospitalFragment()
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_content, hospitalFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
