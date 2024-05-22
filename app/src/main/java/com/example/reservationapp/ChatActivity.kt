package com.example.reservationapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone2024.ChattingAdapter
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.ChatBotResponse
import com.example.reservationapp.Model.ChatItem
import com.example.reservationapp.Model.SearchHospital

import com.example.reservationapp.Retrofit.RetrofitClient
import com.example.reservationapp.databinding.ActivityChatBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//챗gpt 채팅 페이지 액티비티
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var adapter: ChattingAdapter
    private lateinit var chatList: ArrayList<ChatItem>

    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService

    private lateinit var recommended_departments: List<String>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        adapter = ChattingAdapter()

        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()

        //채팅 recyclerView
        val recyclerView = binding.chatRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true //가장 최근 대화 맨 아래로 정렬
        recyclerView.setHasFixedSize(true);
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager



        //주고 받은 채팅 임의의 데이터 초기화
        chatList = ArrayList()
        chatList.add(ChatItem("AI", "안녕하세요. 캐치닥터 챗봇입니다. 당신의 증상을 알려주세요. 캐치닥터 챗봇이 진료과목을 추천해드립니다!"))
//        chatList.add(ChatItem("hansung", "머리가 아프고, 기침이 나와 어디로 가야하니?"))
//        chatList.add(ChatItem("hansung", "그리고 배도 아파"))
//        chatList.add(ChatItem("AI", "제가 생각하기로는 코로나입니다. 당신은 이비인후과를 방문하십시오."))
//        chatList.add(ChatItem("hansung", "코로나는 아닌거 같아. 심각하게 기침이 나오지 않거든"))
//        chatList.add(ChatItem("AI", "아닙니다. 당신은 코로나입니다. 격리하세요."))
//        chatList.add(ChatItem("hansung", "메세지"))
//        chatList.add(ChatItem("hansung", "메세지"))
//        chatList.add(ChatItem("hansung", "메세지"))
//        chatList.add(ChatItem("AI", "메세지"))
//        chatList.add(ChatItem("AI", "메세지"))
//        chatList.add(ChatItem("AI", "메세지"))
        adapter.updateList(chatList)

        //recyclerView.scrollTo(0, 0) //채팅창 하단으로 이동

        //메세지 보내기
        val messageEditText = binding.messageEditText
        val sendButton = binding.sendButton
        sendButton.setOnClickListener {
            val messageString = messageEditText.text.toString()
            if(messageString != null) {
                chatList.add(ChatItem("hansung", messageString))
                adapter.updateList(chatList)
                messageEditText.setText("")
                apiService.getChatBotAnswer(prompt = messageString).enqueue(object : Callback<ChatBotResponse> {
                    override fun onResponse(call: Call<ChatBotResponse>, response: Response<ChatBotResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()!!
                            Log.d("Success Response", responseBody.toString())

                            recommended_departments = responseBody.departments
                            var chatBotResponse = "당신의 증상에 대해 캐치닥터 챗봇이 추천하는 진료과목은 다음과 같습니다.\n"
                            for(department in recommended_departments) {
                                chatBotResponse += "#${department} "
                            }

                            chatList.add(ChatItem("AI", chatBotResponse))
                            adapter.updateList(chatList)

                            gethospital(recommended_departments[0])
                        } else {
                            handleErrorResponse(response)
                            Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                        }
                    }

                    override fun onFailure(call: Call<ChatBotResponse>, t: Throwable) {
                        Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                    }
                })

        } else {
                Log.w("Message Send Error", "$messageString")
            }
        }
    }

    private fun gethospital(department:String){
        apiService.getSearchHospital(className = department, mapx = 37.2779855, mapy = 127.0274271).enqueue(object : Callback<List<SearchHospital>> {
            override fun onResponse(call: Call<List<SearchHospital>>, response: Response<List<SearchHospital>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    Log.d("Success Response", responseBody.toString())
                    val hospitalName = responseBody[0].hospitalName

                    var chatBotResponse = "현 위치에서 가장 가까운 ${department}를 추천해드리겠습니다."
                    chatList.add(ChatItem("AI", chatBotResponse))
                    adapter.updateList(chatList)

                    chatBotResponse = "#${hospitalName} "


                    chatList.add(ChatItem("AI", chatBotResponse))
                    adapter.updateList(chatList)

                } else {
                    Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                }
            }

            override fun onFailure(call: Call<List<SearchHospital>>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
    private fun handleErrorResponse(response: Response<ChatBotResponse>) {
        val errorBody = response.errorBody()?.string()
        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: $errorBody")
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
