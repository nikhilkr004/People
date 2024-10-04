package com.example.people.Chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.DataClass.messageModel
import com.example.people.R
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date

class MessageAdapter (val messageList:List<messageModel>, val context: Context,val image:String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECIVE = 1
    val ITEM_SEND = 2

    class SendViwh(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image=itemView.findViewById<CircleImageView>(R.id.ivProfile)
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_send_message)
        val tiem=itemView.findViewById<TextView>(R.id.ivTime)

    }

    class ReciveMessage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recivemessage = itemView.findViewById<TextView>(R.id.txt_recive_message)
        val reciveTime=itemView.findViewById<TextView>(R.id.tvDateTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            //infalte recive
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.recive_message, parent, false)
            return ReciveMessage(view)
        } else {
            ///infalte sent
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.send_layout, parent, false)
            return SendViwh(view)


        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentmessage = messageList[position]
        if (holder.javaClass == SendViwh::class.java) {

            ////do stuff sendViewHolder
            val viewholder = holder as SendViwh
            holder.sentMessage.text = currentmessage.message.toString()
            Glide.with(context).load(image).into(holder.image)
            holder.tiem.text=currentmessage.time!!.substring(0,5)

        } else {
            //do stuff reciveViewholder
            val viewholder = holder as ReciveMessage
            holder.recivemessage.text = currentmessage.message
            holder.reciveTime.text=currentmessage.time!!.substring(0,5)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SEND
        } else {
            return ITEM_RECIVE
        }
    }

}
