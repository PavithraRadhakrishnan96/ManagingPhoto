package com.example.managingphotoapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.managingphotoapp.databinding.LayoutPhotoBinding

class PhotoListAdapter(var cvNotes: CardView,var clickListner: ClickListner) : RecyclerView.Adapter<PhotoListAdapter.ViewHolder>() {

    var imageList = ArrayList<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutPhotoBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ivImage.setImageURI(imageList[position])
        holder.binding.ivCancel.setOnClickListener {
            clickListner.removePosition(position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
    fun setImageList(imageList: List<Uri>) {
        if(imageList.isNotEmpty())cvNotes.visibility= View.GONE
        else cvNotes.visibility= View.VISIBLE
        this.imageList = imageList as ArrayList<Uri>
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: LayoutPhotoBinding) : RecyclerView.ViewHolder(binding.root)

}