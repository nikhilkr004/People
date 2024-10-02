package com.example.people.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Activity.Utils
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.StoryLikeItemBinding
import com.github.marlonlom.utilities.timeago.TimeAgo

class StoryLikeAdapter(val comment: List<UserData>) :
    RecyclerView.Adapter<StoryLikeAdapter.ViewHolder>() {
    class ViewHolder(val binding: StoryLikeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: UserData) {



                if (comment.name.equals("")) {
                    binding.name.text = "unknown"
                } else {
                    binding.name.text = comment.name.toString()
                }


                val context = binding.root.context

                Glide.with(context).load(comment.profileImage).placeholder(R.drawable.user)
                    .into(binding.profileimage)
                binding.time.text = TimeAgo.using(comment.bio!!.toLong())



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StoryLikeItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return comment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comment[position]
        holder.bind(comment)
    }
}