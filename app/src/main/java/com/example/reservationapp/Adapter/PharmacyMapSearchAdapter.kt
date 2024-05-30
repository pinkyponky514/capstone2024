package com.example.reservationapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.R

private var pharmacy_search_word_list = ArrayList<String>()

class PharmacyMapSearchAdapter: RecyclerView.Adapter<PharmacyMapSearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var textView: TextView //약국 검색 단어

        init {
            textView = itemView.findViewById(R.id.pharmacy_textView)
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