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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.CommunityDetailCommentAdapter
import com.example.reservationapp.Adapter.ImageAdapter
import com.example.reservationapp.Model.CommentItem
import com.example.reservationapp.Model.ImageItem
import com.example.reservationapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CommunityDetailCommentFragment : Fragment() {

    private lateinit var buttonFavorite: CompoundButton
    private lateinit var timestamp2: TextView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton2: Button
    private lateinit var adapter: CommunityDetailCommentAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var imageAdapter: ImageAdapter

    companion object {
        private const val ARG_IMAGE_RESOURCE = "arg_image_resource"
        private const val ARG_IMAGE_TITLE = "arg_image_title"
        private const val ARG_IMAGE_URLS = "arg_image_urls"

        fun newInstance(imageResource: Int, imageTitle: String, imageUrls: List<String>): CommunityDetailCommentFragment {
            val fragment = CommunityDetailCommentFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RESOURCE, imageResource)
            args.putString(ARG_IMAGE_TITLE, imageTitle)
            args.putStringArrayList(ARG_IMAGE_URLS, ArrayList(imageUrls))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community_detail_comment, container, false)

        val recyclerViewImages: RecyclerView = view.findViewById(R.id.recyclerViewImages)
        recyclerViewImages.layoutManager = GridLayoutManager(requireContext(), 1)

        val imageList = listOf(
            ImageItem(R.drawable.image1),
            ImageItem(R.drawable.image2),
            ImageItem(R.drawable.image3),
            ImageItem(R.drawable.image4),
            ImageItem(R.drawable.image5)
        )

        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton)

        buttonFavorite = view.findViewById(R.id.button_favorite)
        buttonFavorite.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.setBackgroundResource(R.drawable.ic_likes)
                Log.d("CommunityDetailFragment", "좋아요꺼짐")
            } else {
                buttonView.setBackgroundResource(R.drawable.ic_favoritelikes)
                Log.d("CommunityDetailFragment", "좋아요")
            }
        }

        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedTime = sdf.format(currentTime)

        timestamp2 = view.findViewById(R.id.timestamp2)
        timestamp2.text = "작성 시간: $formattedTime"

        commentRecyclerView = view.findViewById(R.id.commentRecyclerView)
        editText = view.findViewById(R.id.messageEditText)
        sendButton2 = view.findViewById(R.id.buttonSend)

        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CommunityDetailCommentAdapter()
        commentRecyclerView.adapter = adapter

        sendButton2.isEnabled = false

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
                val comment = CommentItem(commentContent, "혜인공주", formattedTime)
                adapter.addComment(comment)
                editText.text.clear()
            }
        }

        adapter = CommunityDetailCommentAdapter(
            onItemDelete = { position ->
                adapter.removeComment(position)
            }
        )
        commentRecyclerView.adapter = adapter

        imageAdapter = ImageAdapter(imageList)
        recyclerViewImages.adapter = imageAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        floatingActionButton.hide()
    }

    override fun onPause() {
        super.onPause()
        floatingActionButton.show()
    }
}
