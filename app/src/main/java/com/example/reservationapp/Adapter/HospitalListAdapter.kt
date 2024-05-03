package com.example.reservationapp.Adapter


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Hospital_DetailPage
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.R


private var hospital_list_data = ArrayList<HospitalItem>()

//병원 목록 페이지 Adapter
class HospitalListAdapter: RecyclerView.Adapter<HospitalListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var Hospital_name_TextView: TextView //병원이름
        private var Star_score_TextView: TextView //별점
        private var Opening_time_TextView: TextView //영업시간
        private var Hospital_address_TextView: TextView //병원주소
        private var class_name_TextView: TextView //진료과명


        init {
            Hospital_name_TextView = itemView.findViewById(R.id.hospitalNameTextView)
            Star_score_TextView = itemView.findViewById(R.id.starScopeTextView)
            Opening_time_TextView = itemView.findViewById(R.id.openTimeTextView)
            Hospital_address_TextView = itemView.findViewById(R.id.hospital_address_TextView)
            class_name_TextView = itemView.findViewById(R.id.classTextView)

            //병원 itemView 눌렀을때
            itemView.setOnClickListener {
                //val hospitalNameTextView = itemView.findViewById<TextView>(R.id.hospitalNameTextView)
                val context = itemView.context
                val intent = Intent(context, Hospital_DetailPage::class.java)

                intent.putExtra("hospitalName", Hospital_name_TextView.text.toString())
                context.startActivity(intent)
            }
        }



        //데이터 설정
        fun setContents(list: HospitalItem) {
            Hospital_name_TextView.text = list.hospitalName
            Star_score_TextView.text = list.starScore
            Opening_time_TextView.text = list.openingTimes
            Hospital_address_TextView.text = list.hospitalAddress
            class_name_TextView.text = list.className
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HospitalListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.hospital_list_item_example, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    //ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: HospitalListAdapter.ViewHolder, position: Int) {
        holder.setContents(hospital_list_data[position])
    }
    override fun getItemCount(): Int {
        return hospital_list_data.size
    }



    //
    fun updatelist(newList: ArrayList<HospitalItem>) {
        hospital_list_data = newList
        Log.w("HospitalListAdapter - updateList :", "${newList}")
        notifyDataSetChanged()
    }

}