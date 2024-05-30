package com.example.reservationapp.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.HospitalListActivity
import com.example.reservationapp.R

private var pharmacy_search_word_list = ArrayList<String>()

class PharmacyMapSearchAdapter: RecyclerView.Adapter<PharmacyMapSearchAdapter.ViewHolder>() {
    //리사이클러뷰 아이템 클릭을 처리하기 위한 인터페이스
    interface ItemClick {
        fun itemSetOnClick(itemView: View, position: Int)
    }
    var itemClick: ItemClick ?= null //아이템 클릭 리스너


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var textView: TextView //약국 검색 단어

        init {
            textView = itemView.findViewById(R.id.pharmacy_textView)

/*
            itemView.setOnClickListener {
                val context = itemView.context as Activity

                val intent = Intent(context, HospitalListActivity::class.java)
                intent.putExtra("searchWord", searchWord) //검색어 데이터 putExtra로 전환 해줘야함
                context.startActivity(intent)
                context.finish()
            }
*/
        }

        //데이터 설정
        fun setContents(queryText: String) {
            textView.setText(queryText)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_pharmacymap_search, viewGroup, false)
        return ViewHolder(layoutInflater)

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //아이템 클릭 onClick
        holder.itemView.setOnClickListener { item ->
            itemClick?.itemSetOnClick(item, position)
        }

        holder.setContents(pharmacy_search_word_list[position])
    }
    override fun getItemCount(): Int {
        return pharmacy_search_word_list.size
    }

    //
    fun updatelist(newList: ArrayList<String>) {
        pharmacy_search_word_list = newList
        notifyDataSetChanged()
    }
}