package com.example.reservationapp.navigation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.CommunityDetailCommentAdapter
import com.example.reservationapp.App
import com.example.reservationapp.Model.BoardContentResponse
import com.example.reservationapp.Model.BoardLikeResponse
import com.example.reservationapp.Model.BoardLikesResponset
import com.example.reservationapp.Model.ChatBotResponse
import com.example.reservationapp.Model.CommentItem
import com.example.reservationapp.Model.CommentRequest
import com.example.reservationapp.Model.CommentsRequest
import com.example.reservationapp.Model.CommunityCommentRequest
import com.example.reservationapp.R
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CommunityDetailCommentFragment : Fragment() {

    private lateinit var buttonFavorite: CompoundButton
    private lateinit var titleTextView: TextView
    private lateinit var writerTextView:TextView
    private lateinit var textViewContent: TextView
    private lateinit var timestamp2: TextView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton2: Button
    private lateinit var adapter: CommunityDetailCommentAdapter
    private var boardId: Long = 0

    private lateinit var progressBar: ProgressBar

    companion object {
        private const val ARG_IMAGE_RESOURCE = "arg_image_resource"
        private const val ARG_IMAGE_TITLE = "arg_image_title"
        private const val ARG_BOARD_ID = "arg_board_id"

        fun newInstance(imageResource: Int, imageTitle: String, boardId: Long): CommunityDetailCommentFragment {
            val fragment = CommunityDetailCommentFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RESOURCE, imageResource)
            args.putString(ARG_IMAGE_TITLE, imageTitle)
            args.putLong(ARG_BOARD_ID, boardId) // 올바른 boardId 값 설정
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            boardId = it.getLong("boardId", 0)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community_detail_comment, container, false)

        // ProgressBar를 XML 레이아웃에서 찾아서 변수에 할당합니다.
        progressBar = view.findViewById(R.id.progressBar)

        // 데이터를 로드하기 전에 ProgressBar를 표시합니다.
        progressBar.visibility = View.VISIBLE

        titleTextView = view.findViewById(R.id.textViewTitle)
        writerTextView = view.findViewById(R.id.textViewWriter)
        textViewContent = view.findViewById(R.id.textViewContent)
        timestamp2 = view.findViewById(R.id.timestamp2)
        commentRecyclerView = view.findViewById(R.id.commentRecyclerView)
        editText = view.findViewById(R.id.messageEditText)
        sendButton2 = view.findViewById(R.id.buttonSend)
        buttonFavorite = view.findViewById(R.id.button_favorite)

        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CommunityDetailCommentAdapter()
        commentRecyclerView.adapter = adapter

        sendButton2.isEnabled = false

        App.apiService.getBoaradContent(boardId = boardId).enqueue(object :
            Callback<BoardContentResponse> {
            override fun onResponse(call: Call<BoardContentResponse>, response: Response<BoardContentResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    val data = responseBody.data
                    val title = data.title
                    val content = data.content
                    val writer = data.writer
                    val regDate = data.regDate
                    //     val regTime = data.regTime

//
//                    titleTextView = view.findViewById<TextView>(R.id.textViewTitle)
//                    writerTextView = view.findViewById(R.id.textViewWriter)
//                    timestamp2 = view.findViewById(R.id.timestamp2)
                    timestamp2.text = regDate.toString()
                    titleTextView.text = title
                    writerTextView.text="글쓴이:"+writer
                    textViewContent.text = content

                    fetchComments()
                    progressBar.visibility = View.GONE

                } else {
                    Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                }
            }

            override fun onFailure(call: Call<BoardContentResponse>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })

        buttonFavorite = view.findViewById(R.id.button_favorite)

        buttonFavorite.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                App.apiService.postBoardLike(boardId = boardId).enqueue(object :
                    Callback<BoardLikeResponse> {
                    override fun onResponse(call: Call<BoardLikeResponse>, response: Response<BoardLikeResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()!!
                            Log.d("Success Response", responseBody.toString())
                            val message = responseBody.message

                            buttonView.setBackgroundResource(R.drawable.ic_likes)
                            Log.d("CommunityDetailFragment", "좋아요꺼짐")
                        } else {
                            Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                        }
                    }
                    override fun onFailure(call: Call<BoardLikeResponse>, t: Throwable) {
                        Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                    }
                })
            } else {
                val boardid = boardId
                App.apiService.postBoardLike(boardId = boardId).enqueue(object :
                    Callback<BoardLikeResponse> {
                    override fun onResponse(call: Call<BoardLikeResponse>, response: Response<BoardLikeResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()!!
                            Log.d("Success Response", responseBody.toString())
                            val message = responseBody.message

                            buttonView.setBackgroundResource(R.drawable.ic_favoritelikes)
                            Log.d("CommunityDetailFragment", "좋아요")
                        } else {
                            Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                        }
                    }

                    override fun onFailure(call: Call<BoardLikeResponse>, t: Throwable) {
                        Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                    }
                })
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    sendButton2.isEnabled = true
                    sendButton2.setBackgroundResource(R.drawable.rounded_button_active)
                } else {
                    sendButton2.isEnabled = false
                    sendButton2.setBackgroundResource(R.drawable.rounded_button_inactive)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        sendButton2.setOnClickListener {
            val commentContent = editText.text.toString().trim()
            if (commentContent.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
              val formattedTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", currentTime).toString()
//                //val comment = CommentItem(commentContent, "pinky", formattedTime) // 작성자 정보는 나중에 변경 가능

                val comment = CommentRequest(commentContent)
                App.apiService.postComment(boardId, comment).enqueue(object : Callback<CommunityCommentRequest> {
                    override fun onResponse(call: Call<CommunityCommentRequest>, response: Response<CommunityCommentRequest>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                val data = responseBody.data
                                val newComment = CommentItem(commentContent, data.writer, "${data.regDate} ${data.regTime}")
                                adapter.addComment(newComment)
                                editText.text.clear()
                            } else {
                                Log.d("FAILURE Response", "Response body is null")
                            }
                        } else {
                            Log.d("FAILURE Response", "Connect SUCCESS, Response FAILURE, body: ${response.body().toString()}")
                        }
                    }

                    override fun onFailure(call: Call<CommunityCommentRequest>, t: Throwable) {
                        Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                    }
                })

                }
        }

        return view
    }
    fun fetchComments(){
        App.apiService.getComments(boardId = boardId).enqueue(object :
            Callback<CommentsRequest> {
            override fun onResponse(call: Call<CommentsRequest>, response: Response<CommentsRequest>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    val data = responseBody.data

                    for(comment in data){
                        val newComment = CommentItem(comment.content, comment.writer, "${comment.regDate} ${comment.regTime}")
                        adapter.addComment(newComment)
                    }

                } else {
                    Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                }
            }

            override fun onFailure(call: Call<CommentsRequest>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }


}
