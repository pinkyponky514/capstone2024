package com.example.capstone2024

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.R


private var recent_search_word_data = ArrayList<RecentItem>()


//최근 검색단어 Adapter
class RecentSearchWordAdapter:
    RecyclerView.Adapter<RecentSearchWordAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            private var recent_textview: TextView

            init {
                recent_textview = itemView.findViewById(R.id.search_word)

                //최근 검색 단어 버튼을 클릭했을때
                itemView.setOnClickListener {
                    //병원 검색 목록 페이지로 넘어간다
                }

                //최근 검색 단어 x버튼 클릭했을때
                itemView.findViewById<Button>(R.id.clear_button).setOnClickListener {
                    recent_search_word_data.removeAt(adapterPosition)
                    notifyDataSetChanged()
                }
            }

            //데이터 설정해줌
            fun setContents(list: RecentItem) {
                recent_textview.text = list.recent_search_word
            }
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.recent_search_item, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    //ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(recent_search_word_data[position])
    }
    override fun getItemCount(): Int {
        return recent_search_word_data.size
    }



    //
    fun updateList(newList: ArrayList<RecentItem>) {
        recent_search_word_data = newList
        Log.w("updateList", "${newList}")
        notifyDataSetChanged()
    }

}