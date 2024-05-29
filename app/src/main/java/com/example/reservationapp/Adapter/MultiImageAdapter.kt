package com.example.reservationapp.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reservationapp.Model.ImageData
import com.example.reservationapp.R

class MultiImageAdapter(
    private val imageDataList: ArrayList<ImageData>,
    private val context: Context,
    private val onImageDeleted: (Int) -> Unit // 이미지 삭제 콜백 함수
) : RecyclerView.Adapter<MultiImageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = imageDataList[position].uri
        Glide.with(context)
            .load(imageUri)
            .into(holder.imageView)
        holder.deleteButton.setOnClickListener {
            // Safeguard to prevent IndexOutOfBoundsException
            if (position >= 0 && position < imageDataList.size) {
                onImageDeleted(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageDataList.size
    }
}
