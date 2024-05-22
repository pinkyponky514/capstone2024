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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.MultiImageAdapter
import com.example.reservationapp.Model.ImageData
import com.example.reservationapp.R

@Suppress("DEPRECATION")
class CommunityPostFragment : Fragment() {

    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var getImageButton: Button
    private val imageDataList = ArrayList<ImageData>()
    private lateinit var adapter: MultiImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_communitypost, container, false)

        submitButton = view.findViewById(R.id.submit_button)
        recyclerView = view.findViewById(R.id.recyclerView)
        getImageButton = view.findViewById(R.id.getImage)

        getImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        submitButton.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData == null) { // Single image selected
                Log.e("single choice: ", data.data.toString())
                val imageUri = data.data
                imageDataList.add(ImageData(imageUri!!))

                adapter = MultiImageAdapter(imageDataList, requireContext())
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            } else { // Multiple images selected
                val clipData = data.clipData
                Log.e("clipData", clipData!!.itemCount.toString())

                if (clipData.itemCount > 10) { // More than 10 images selected
                    Toast.makeText(context, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
                } else {
                    Log.e(TAG, "multiple choice")

                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        try {
                            imageDataList.add(ImageData(imageUri))
                        } catch (e: Exception) {
                            Log.e(TAG, "File select error", e)
                        }
                    }

                    adapter = MultiImageAdapter(imageDataList, requireContext())
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val TAG = "CommunityPostFragment"
    }
}
