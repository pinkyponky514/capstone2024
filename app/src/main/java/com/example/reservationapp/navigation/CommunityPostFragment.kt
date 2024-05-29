package com.example.reservationapp.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.MultiImageAdapter
import com.example.reservationapp.Model.ImageData
import com.example.reservationapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class CommunityPostFragment : Fragment() {

    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var getImageButton: Button
    private lateinit var imageCountTextView: TextView
    private val imageDataList = ArrayList<ImageData>()
    private lateinit var adapter: MultiImageAdapter
    private lateinit var floatingActionButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_communitypost, container, false)

        // FloatingActionButton 참조 가져오기
        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton)

        submitButton = view.findViewById(R.id.submit_button)
        recyclerView = view.findViewById(R.id.recyclerView)
        getImageButton = view.findViewById(R.id.getImage)
        imageCountTextView = view.findViewById(R.id.imageCountTextView)


        getImageButton.setOnClickListener {
            if (imageDataList.size >= 5) {
                Toast.makeText(context, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            }
        }

        submitButton.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        setupRecyclerView()
        updateImageCount()

        return view
    }

    private fun setupRecyclerView() {
        adapter = MultiImageAdapter(imageDataList, requireContext()) { position ->
            if (position >= 0 && position < imageDataList.size) {
                imageDataList.removeAt(position)
                adapter.notifyDataSetChanged()
                updateImageCount()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun updateImageCount() {
        imageCountTextView.text = "${imageDataList.size}/5"
    }

    override fun onResume() {
        super.onResume()
        floatingActionButton.hide() // Hide FloatingActionButton
    }

    override fun onPause() {
        super.onPause()
        floatingActionButton.show() // Show FloatingActionButton
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImages = ArrayList<Uri>()
            if (data.clipData == null) { // Single image selected
                val imageUri = data.data
                if (imageUri != null) {
                    selectedImages.add(imageUri)
                }
            } else { // Multiple images selected
                val clipData = data.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        selectedImages.add(clipData.getItemAt(i).uri)
                    }
                }
            }

            // 이미지 개수 확인
            val totalImages = imageDataList.size + selectedImages.size
            if (totalImages > 5) {
                Toast.makeText(context, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
                return
            }

            // 이미지 추가
            for (uri in selectedImages) {
                imageDataList.add(ImageData(uri))
            }

            adapter.notifyDataSetChanged()
            updateImageCount()
        }
    }




    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val TAG = "CommunityPostFragment"
    }
}
