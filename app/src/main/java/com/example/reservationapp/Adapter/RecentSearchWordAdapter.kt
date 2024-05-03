package com.example.capstone2024

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.HospitalListActivity
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.R


var recent_search_word_data = ArrayList<RecentItem>()
//lateinit var recent_search_word_data:ArrayList<RecentItem>

//최근 검색단어 Adapter
class RecentSearchWordAdapter:
    RecyclerView.Adapter<RecentSearchWordAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            private var recent_textview: TextView

            init {
                //recent_search_word_data = ArrayList()
                /*
                if(recent_search_word_data==null) {
                    recent_search_word_data = ArrayList()
                }
                */


                recent_textview = itemView.findViewById(R.id.search_word)

                //최근 검색 단어 버튼을 클릭했을때
                itemView.setOnClickListener {
                    //병원 검색 목록 페이지로 넘어간다
                    val context = itemView.context
                    var searchWord = recent_search_word_data[adapterPosition].recent_search_word

                    val intent = Intent(context, HospitalListActivity::class.java)
                    intent.putExtra("searchWord", searchWord) //검색어 데이터 putExtra로 전환 해줘야함
                    context.startActivity(intent)
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


    //데이터 갱신
    fun updateList(newList: ArrayList<RecentItem>) {
        recent_search_word_data = newList
        Log.w("RecentSearchWordAdapter", "1. updateList: ${newList}")
        notifyDataSetChanged()
    }

    //데이터 접근
    fun getRecentWorldData(): ArrayList<RecentItem> {
        return recent_search_word_data
    }

    //
}