package com.example.reservationapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.HistoryItem
import com.example.reservationapp.R

private var history_list_data = ArrayList<HistoryItem>()

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var status_TextView: TextView //진료상태
        private var hospital_name_TextView: TextView //병원이름
        private var class_name_TextView: TextView //진료과명
        private var reserve_date_TextView: TextView //예약 날짜
        private lateinit var review_Button: Button //리뷰쓰기
        private lateinit var try_reserve_Button: Button //다시 접수하기

        init {
            status_TextView = itemView.findViewById(R.id.history_status_textView)
            hospital_name_TextView = itemView.findViewById(R.id.hospital_name_textView)
            class_name_TextView = itemView.findViewById(R.id.class_textView)
            reserve_date_TextView = itemView.findViewById(R.id.reserve_date_textView)

        }

        //데이터 설정
        fun setContents(list: HistoryItem) {
            status_TextView.text = list.status
            hospital_name_TextView.text = list.hospitalName
            class_name_TextView.text = list.className
            reserve_date_TextView.text = list.reserveDate
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.history_item, viewGroup, false)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        holder.setContents(history_list_data[position])
    }

    override fun getItemCount(): Int {
        return history_list_data.size
    }

    //
    fun updatelist(newList: ArrayList<HistoryItem>) {
        history_list_data = newList
        notifyDataSetChanged()
    }
}