package com.example.reservationapp.Adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.R

private var hospital_detail_image_list = ArrayList<Bitmap>()

class HospitalDetailImageAdapter: RecyclerView.Adapter<HospitalDetailImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var hospitalDetailImage: ImageView

        init {
            hospitalDetailImage = itemView.findViewById(R.id.hospital_detail_image_item)
        }

        fun setContents(list: Bitmap) {
            hospitalDetailImage.setImageBitmap(list)
        }
    }


    //
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_hospital_detail_image, viewGroup, false)
        return ViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: HospitalDetailImageAdapter.ViewHolder, position: Int) {
        holder.setContents(hospital_detail_image_list[position])
    }
    override fun getItemCount(): Int {
        return hospital_detail_image_list.size
    }



    //
    fun updatelist(newList: ArrayList<Bitmap>) {
        hospital_detail_image_list = newList // as ArrayList<HospitalDetailImageItem>
        notifyDataSetChanged()
    }
}