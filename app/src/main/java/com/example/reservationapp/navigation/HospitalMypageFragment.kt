package com.example.reservationapp.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.MultiImageHospitalAdapter
import com.example.reservationapp.Model.ImageDataHospital
import com.example.reservationapp.R

@Suppress("DEPRECATION")
class HospitalMypageFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val TAG = "HospitalMypageFragment"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var getImageButton: Button
    private lateinit var imageCountTextView: TextView
    private val imageDataList = ArrayList<ImageDataHospital>()
    private lateinit var adapter: MultiImageHospitalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hospital_mypage, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        getImageButton = view.findViewById(R.id.getImage)
        imageCountTextView = view.findViewById(R.id.imageCountTextView)

        getImageButton.setOnClickListener {
            if (imageDataList.size >= 5) {
                Toast.makeText(context, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                startActivityForResult(intent, HospitalMypageFragment.REQUEST_IMAGE_PICK)
            }
        }

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

        setupCheckboxGroup(view, R.id.checkbox_working_mon, R.id.checkbox_rest_mon, R.id.StartMon, R.id.FinishMon)
        setupCheckboxGroup(view, R.id.checkbox_working_tue, R.id.checkbox_rest_tue, R.id.StartTue, R.id.FinishTue)
        setupCheckboxGroup(view, R.id.checkbox_working_wed, R.id.checkbox_rest_wed, R.id.StartWed, R.id.FinishWed)
        setupCheckboxGroup(view, R.id.checkbox_working_thu, R.id.checkbox_rest_thu, R.id.StartThu, R.id.FinishThu)
        setupCheckboxGroup(view, R.id.checkbox_working_fri, R.id.checkbox_rest_fri, R.id.StartFri, R.id.FinishFri)
        setupCheckboxGroup(view, R.id.checkbox_working_sat, R.id.checkbox_rest_sat, R.id.StartSat, R.id.FinishSat)
        setupCheckboxGroup(view, R.id.checkbox_working_sun, R.id.checkbox_rest_sun, R.id.StartSun, R.id.FinishSun)

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

        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        adapter = MultiImageHospitalAdapter(imageDataList, requireContext()) { position ->
            if (position >= 0 && position < imageDataList.size) {
                imageDataList.removeAt(position)
                adapter.notifyDataSetChanged()
                updateImageCount()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun updateImageCount() {
        imageCountTextView.text = "${imageDataList.size}/5"
    }

    private fun onSaveButtonClicked() {
        // 병원 화면으로 이동하는 코드
        val hospitalFragment = HospitalFragment()
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_content, hospitalFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == HospitalMypageFragment.REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImages = ArrayList<Uri>()
            if (data.clipData == null) { // Single image selected
                val imageUri = data.data
                if (imageUri != null) {
                    selectedImages.add(imageUri)
                }
            } else { // Multiple images selected
                val clipData = data.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        selectedImages.add(clipData.getItemAt(i).uri)
                    }
                }
            }

            // 이미지 개수 확인
            val totalImages = imageDataList.size + selectedImages.size
            if (totalImages > 5) {
                Toast.makeText(context, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
                return
            }

            // 이미지 추가
            for (uri in selectedImages) {
                imageDataList.add(ImageDataHospital(uri))
            }

            adapter.notifyDataSetChanged()
            updateImageCount()
        }
    }

    private fun setupCheckboxGroup(view: View, workingCheckboxId: Int, restCheckboxId: Int, startEditTextId: Int, finishEditTextId: Int) {
        val workingCheckbox = view.findViewById<CheckBox>(workingCheckboxId)
        val restCheckbox = view.findViewById<CheckBox>(restCheckboxId)
        val startEditText = view.findViewById<EditText>(startEditTextId)
        val finishEditText = view.findViewById<EditText>(finishEditTextId)

        // 초기에는 EditText 뷰를 숨김
        startEditText.visibility = View.GONE
        finishEditText.visibility = View.GONE

        // 체크박스 선택 이벤트 처리
        val checkboxes = listOf(workingCheckbox, restCheckbox)
        checkboxes.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 체크박스 선택 해제
                    checkboxes.filter { it != buttonView }.forEach { it.isChecked = false }

                    // 선택된 체크박스에 따라 EditText 뷰의 가시성 조정
                    val isWorking = buttonView.id == workingCheckboxId
                    val visibility = if (isWorking) View.VISIBLE else View.GONE
                    startEditText.visibility = visibility
                    finishEditText.visibility = visibility
                    Log.d("Hospital_Mypage", "${resources.getResourceEntryName(buttonView.id)}: ${buttonView.text}")
                }
            }
        }
    }
}
