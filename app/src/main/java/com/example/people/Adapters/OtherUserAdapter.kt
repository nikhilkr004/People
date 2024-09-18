package com.example.people.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Activity.OtherUserActivity
import com.example.people.DataClass.PostItem
import com.example.people.R
import com.example.people.databinding.ProfileImageItemBinding

class OtherUserAdapter(val data:List<PostItem>):RecyclerView.Adapter<OtherUserAdapter.ViewHolder>() {
    class ViewHolder(val binding:ProfileImageItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PostItem) {
            val context=binding.root.context
            Glide.with(context).load(data.postImage).placeholder(R.drawable.image).into(binding.imageView10)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val inflater=LayoutInflater.from(parent.context)
        val binding =ProfileImageItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val data=data[position]
        holder.bind(data)
    }

}