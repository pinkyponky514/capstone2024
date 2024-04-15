package com.example.reservationapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.ReserveItem
import com.example.reservationapp.R

private var reserve_data = ArrayList<ReserveItem>()

//HomeFragment에서 RecyclerView에 사용할 Adapter
class ReserveAlarmAdapter ():
    RecyclerView.Adapter<ReserveAlarmAdapter.ViewHolder>() {

    //클릭했을때 일어나는 리스너
    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }
    var itemClickListener: OnItemClickListener ?= null

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

        fun getItems(): List<ReserveItem> {
            return listOf(
                ReserveItem("강남대학병원", "수 15:00")
            )
        }
        fun setContents(list: ReserveItem) {
            Hospital_name.text = list.hospital_name
            Date.text = list.date
        }

    }
    //


    //ViewHolder 객체를 생성하고 초기화, ViewHolder는 View를 담는 상자
    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewgroup.context)
            .inflate(R.layout.reserve_alarm_item, viewgroup, false)
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

}