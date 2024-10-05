package com.example.people.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Chat.ChatPageActivity
import com.example.people.DataClass.NotesData
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.NotesAndProfileItemBinding
import com.example.people.databinding.StoryItemBinding
import com.google.firebase.database.FirebaseDatabase

class UserAndNotesAdapter(val data:List<NotesData>):RecyclerView.Adapter<UserAndNotesAdapter.ViewHolder>() {
    class ViewHolder(val binding:NotesAndProfileItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NotesData) {
            val context=binding.root.context
            binding.usernameTextView.text=data.name.toString()
            binding.noteTextView.text=data.notes.toString()
            Glide.with(context).load(data.userImage).placeholder(R.drawable.user).into(binding.profileImageView)

            binding.notes.setOnClickListener {
                val intent= Intent(context, ChatPageActivity::class.java)
                intent.putExtra("name",data.name)
                intent.putExtra("uid",data.userId)
                intent.putExtra("img",data.userImage)
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val inflater=LayoutInflater.from(parent.context)
        val binding = NotesAndProfileItemBinding.inflate(inflater)
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