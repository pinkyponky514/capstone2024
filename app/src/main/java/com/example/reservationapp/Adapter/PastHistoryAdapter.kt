package com.example.reservationapp.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Hospital_DetailPage
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.R
import java.util.Calendar

private var past_history_list_data = ArrayList<HistoryItem>()

class PastHistoryAdapter: RecyclerView.Adapter<PastHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var status_TextView: TextView //진료상태
        private var hospital_name_TextView: TextView //병원이름
        private var class_name_TextView: TextView //진료과명
        private var reserve_date_TextView: TextView //예약 날짜

        private var try_reserve_Button: Button //다시 접수하기
        private lateinit var review_Button: Button //리뷰쓰기


        init {
            status_TextView = itemView.findViewById(R.id.history_status_textView)
            hospital_name_TextView = itemView.findViewById(R.id.hospital_name_textView)
            class_name_TextView = itemView.findViewById(R.id.class_textView)
            reserve_date_TextView = itemView.findViewById(R.id.reserve_date_textView)


            //다시 접수하기 버튼 onClick
            try_reserve_Button = itemView.findViewById<Button>(R.id.try_reserve_button)
            try_reserve_Button.setOnClickListener {
                val hospitalNameTextView = itemView.findViewById<TextView>(R.id.hospital_name_textView)
                val context = itemView.context
                val intent = Intent(context, Hospital_DetailPage::class.java)
                intent.putExtra("hospitalName", hospitalNameTextView.text)
                context.startActivity(intent)
            }
        }

        //데이터 설정
        fun setContents(list: HistoryItem) {
            status_TextView.text = list.status
            hospital_name_TextView.text = list.hospitalName
            class_name_TextView.text = " | " + list.className

            val dayOfWeek = getDayOfWeek(list.reserveDay)
            reserve_date_TextView.text = list.reserveDay + " (" + dayOfWeek + ") " + list.reserveTime
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PastHistoryAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_past_history, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: PastHistoryAdapter.ViewHolder, position: Int) {
        holder.setContents(past_history_list_data[position])
    }
    override fun getItemCount(): Int {
        return past_history_list_data.size
    }

    //
    fun updatelist(newList: ArrayList<HistoryItem>) {
        past_history_list_data = newList
        notifyDataSetChanged()
    }
    //
    //년, 월, 일 해당하는 날짜의 요일 구하기
    private fun getDayOfWeek(date: String): String {
        val dateSplit = date.split(".")
        val year = dateSplit[0].toInt()
        val month = dateSplit[1].toInt()-1
        val day = dateSplit[2].toInt()
        Log.w("dateSplit", "year: $year, month: $month, day: $day")

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

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