package com.example.reservationapp.Adapter


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.App
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.HPDivisonActivity
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.BookmarkResponse
import com.example.reservationapp.Model.HospitalItem
import com.example.reservationapp.Model.MyBookmarkResponse
import com.example.reservationapp.Model.handleErrorResponse
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
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
        private var Star_score_TextView: TextView //별점숫자
        private var Opening_time_TextView: TextView //영업시간
        private var Hospital_address_TextView: TextView //병원주소
        private var class_name_TextView: TextView //진료과명
        private var status_Textview: TextView //병원 진료상태
        private var ratingBar: RatingBar //별점
        private var bookmark_Button: ImageView //즐겨찾기
        private var bookmark_flag: Boolean //즐겨찾기 플래그
        private var Hospital_main_Image: ImageView //병원 이미지 string

        //Retrofit
        private var retrofitClient: RetrofitClient = RetrofitClient.getInstance()
        private var apiService: APIService = retrofitClient.getRetrofitInterface()
        private lateinit var responseBodyMyBookmark: MyBookmarkResponse
        private lateinit var responseBodyBookmark: BookmarkResponse


        init {
            Hospital_Id = 0
            Hospital_name_TextView = itemView.findViewById(R.id.hospitalNameTextView)
            Star_score_TextView = itemView.findViewById(R.id.starScopeTextView)
            Opening_time_TextView = itemView.findViewById(R.id.openTimeTextView)
            Hospital_address_TextView = itemView.findViewById(R.id.hospital_address_TextView)
            class_name_TextView = itemView.findViewById(R.id.classTextView)
            status_Textview = itemView.findViewById(R.id.status_TextView)
            ratingBar = itemView.findViewById(R.id.hospital_list_ratingBar)
            bookmark_Button = itemView.findViewById(R.id.bookmark_ImageView)
            bookmark_flag = false
            Hospital_main_Image = itemView.findViewById(R.id.hopsitalImageView)
        }



        //데이터 설정
        fun setContents(list: HospitalItem) {
            Hospital_Id = list.hospitalId
            Hospital_name_TextView.text = list.hospitalName
            Star_score_TextView.text = list.starScore
            Opening_time_TextView.text = list.openingTimes
            Hospital_address_TextView.text = list.hospitalAddress
            status_Textview.text = list.status
            ratingBar.rating = list.starScore.toFloat()
            var string = ""
            for(i in list.className.indices) {
                string += (list.className[i]+" ")
            }
            class_name_TextView.text = string

            //이미지가 있으면
            if(list.mainImage != null) {
                Hospital_main_Image.setImageBitmap(list.mainImage)
            } else {
                Hospital_main_Image.setImageResource(R.drawable.image_no_image)
                Hospital_main_Image.scaleType = ImageView.ScaleType.CENTER_INSIDE
                Hospital_main_Image.setColorFilter(Color.GRAY)
                Hospital_main_Image.setPadding(90, 90, 90, 90)
            }

            //내가 좋아요한 병원 설정
            if(App.prefs.token != null) { //user token이 있으면 == 로그인 했으면
                apiService.getMyHospitalBookmarkList().enqueue(object: Callback<MyBookmarkResponse> { //내가 즐겨찾기 한 병원 불러오기
                    override fun onResponse(call: Call<MyBookmarkResponse>, response: Response<MyBookmarkResponse>) {
                        //통신, 응답 성공
                        if(response.isSuccessful) {
                            responseBodyMyBookmark = response.body()!!
                            Log.w("HospitalListAdapter", "responseBody MyBookmark : $responseBodyMyBookmark, bookmark_flag: $bookmark_flag")

                            if(responseBodyMyBookmark.result.data.boards != null) { //내가 즐겨찾기한 병원 목록이 있다면
                                bookmark_flag = false
                                bookmark_Button.setImageResource(R.drawable.ic_empty_heart)

                                for(responseMyBookmarkIndex in responseBodyMyBookmark.result.data.boards.indices) {
                                    if(responseBodyMyBookmark.result.data.boards[responseMyBookmarkIndex].hospitalId == list.hospitalId) { //즐겨찾기한 병원 아이디랑 상세페이지 병원 아이디가 같은 경우
                                        bookmark_Button.setImageResource(R.drawable.ic_heart)
                                        bookmark_flag = true
                                        break
                                    }
                                    //내가 즐겨찾기 한 병원이 있지만, 해당 병원은 아닐 경우
                                    //bookmark_Button.setImageResource(R.drawable.ic_empty_heart)
                                }
                            }
                            else { //나의 즐겨찾기 한 목록이 없다면, 빈 하트
                                bookmark_flag = false
                                bookmark_Button.setImageResource(R.drawable.ic_empty_heart)
                            }
                        }

                        //통신 성공, 응답 실패
                        else handleErrorResponse(response)
                    }

                    //통신 실패
                    override fun onFailure(call: Call<MyBookmarkResponse>, t: Throwable) {
                        Log.w("HospitalListAdapter CONNECTION FAILURE: ", "MyBookmark Connect FAILURE : ${t.localizedMessage}")
                    }
                })
            }
            else bookmark_Button.setImageResource(R.drawable.ic_empty_heart) //user token 없으면, 빈 하트

            //즐겨찾기 버튼 눌렀을 경우 onClick
            bookmark_Button.setOnClickListener {
                //user token이 있으면 == 로그인 했으면
                if(App.prefs.token != null) {
                    apiService.postHospitalBookmark(list.hospitalId).enqueue(object: Callback<BookmarkResponse> {
                        override fun onResponse(call: Call<BookmarkResponse>, response: Response<BookmarkResponse>) {
                            //통신, 응답 성공
                            if(response.isSuccessful) {
                                responseBodyBookmark = response.body()!!
                                Log.w("HospitalListAdapter", "responseBookmark: $responseBodyBookmark")

                                if(bookmark_flag) { //즐겨찾기 한 병원이면, 즐겨찾기 취소
                                    bookmark_flag = false
                                    bookmark_Button.setImageResource(R.drawable.ic_empty_heart)
                                    Log.w("HospitalListAdapter", "즐겨찾기 취소: $bookmark_flag")
                                } else if(responseBodyBookmark.result.data == "Hospital not found") {
                                    bookmark_flag = false
                                    bookmark_Button.setImageResource(R.drawable.ic_empty_heart)
                                    CustomToast(itemView.context, "병원 정보가 없습니다.\n병원 정보를 입력하길 기다리십시오.").show()
                                } else { //즐겨찾기 안한 병원이면, 즐겨찾기
                                    bookmark_flag = true
                                    bookmark_Button.setImageResource(R.drawable.ic_heart)
                                    Log.w("HospitalListAdapter", "즐겨찾기 등록 : $bookmark_flag")
                                }
                            }

                            //통신 성공, 응답 실패
                            else {
                                val errorBody = response.errorBody()?.string()
                                Log.d("FAILURE Response", "Bookmark onClick Response Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
                                if (errorBody != null) {
                                    try {
                                        val jsonObject = JSONObject(errorBody)
                                        val timestamp = jsonObject.optString("timestamp")
                                        val status = jsonObject.optInt("status")
                                        val error = jsonObject.optString("error")
                                        val message = jsonObject.optString("message")
                                        val path = jsonObject.optString("path")

                                        Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
                                    } catch (e: JSONException) {
                                        Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
                                    }
                                }
                            }
                        }

                        //통신 실패
                        override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                            Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                        }
                    })
                }

                //user token이 없으면, 로그인 먼저
                else {
                    //다이얼로그 띄워서 로그인 먼저 하고 기능을 이용하라고 코드 구현하기
                    val context = itemView.context as Activity
                    val intent = Intent(context, HPDivisonActivity::class.java)
                    context.startActivity(intent)
                    context.finish()
                }
            }
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