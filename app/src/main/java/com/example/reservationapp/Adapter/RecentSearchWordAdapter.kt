package com.example.capstone2024

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.App
import com.example.reservationapp.HospitalListActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.RecentItem
import com.example.reservationapp.Model.RecentSearchWordResponse
import com.example.reservationapp.Model.RecentSearchWordResponseData
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private var recent_search_word_data = ArrayList<RecentItem>()

//최근 검색단어 Adapter
class RecentSearchWordAdapter:
    RecyclerView.Adapter<RecentSearchWordAdapter.ViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var recent_textview: TextView
        private var delete_button: Button
        private var searchWord: String = ""

        //Retrofit
        private var retrofitClient: RetrofitClient
        private var apiService: APIService
        private lateinit var responseBody: RecentSearchWordResponseData
        private lateinit var responseBodyPost: RecentSearchWordResponseData


        init {
            recent_textview = itemView.findViewById(R.id.search_word)
            delete_button = itemView.findViewById(R.id.clear_button)

            //Retrofit
            retrofitClient = RetrofitClient.getInstance()
            apiService = retrofitClient.getRetrofitInterface()


            //최근 검색 단어 버튼을 클릭했을때
            itemView.setOnClickListener {
                //병원 검색 목록 페이지로 넘어간다
                val context = itemView.context as Activity

                searchWord = recent_search_word_data[adapterPosition].recentSearchWord

                if(App.prefs.token != null) { //로그인 되어 있으면
                    apiService.postRecentSearchWord(searchWord).enqueue(object: Callback<RecentSearchWordResponseData> {
                        override fun onResponse(call: Call<RecentSearchWordResponseData>, response: Response<RecentSearchWordResponseData>) {
                            if(response.isSuccessful) {
                                responseBodyPost = response.body()!!
                                Log.w("HospitalSearchActivity", "검색어 저장! responseBodyPost : $responseBodyPost")

                                val intent = Intent(context, HospitalListActivity::class.java)
                                intent.putExtra("searchWord", searchWord) //검색어 데이터 putExtra로 전환 해줘야함
                                context.startActivity(intent)
                                context.finish()
                            }

                            else handleErrorResponse(response)
                        }

                        override fun onFailure(call: Call<RecentSearchWordResponseData>, t: Throwable) {
                            Log.w("HospitalSearchActivity", "post Recent Search Word API call failed: ${t.localizedMessage}")
                        }
                    })
                }
                //로그인 안되어 있으면
                else {
                    val intent = Intent(context, HospitalListActivity::class.java)
                    intent.putExtra("searchWord", searchWord) //검색어 데이터 putExtra로 전환 해줘야함
                    context.startActivity(intent)
                    context.finish()
                }
            }

            //최근 검색 단어 x버튼 클릭했을때
            delete_button.setOnClickListener {
                searchWord = recent_search_word_data[adapterPosition].recentSearchWord
                recent_search_word_data.removeAt(adapterPosition)
                notifyDataSetChanged()

                if(App.prefs.token != null) {
                    apiService.deleteRecentSearchWord(searchWord).enqueue(object: Callback<RecentSearchWordResponseData> {
                        override fun onResponse(call: Call<RecentSearchWordResponseData>, response: Response<RecentSearchWordResponseData>) {
                            if(response.isSuccessful) {
                                responseBody = response.body()!!
                                Log.w("RecentSearchWordAdpater", "최근 검색어 삭제! $responseBody")
                            }

                            else handleErrorResponse(response)
                        }

                        override fun onFailure(call: Call<RecentSearchWordResponseData>, t: Throwable) {

                        }
                    })
                }

                else {

                }
            }
        }

        //데이터 설정해줌
        fun setContents(list: RecentItem) {
            recent_textview.text = list.recentSearchWord
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_recent_search, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    //ViewHolder에 데이터 연결
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(recent_search_word_data[position])
    }
    override fun getItemCount(): Int {
        return recent_search_word_data.size
    }


    //데이터 갱신
    fun updateList(newList: ArrayList<RecentItem>) {
        recent_search_word_data = newList
        Log.w("RecentSearchWordAdapter", "updateList: ${newList}")
        notifyDataSetChanged()
    }

    //
}