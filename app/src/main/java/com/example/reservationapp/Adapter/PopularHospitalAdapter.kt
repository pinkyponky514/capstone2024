package com.example.reservationapp.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Hospital_DetailPage
import com.example.reservationapp.Model.PopularHospitalItem
import com.example.reservationapp.R

private var popular_hospital_list_data = ArrayList<PopularHospitalItem>()

class PopularHospitalAdapter: RecyclerView.Adapter<PopularHospitalAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var score_textView: TextView //순위
        private var hospitalId: Long //병원 레이블 번호
        private var hospitalName_textView: TextView //병원이름


        init {
            score_textView = itemView.findViewById(R.id.score_textView)
            hospitalId = 0
            hospitalName_textView = itemView.findViewById(R.id.hospitalName_textView)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, Hospital_DetailPage::class.java)
                intent.putExtra("hospitalName", hospitalName_textView.text.toString())
                intent.putExtra("hospitalId", hospitalId)
                context.startActivity(intent)
            }
        }

        //데이터 설정
        fun setContents(list: PopularHospitalItem) {
            score_textView.text = list.score.toString()
            hospitalId = list.hospitalId
            hospitalName_textView.text = list.hospitalName
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PopularHospitalAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_popular_hospital, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: PopularHospitalAdapter.ViewHolder, position: Int) {
        holder.setContents(popular_hospital_list_data[position])
    }
    override fun getItemCount(): Int {
        return popular_hospital_list_data.size
    }

    //
    fun updatelist(newList: List<PopularHospitalItem>) {
        popular_hospital_list_data = newList as ArrayList
        Log.w("PopularHospitalAdapter", "병원 인기 순위 Adapter: $popular_hospital_list_data")
        notifyDataSetChanged()
    }
//
}