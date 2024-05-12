package com.example.reservationapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.R
import java.text.*
import java.util.*

private var reserve_history_list_data = ArrayList<HistoryItem>()

class ReserveHistoryAdapter: RecyclerView.Adapter<ReserveHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var status_TextView: TextView //진료상태
        private var hospital_name_TextView: TextView //병원이름
        private var class_name_TextView: TextView //진료과명
        private var reserve_date_TextView: TextView //예약 날짜
        private var comming_date_TextView: TextView //다가오는 날짜 계산

        private lateinit var cancel_Button: Button //예약 취소 버튼


        init {
            status_TextView = itemView.findViewById(R.id.history_status_textView)
            hospital_name_TextView = itemView.findViewById(R.id.hospital_name_textView)
            class_name_TextView = itemView.findViewById(R.id.class_textView)
            reserve_date_TextView = itemView.findViewById(R.id.reserve_date_textView)
            comming_date_TextView = itemView.findViewById(R.id.comming_date_textView)
        }

        //데이터 설정
        fun setContents(list: HistoryItem) {
            status_TextView.text = list.status
            hospital_name_TextView.text = list.hospitalName
            class_name_TextView.text = " | " + list.className

            val dateSplit = list.reserveDay.split(".")
            val timeSplit = list.reserveTime.split(":")
            val dayOfWeek = getDayOfWeek(dateSplit)
            reserve_date_TextView.text = list.reserveDay + " (" + dayOfWeek + ") " + list.reserveTime

            //날짜 세팅
            try {
                var today = Calendar.getInstance()
                var commingDate = String.format("%d-%d-%d %02d:%02d:00", dateSplit[0].toInt(), dateSplit[1].toInt(), dateSplit[2].toInt(), timeSplit[0].toInt(), timeSplit[1].toInt())
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                var date = simpleDateFormat.parse(commingDate)
                var calcuDate = (today.time.time - date.time) / (60 * 60 * 24 * 1000)

                if(calcuDate.toInt() == 0) {
                    comming_date_TextView.text = "오늘"
                } else if (calcuDate.toInt() > 0) {
                    comming_date_TextView.text = ((calcuDate).toInt()).toString() + "일 지남"
                } else {
                    comming_date_TextView.text = ((calcuDate*-1).toInt()).toString() + "일전"
                }

            } catch (e: ParseException) {
                Log.e("ParseException", "Error parsing date", e)
                comming_date_TextView.text = "날짜 오류"
                }
        }
        //
    }



    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_reserve_history, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(reserve_history_list_data[position])
    }
    override fun getItemCount(): Int {
        return reserve_history_list_data.size
    }


    //
    fun updatelist(newList: ArrayList<HistoryItem>) {
        reserve_history_list_data = newList
        notifyDataSetChanged()
    }

    //
    //년, 월, 일 해당하는 날짜의 요일 구하기
    private fun getDayOfWeek(dateSplit: List<String>): String {
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