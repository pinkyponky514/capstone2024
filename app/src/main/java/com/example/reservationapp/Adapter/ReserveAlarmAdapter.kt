package com.example.reservationapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.ReserveItem
import com.example.reservationapp.R
import java.util.Calendar

private var reserve_data = ArrayList<ReserveItem>()

//HomeFragment에서 RecyclerView에 사용할 Adapter
class ReserveAlarmAdapter (): RecyclerView.Adapter<ReserveAlarmAdapter.ViewHolder>() {
    //
    //뷰를 담아두는 상자
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var Hospital_name: TextView //예약한 병원이름
        private var Date: TextView //에약한 날짜

        init {
            Hospital_name = itemView.findViewById(R.id.hospital_name_TextView)
            Date = itemView.findViewById(R.id.date_TextView)

            //진료예약 알림버튼 눌렀을때
            //진료내역으로 이동하도록
            itemView.setOnClickListener {
                //itemClickListener?.onItemClick(adapterPosition)
            }
        }

        fun setContents(list: ReserveItem) {
            val dateSplit = list.reservationDate.split("-")
            val dayOfWeek = getDayOfWeek(dateSplit)

            Hospital_name.text = list.hospital_name
            Date.text = "($dayOfWeek) ${list.reservationTime}"
        }

    }
    //


    //ViewHolder 객체를 생성하고 초기화, ViewHolder는 View를 담는 상자
    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewgroup.context)
            .inflate(R.layout.item_reserve_alarm, viewgroup, false)
        return ViewHolder(layoutInflater)
    }
    //ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(reserve_data[position])
    }
    override fun getItemCount(): Int {
        return reserve_data.size
    }

    //사용자정의 함수
    fun updateList(newList: ArrayList<ReserveItem>) {
        reserve_data = newList
        Log.w("updataList", "$newList")
        notifyDataSetChanged()
    }

    //
    //년, 월, 일 해당하는 날짜의 요일 구하기
    private fun getDayOfWeek(dateSplit: List<String>): String {
        val year = dateSplit[0].toInt()
        val month = dateSplit[1].toInt()-1
        val day = dateSplit[2].toInt()

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


}