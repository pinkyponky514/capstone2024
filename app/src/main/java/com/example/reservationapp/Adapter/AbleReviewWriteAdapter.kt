package com.example.reservationapp.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.R
import com.example.reservationapp.ReviewWriteDetailActivity
import java.util.Calendar

private var able_review_write_list_data = ArrayList<HistoryItem>()

class AbleReviewWriteAdapter: RecyclerView.Adapter<AbleReviewWriteAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var reservationId: Long //예약 레이블 번호
        private var hospitalId: Long //병원 레이블 번호

        private var status_TextView: TextView //진료상태
        private var hospital_name_TextView: TextView //병원이름
        private var class_name_TextView: TextView //진료과명
        private var reserve_date_TextView: TextView //예약 날짜

        private var review_Button: Button //리뷰쓰기

        private lateinit var historyItem: HistoryItem

        init {
            reservationId = 0
            hospitalId = 0
            status_TextView = itemView.findViewById(R.id.able_review_status)
            hospital_name_TextView = itemView.findViewById(R.id.able_review_hospital_name_textView)
            class_name_TextView = itemView.findViewById(R.id.able_review_class_textView)
            reserve_date_TextView = itemView.findViewById(R.id.able_review_reserve_date_textView)

            //리뷰쓰기 버튼 onClick
            review_Button = itemView.findViewById(R.id.able_review_write_button)
            review_Button.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ReviewWriteDetailActivity::class.java)
                intent.putExtra("historyItem", historyItem)
                context.startActivity(intent)
            }
        }


        //데이터 설정
        fun setContents(list: HistoryItem) {
            historyItem = list

            reservationId = list.reservationId
            hospitalId = list.hospitalId
            status_TextView.text = list.status
            hospital_name_TextView.text = list.hospitalName
            class_name_TextView.text = "| " + list.className

            val dateSplit = list.reserveDay.split("-")
            Log.w("PastHistoryAdapter", "dateSplit: $dateSplit")

            val dayOfWeek = getDayOfWeek(dateSplit)
            reserve_date_TextView.text = "${dateSplit[0]}.${dateSplit[1]}.${dateSplit[2]} ($dayOfWeek) ${list.reserveTime}"

            if(list.reviewWriteBoolean) { //리뷰를 썼으면
                review_Button.isEnabled = false
                review_Button.text = "리뷰 작성완료"
                review_Button.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                review_Button.setBackgroundResource(R.drawable.style_gray_radius_5)
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_able_review_write, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(able_review_write_list_data[position])
    }
    override fun getItemCount(): Int {
        return able_review_write_list_data.size
    }

    //
    fun updatelist(newList: ArrayList<HistoryItem>) {
        able_review_write_list_data = newList
        notifyDataSetChanged()
    }

    //
    //년, 월, 일 해당하는 날짜의 요일 구하기
    private fun getDayOfWeek(dateSplit: List<String>): String {
        val year = dateSplit[0].toInt()
        val month = dateSplit[1].toInt()-1
        val day = dateSplit[2].toInt()
        Log.w("PastHistoryAdapter", "dateSplit year: $year, month: ${month}, day: $day")

        val calendar = Calendar.getInstance()
        calendar.set(year, month-1, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when(dayOfWeek) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }
    }

    //
}