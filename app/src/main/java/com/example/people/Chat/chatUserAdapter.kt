package com.example.people.Chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.DataClass.RecentChat
import com.example.people.databinding.RecentchatlistBinding

class chatUserAdapter : RecyclerView.Adapter<chatUserAdapter.Viewholder>() {

    private var getUsere = mutableListOf<RecentChat>() // Use MutableList for dynamic updates

    class Viewholder(val binding: RecentchatlistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bind: RecentChat) {
            val context = binding.root.context
            binding.recentChatTextName.text = bind.name
            Glide.with(context).load(bind.friendsimage).into(binding.recentChatImageView)
            binding.recentChatTextTime.text = bind.time!!.substring(0, 5)

            // This is a combination of the user who sent the message and the actual message
            val themessage = bind.message!!.split(" ").take(4).joinToString(" ")
            val makelastmessage = "${bind.person}: ${themessage}"
            binding.recentChatTextLastMessage.text = makelastmessage

            binding.mainChat.setOnClickListener {
                val intent = Intent(context, ChatPageActivity::class.java)
                intent.putExtra("name", bind.name)
                intent.putExtra("uid", bind.friendid)
                intent.putExtra("img", bind.friendsimage)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecentchatlistBinding.inflate(inflater, parent, false)
        return Viewholder(binding)
    }

    override fun getItemCount(): Int {
        return getUsere.size
    }

    // Update the list and notify the adapter
    fun setList(list: List<RecentChat>) {
        getUsere.clear()
        getUsere.addAll(list)
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // Add a new item to the list and notify the adapter
    fun addItem(item: RecentChat) {
        getUsere.add(item)
        notifyItemInserted(getUsere.size - 1) // Notify the adapter that an item was added
    }

    // Update an existing item in the list and notify the adapter
    fun updateItem(item: RecentChat) {
        val index = getUsere.indexOfFirst { it.friendid == item.friendid }
        if (index != -1) {
            getUsere[index] = item
            notifyItemChanged(index) // Notify the adapter that an item was updated
        }
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val bind = getUsere[position]
        holder.bind(bind)
    }
}