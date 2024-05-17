package com.example.reservationapp.Adapter


import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.HospitalListActivity
import com.example.reservationapp.Hospital_DetailPage
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private var hospital_list_data = ArrayList<HospitalItem>()

//병원 목록 페이지 Adapter
class HospitalListAdapter: RecyclerView.Adapter<HospitalListAdapter.ViewHolder>() {
    //리사이클러뷰 아이템 클릭을 처리하기 위한 인터페이스
    interface ItemClick {
        fun itemSetOnClick(itemView: View, position: Int)
    }
    var itemClick: ItemClick ?= null //아이템 클릭 리스너

    @RequiresApi(Build.VERSION_CODES.O)
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var Hospital_Id: Long //병원 레이블 번호 저장
        private var Hospital_name_TextView: TextView //병원이름
        private var Star_score_TextView: TextView //별점
        private var Opening_time_TextView: TextView //영업시간
        private var Hospital_address_TextView: TextView //병원주소
        private var class_name_TextView: TextView //진료과명
        private var status_Textview: TextView //병원 진료상태


        init {
            Hospital_Id = 0
            Hospital_name_TextView = itemView.findViewById(R.id.hospitalNameTextView)
            Star_score_TextView = itemView.findViewById(R.id.starScopeTextView)
            Opening_time_TextView = itemView.findViewById(R.id.openTimeTextView)
            Hospital_address_TextView = itemView.findViewById(R.id.hospital_address_TextView)
            class_name_TextView = itemView.findViewById(R.id.classTextView)
            status_Textview = itemView.findViewById(R.id.status_TextView)
        }



        //데이터 설정
        fun setContents(list: HospitalItem) {
            Hospital_Id = list.hospitalId
            Hospital_name_TextView.text = list.hospitalName
            Star_score_TextView.text = list.starScore
            Opening_time_TextView.text = list.openingTimes
            Hospital_address_TextView.text = list.hospitalAddress
            status_Textview.text = list.status

            var string = ""
            for(i in list.className.indices) {
                string += (list.className[i]+" ")
            }
            class_name_TextView.text = string
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HospitalListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_hospital_list, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    //ViewHolder에 데이터 연결
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HospitalListAdapter.ViewHolder, position: Int) {
        //아이템 클릭 onClick
        holder.itemView.setOnClickListener { item ->
            itemClick?.itemSetOnClick(item, position)
        }

        //데이터 바인딩
        holder.setContents(hospital_list_data[position])
    }
    override fun getItemCount(): Int {
        return hospital_list_data.size
    }



    //
    fun updatelist(newList: ArrayList<HospitalItem>) {
        hospital_list_data = newList
        notifyDataSetChanged()
    }

}