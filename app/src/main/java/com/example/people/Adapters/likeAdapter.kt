package com.example.people.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.DataClass.Comment
import com.example.people.DataClass.Likes
import com.example.people.DataClass.UserData
import com.example.people.databinding.CommentShowItemBinding
import com.example.people.databinding.LikeItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class likeAdapter (val comment:List<Likes>): RecyclerView.Adapter<likeAdapter.ViewHolder>() {
    class ViewHolder(val binding: LikeItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Likes) {
            val userID =comment.user!!
            val context = binding.root.context

            val database = FirebaseDatabase.getInstance().getReference("user")

            database.child(userID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data =snapshot.getValue(UserData::class.java)

                    Glide.with(context).load(data?.profileImage).into(binding.profileImage)
                    binding.name.text=data?.name
                    binding.textView18.text=data?.bio


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding= LikeItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return comment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment=comment[position]
        holder.bind(comment)
    }
}