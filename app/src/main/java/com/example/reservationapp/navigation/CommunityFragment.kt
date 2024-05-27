package com.example.reservationapp.navigation

import CommunityImageAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.App
import com.example.reservationapp.Model.*
import com.example.reservationapp.MainActivity
import com.example.reservationapp.Model.BoardContent
import com.example.reservationapp.Model.CommunityItem
import com.example.reservationapp.R
import kotlinx.coroutines.CoroutineStart
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.io.encoding.ExperimentalEncodingApi
import android.util.Base64
import android.widget.ProgressBar

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommunityImageAdapter
    private lateinit var timestamp: TextView
    private lateinit var boardItems: List<BoardContent>
    private lateinit var progressBar: ProgressBar

    val itemList = mutableListOf<CommunityItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        adapter = CommunityImageAdapter(itemList) { position ->
            val bundle = Bundle()
            bundle.putLong("boardId", itemList[position].boardId)

            // 현재 시간을 가져와서 형식을 맞춘 후 TextView에 설정합니다.
            val currentTime = Calendar.getInstance().time
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedTime = sdf.format(currentTime)

            val item = itemList[position]
            //!!!!!!!!!!!!!!!!! 여기 주석 풀어서 확인 !!!!!!!!!!!!!!!!!!!!!!!! 오류남 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
/*
            val fragment = CommunityDetailCommentFragment.newInstance(item.imageResource, item.title, listOf(formattedTime), item.boardId)
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit()
*/
        }

        val mainActivity = requireActivity() as MainActivity
        mainActivity.tokenCheck()

//        // 작성 시간을 표시하는 TextView를 찾아 변수에 할당합니다.
//        timestamp = view.findViewById(R.id.timestamp)

        progressBar = view.findViewById(R.id.progressBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()

        // floatingActionButton 클릭 시 CommunityPostFragment로 이동
        view.findViewById<View>(R.id.floatingActionButton)?.setOnClickListener {
            val fragment = CommunityPostFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit()
        }

        fetchCommunityItems()
    }

    private fun initializeRecyclerView() {
        recyclerView = requireView().findViewById(R.id.recyclerViewCommunity)
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun fetchCommunityItems() {
        // 로딩 시작 시 ProgressBar 표시
        progressBar.visibility = View.VISIBLE
        val apiService = App.apiService

        apiService.getAllBoards().enqueue(object : Callback<AllBoardResponse> {
            override fun onResponse(call: Call<AllBoardResponse>, response: Response<AllBoardResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    boardItems = responseBody.data
                    Log.d("CommunityFragment", "boardItems: $boardItems")

                    var completedRequests = 0
                    for (boardContent in boardItems) {
                        getCommentsCount(boardContent.id) { commentCount ->
                            getBoardLikeCount(boardContent.id) { likeCount ->

                                val decodedBytes: ByteArray = Base64.decode(boardContent.mainImage, Base64.DEFAULT)
                                val bitmapImage = getImg(decodedBytes)
                                val item = CommunityItem(
                                    bitmapImage,
                                    boardContent.title,
                                    boardContent.writer ?: "",
                                    likeCount,
                                    commentCount,
                                    "${boardContent.regDate} ${boardContent.regTime}",
                                    boardContent.id
                                )
                                itemList.add(item)
                                completedRequests++
                                if (completedRequests == boardItems.size) {
                                    recyclerView.adapter = adapter
                                    adapter.notifyDataSetChanged()
                                    progressBar.visibility = View.GONE
                                }
                            }
                        }
                    }
//                    // 데이터 로드 완료 시 ProgressBar 숨기기
//                    progressBar.visibility = View.GONE
                } else {
                    handleErrorResponse(response)
                    progressBar.visibility = View.GONE
                    Log.d("FAILURE Response", "Connect SUCCESS, Response FAILURE, body: ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<AllBoardResponse>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun handleErrorResponse(response: Response<AllBoardResponse>) {
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
    
    private fun getImg(input: ByteArray): Bitmap{
        var arr = input

        try {
            var bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.size)
            return bitmap
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getCommentsCount(id: Long, callback: (String) -> Unit) {
        App.apiService.getComments(boardId = id).enqueue(object : Callback<CommentsRequest> {
            override fun onResponse(call: Call<CommentsRequest>, response: Response<CommentsRequest>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    val commentCount = responseBody.data.count().toString()
                    callback(commentCount)
                } else {
                    Log.d("FAILURE Response", "Connect SUCCESS, Response FAILURE, body: ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<CommentsRequest>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }

    private fun getBoardLikeCount(id: Long, callback: (String) -> Unit) {
        App.apiService.getBoardLikes(boardId = id).enqueue(object : Callback<BoardLikesResponset> {
            override fun onResponse(call: Call<BoardLikesResponset>, response: Response<BoardLikesResponset>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    val likeCount = responseBody.data.count().toString()
                    callback(likeCount)
                } else {
                    Log.d("FAILURE Response", "Connect SUCCESS, Response FAILURE, body: ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<BoardLikesResponset>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}
