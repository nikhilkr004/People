package com.example.people.Adapters

import android.content.Intent
import android.provider.Telephony.Mms.Intents
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Activity.OtherUserActivity
import com.example.people.Activity.Utils
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.UserFollowItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class searchAdapter(var data: List<UserData>) : RecyclerView.Adapter<searchAdapter.ViewHolder>() {

    fun setFilterList(data: MutableList<UserData>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: UserFollowItemBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(data: UserData) {
            val context = binding.root.context
            binding.name.text = data.name
            binding.textView18.text = data.userName
            Glide.with(context).load(data.profileImage).placeholder(R.drawable.user)
                .into(binding.profileImage)

            binding.main.setOnClickListener {
                val intent = Intent(context, OtherUserActivity::class.java)
                intent.putExtra("userid", data.userId)
                context.startActivity(intent)
            }



            checkFollowingStatus(data.userId, binding.followbtn)
    
            binding.followbtn.setOnClickListener {
                if (binding.followbtn.text.toString() == "Follow") {
                    Utils.currentUserId()?.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(data.userId!!)
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(data.userId!!)
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Following successful...",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }
                    }
                } else {
                    Utils.currentUserId()?.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(data.userId!!)
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(data.userId!!)
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "UnFollowing successful...",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }
                    }
                }
            }
        }

        private fun checkFollowingStatus(userId: String?, followbtn: TextView) {

            val folowingRef = Utils.currentUserId()?.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following")
            }

            folowingRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(userId!!).exists()) {
                        followbtn.text = "Following"
                    } else {
                        followbtn.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserFollowItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data[position]
        holder.bind(data)
    }
}