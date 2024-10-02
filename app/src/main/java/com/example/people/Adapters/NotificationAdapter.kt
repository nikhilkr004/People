package com.example.people.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Activity.OtherUserActivity
import com.example.people.Activity.ShowImageInFullPageActivity
import com.example.people.DataClass.Notification_item
import com.example.people.DataClass.PostItem
import com.example.people.DataClass.UserData
import com.example.people.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter (private var mContext: Context, private  val mNotification:List<Notification_item>):
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView
        var profileImage: CircleImageView
        var userName: TextView
        var text: TextView


        init {
            postImage=itemView.findViewById(R.id.notification_post_image)
            profileImage=itemView.findViewById(R.id.notifiction_profile_image)
            userName=itemView.findViewById(R.id.udername_notification)
            text=itemView.findViewById(R.id.comment_notification)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotification.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notification = mNotification[position]

        if (notification.gettext().equals("start following you")){
            holder.text.text="start following you"
        }

        else if(notification.gettext().equals("like your post")){
            holder.text.text="like your post"

        }
        else if(notification.gettext().equals("comment on your post ")){
            holder.text.text="comment on your post"+notification.getSign()

        }
        // if the all condition is false then this will be execute
        else{
            holder.text.text=notification.gettext()
        }

        holder.postImage.setOnClickListener {
            /// show post
            if (notification.getispost()){
                val intent = Intent(mContext,ShowImageInFullPageActivity::class.java)
                intent.putExtra("id",notification.getpostid())
                mContext.startActivity(intent)
            }
        }


        holder.profileImage.setOnClickListener {
            val intent =Intent(
                mContext,OtherUserActivity::class.java
            )

            intent.putExtra("userid",notification.getuserid())
            mContext.startActivity(intent)
        }


        userInfo(holder.profileImage,holder.userName,notification.getuserid())
        if (notification.getispost()){
            holder.postImage.visibility= View.VISIBLE
            getPost(holder.postImage,notification.getpostid())
        }

        else{
            holder.postImage.visibility= View.GONE
        }
    }
    private fun userInfo(imageView: ImageView, username: TextView, publisherId:String){
        var currentUSer= FirebaseAuth.getInstance().currentUser?.uid
        val userRef= FirebaseDatabase.getInstance().reference.child("user").child(publisherId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val user =snapshot.getValue(UserData::class.java)
                    Glide.with(mContext).load(user!!.profileImage).placeholder(R.drawable.user).into(imageView)
                    username.text=user!!.name


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    private fun getPost(imageView: ImageView, postId:String){
        val ref = FirebaseDatabase.getInstance().reference
            .child("post")
            .child(postId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val post=snapshot.getValue(PostItem::class.java)
                    Glide.with(mContext).load(post!!.postImage).placeholder(R.drawable.image).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}