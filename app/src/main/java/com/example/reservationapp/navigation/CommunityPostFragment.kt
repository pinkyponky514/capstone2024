package com.example.reservationapp.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.reservationapp.App
import com.example.reservationapp.Model.BoardContent
import com.example.reservationapp.Model.BoardResponse
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Adapter.MultiImageAdapter
import com.example.reservationapp.Model.APIService
import com.example.reservationapp.Model.AllBoardResponse
import com.example.reservationapp.Model.BoardPost
import com.example.reservationapp.Model.ImageData
import com.example.reservationapp.R
import com.example.reservationapp.Retrofit.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@Suppress("DEPRECATION")
class CommunityPostFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var getImageButton: Button
    private lateinit var imageCountTextView: TextView
    private val imageDataList = ArrayList<ImageData>()
    private lateinit var adapter: MultiImageAdapter
    private lateinit var floatingActionButton: FloatingActionButton

    //Retrofit
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var apiService: APIService

    private var imageFiles: List<File> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_communitypost, container, false)

        // FloatingActionButton 참조 가져오기
        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton)

        //Retrofit
        retrofitClient = RetrofitClient.getInstance()
        apiService = retrofitClient.getRetrofitInterface()

        //초기화
        submitButton = view.findViewById(R.id.submit_button)
        recyclerView = view.findViewById(R.id.recyclerView)
        getImageButton = view.findViewById(R.id.getImage)
        imageCountTextView = view.findViewById(R.id.imageCountTextView)

        getImageButton.setOnClickListener {
            if (imageDataList.size >= 10) {
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
            // FragmentManager를 사용하여 CommunityFragment로 이동
            fragmentManager?.popBackStack()

            val title = view.findViewById<EditText>(R.id.editTextTitle).text.toString()
            val content = view.findViewById<EditText>(R.id.editTextContent).text.toString()

            val boardContent = BoardPost(title, content)

            val imagepart = imageFiles.size
            val imageParts = imageFiles.map { file ->
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestBody)
            }



            apiService.postBoard(imageParts, boardContent).enqueue(object :
                Callback<BoardResponse> {
                override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()!!
                        val message = responseBody.message

                       // fragmentManager?.popBackStack() // 이전 프래그먼트로 이동

                    } else {
                        handleErrorResponse(response)
                        Log.d("FAILURE Response", "Connect SUCESS, Response FAILURE, body: ${response.body().toString()}") //통신 성공, 응답은 실패
                    }
                }

                override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                }
            })
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

    //
    private fun handleErrorResponse(response: Response<BoardResponse>) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImages = ArrayList<Uri>()
            if (data.clipData == null) { // Single image selected
                data.data?.let { selectedImages.add(it) }
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

            // 이미지 추가 및 업로드
            imageFiles = selectedImages.mapNotNull { uri ->
                val imagePath = getRealPathFromURI(uri)
                if (imagePath != null) File(imagePath) else null
            }

            // RecyclerView 업데이트
            for (uri in selectedImages) {
                imageDataList.add(ImageData(uri))
            }
//            if (imageFiles.isNotEmpty()) {
//                uploadImages(imageFiles)
//            }
            adapter.notifyDataSetChanged()
            updateImageCount()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val TAG = "CommunityPostFragment"
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val cursor = activity?.contentResolver?.query(contentUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
}
