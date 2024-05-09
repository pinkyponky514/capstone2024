package com.example.reservationapp

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TableRow
import androidx.annotation.RequiresApi
import androidx.core.view.marginStart
import androidx.fragment.app.DialogFragment
import com.example.reservationapp.databinding.FragmentCustomMoreDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomMoreDialogActivity(): DialogFragment() {
//class CustomMoreDialogActivity(val list: List<String>): DialogFragment() {
//class CustomMoreDialogActivity(val list: List<String>): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCustomMoreDialogBinding
    private lateinit var list: List<String>

    companion object {
        private const val ARG_LIST = "arg_list"

        fun newInstance(newList: List<String>): CustomMoreDialogActivity {
            return CustomMoreDialogActivity().apply {
                arguments = Bundle().apply {//arguments = bundleOf(ARG_LIST to newList)
                    putStringArrayList(ARG_LIST, ArrayList(newList))
                }
                list = arguments?.getStringArrayList(ARG_LIST) ?: emptyList() //list = newList
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCustomMoreDialogBinding.inflate(inflater, container, false)

        val rowSize = 4 //한 행에 들어갈 버튼 수
        var tableRow: TableRow?= null //동적으로 추가하기 위함, 아직 테이블에 아무 행도 없기 때문에 id를 찾을 수 없음
        var buttonCountInRow = 0 //현재 행에 추가된 버튼 개수
        val listTableLayout = binding.listTableLayout

        for(i in list.indices) { //list의 개수만큼 for문 수행
            if(buttonCountInRow == rowSize || (i % 4 == 0) ) {
                buttonCountInRow = 0 //한 행에 버튼 개수 초기화
            }

            if(buttonCountInRow == 0) { //새로운 행 시작
                tableRow = TableRow(requireContext())
                tableRow?.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                tableRow?.gravity = Gravity.CENTER_VERTICAL
                tableRow?.gravity = Gravity.CENTER_HORIZONTAL
                listTableLayout.addView(tableRow)
            }

            val button = Button(requireContext())
            button.text = list[i]
            //button.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (37*resources.displayMetrics.density).toInt())

            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13f)
            button.setAutoSizeTextTypeUniformWithConfiguration(9f.toInt(), 13f.toInt(), 1, TypedValue.COMPLEX_UNIT_DIP)
            button.setBackgroundResource(requireContext().resources.getIdentifier("style_gray_radius_line_button", "drawable", requireContext().packageName))

            tableRow?.addView(button)
            buttonCountInRow++
        }
        if(buttonCountInRow != 4) {
            tableRow?.gravity = Gravity.NO_GRAVITY
            //tableRow?.marginStart = (TypedValue.COMPLEX_UNIT_PX, 10f)
        }


        /*
        //해당 뷰 없어지게 하는 버튼
        val dimissButton = binding.dismissImageView
        dimissButton.setOnClickListener {
            dismiss()
        }
        */

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val Dialog = dialog?.window
        Dialog?.setGravity(Gravity.BOTTOM) //하단에 위치
        Dialog?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //백그라운드 컬러 불투명
        //Dialog?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //백그라운드 컬러 투명

        //Dialog?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) //뷰 크기 설정
        //dialog?.setContentView(R.layout.fragment_custom_more_dialog)
        //dialog?.setCancelable(false) //다이얼로그 외 다른 화면 눌렀을때 나가는걸 방지

        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val deviceSize = Point() //현재 디바이스 크기
        display.getSize(deviceSize)

        val params = Dialog?.attributes
        params?.width = deviceSize.x
        params?.horizontalMargin = 0.0f
        dialog?.window?.attributes = params
    }

}
