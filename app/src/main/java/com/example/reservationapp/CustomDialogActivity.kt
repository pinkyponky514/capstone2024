import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.annotation.RequiresApi
import androidx.core.view.marginStart
import androidx.fragment.app.DialogFragment
import com.example.reservationapp.databinding.ActivityCustomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

//class CustomDialogActivity(val list: List<String>): DialogFragment() { // BottomSheetDialogFragment()
class CustomDialogActivity(val list: List<String>): BottomSheetDialogFragment() {
    private lateinit var binding: ActivityCustomDialogBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityCustomDialogBinding.inflate(inflater, container, false)

        val rowSize = 4 //한 행에 들어갈 버튼 수
        var tableRow: TableRow?= null //동적으로 추가하기 위함, 아직 테이블에 아무 행도 없기 때문에 id를 찾을 수 없음
        var buttonCountInRow = 0 //현재 행에 추가된 버튼 개수
        val listTableLayout = binding.listTableLayout

        for(i in list.indices) {
            if(buttonCountInRow == rowSize || (i % 4 == 0) ) {
                buttonCountInRow = 0 //한 행에 버튼 개수 초기화
            }

            if(buttonCountInRow == 0) { //새로운 행 시작
                tableRow = TableRow(requireContext())
                tableRow?.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                tableRow?.gravity = Gravity.CENTER_VERTICAL
                listTableLayout.addView(tableRow)
            }

            val button = Button(requireContext())
            button.text = list[i]

            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15f)
            button.setAutoSizeTextTypeUniformWithConfiguration(9f.toInt(), 15f.toInt(), 1, TypedValue.COMPLEX_UNIT_DIP)
            button.setBackgroundResource(requireContext().resources.getIdentifier("style_reserve_button", "drawable", requireContext().packageName))

            tableRow?.addView(button)
            buttonCountInRow++
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

}
