package com.example.people.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Activity.AddStoryActivity
import com.example.people.Activity.StoryActivity
import com.example.people.DataClass.Story
import com.example.people.DataClass.UserData
import com.example.people.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter (private val mContext: Context, private val stories: ArrayList<Story>):
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        ///story item

        var  story_image_seen: CircleImageView?= null
        var story_image: CircleImageView?=null
        var story_Username: TextView?=null

        //add story item
        var story_plus_btn: CircleImageView?=null
        var addStoryText: TextView?=null



        init {

            ///story item

            story_image=itemView.findViewById(R.id.story_image)
            story_image_seen=itemView.findViewById(R.id.story_image_seen)
            story_Username=itemView.findViewById(R.id.story_name)

            ///add story item
            story_plus_btn=itemView.findViewById(R.id.addStoryItem)
            addStoryText=itemView.findViewById(R.id.addstoryText)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType==0){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.add_story_item,parent,false)
            ViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.story_item,parent,false)
            ViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story=stories[position]
        userInfo(holder,story.userId!!,position)

        holder.story_image_seen




        if (holder.adapterPosition !==0){
            seenStory(holder,story.storyId!!)
        }
        if (holder.adapterPosition===0){
            myStories(holder.addStoryText!!,holder.story_plus_btn!!,false)
        }



        holder.itemView.setOnClickListener {
            if (holder.adapterPosition===0){
                myStories(holder.addStoryText!!,holder.story_plus_btn!!,true)
            }
            else{
                val intent = Intent(mContext,StoryActivity::class.java)
                intent.putExtra("userId",story.userId)
                mContext.startActivity(intent)
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position==0){
            return 0
        }
        return 1
    }

    override fun getItemCount(): Int {
        return stories.size

    }
    private fun userInfo(viewHolder:ViewHolder, userId:String,position: Int){
        var currentUSer= FirebaseAuth.getInstance().currentUser?.uid
        val userRef= FirebaseDatabase.getInstance().reference.child("user").child(userId!!)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val user =snapshot.getValue(UserData::class.java)



                    Glide.with(mContext).load(user!!.profileImage).placeholder(R.drawable.user).into(viewHolder.story_image!!)

                    if (position!=0){
                        Glide.with(mContext).load(user.profileImage).placeholder(R.drawable.user).into(viewHolder.story_image_seen!!)
                        viewHolder.story_Username!!.text=user.name.toString()

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }




    private fun myStories(textView: TextView, imageView: ImageView, click:Boolean){
        val currentuser= FirebaseAuth.getInstance().currentUser!!.uid
        val storyRef= FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(currentuser)





        storyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var counter=0
                val currentTime=System.currentTimeMillis()
                for (snapshot in snapshot.children){
                    val story=snapshot.getValue(Story::class.java)
                    if (currentTime>story!!.timeStart && currentTime<story!!.timeEnd){
                        counter++
                    }
                }
                if (click){
                    if (counter>0) {
                        val alertDialog = AlertDialog.Builder(mContext).create()
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEUTRAL,
                            "View Story"
                        ) { alertDialog, which ->
                            val intent = Intent(mContext, StoryActivity::class.java)
                            intent.putExtra("userId", currentuser)
                            mContext.startActivity(intent)

                            alertDialog.dismiss()
                        }


                        alertDialog.setButton(
                            AlertDialog.BUTTON_POSITIVE,
                            "Add Story"
                        ) { alertDialog, which ->
                            val intent = Intent(mContext, AddStoryActivity::class.java)
                            intent.putExtra("userId", currentuser)
                            mContext.startActivity(intent)

                            alertDialog.dismiss()
                        }
                        alertDialog.show()
                    }
                    else{
                        val intent = Intent(mContext, AddStoryActivity::class.java)
                        intent.putExtra("userId", currentuser)
                        mContext.startActivity(intent)
                    }

                }

                else{
                    if (counter>0){
                        textView.text="Your Story"
                        imageView.visibility= View.GONE
                    }
                    else{
                        textView.text="Add Story"
                        imageView.visibility= View.VISIBLE
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun seenStory(viewHolder:ViewHolder,userId: String){
        val storyRef= FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId)
        storyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var i=0
                for (snapshot in snapshot.children){
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).exists()
                        && System.currentTimeMillis()<snapshot.getValue(Story::class.java)!!.timeEnd)
                    {
                        i=i+1
                    }
                }
                if (i>0){
                    viewHolder.story_image!!.visibility= View.VISIBLE
                    viewHolder.story_image_seen!!.visibility= View.GONE
                }
                else{
                    viewHolder.story_image!!.visibility= View.GONE
                    viewHolder.story_image_seen!!.visibility= View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}