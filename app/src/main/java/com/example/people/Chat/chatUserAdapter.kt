package com.example.people.Chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.DataClass.RecentChat
import com.example.people.databinding.LikeItemBinding
import com.example.people.databinding.RecentchatlistBinding

class chatUserAdapter:RecyclerView.Adapter<chatUserAdapter.Viewholder>() {

    var getUsere = listOf<RecentChat>()
    var chatSheetModel=RecentChat()
    class Viewholder(val binding: RecentchatlistBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(bind: RecentChat) {
            val context=binding.root.context
            binding.recentChatTextName.text=bind.name
            Glide.with(context).load(bind.friendsimage).into(binding.recentChatImageView)
            binding.recentChatTextTime.text=bind.time!!.substring(0,5)

            //this is  combination of the user who send message and the actual message
            
            val themessage = bind.message!!.split(" ").take(4).joinToString(" ")
            val makelastmessage = "${bind.person}: ${themessage} "
            binding.recentChatTextLastMessage.text=makelastmessage

            binding.mainChat.setOnClickListener {
                val intent= Intent(context,ChatPageActivity::class.java)
                intent.putExtra("name",bind.name)
                intent.putExtra("uid",bind.friendid)
                intent.putExtra("img",bind.friendsimage)
                context.startActivity(intent)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=RecentchatlistBinding.inflate(inflater)
        return Viewholder(binding)
    }

    override fun getItemCount(): Int {
        return getUsere.size
    }
    fun setList(list: List<RecentChat>) {
        this.getUsere = list
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val bind=getUsere[position]
        chatSheetModel=bind
        holder.bind(bind)
    }

}