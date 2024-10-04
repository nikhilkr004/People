package com.example.people.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Activity.Utils
import com.example.people.Chat.ChatPageActivity
import com.example.people.DataClass.Comment
import com.example.people.DataClass.Likes
import com.example.people.DataClass.PostItem
import com.example.people.DataClass.UserData
import com.example.people.MainActivity
import com.example.people.R
import com.example.people.databinding.HomeItemBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.logging.Handler

class postAdapter(val data: List<PostItem>) : RecyclerView.Adapter<postAdapter.ViewHolder>() {



    class ViewHolder(val binding: HomeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
        private var userName: String? = null
        private var userImage: String? = null
        val commentsList = mutableListOf<Comment>()
        var doubleClick: Boolean? = false
        fun bind(data: PostItem) {
            val context = binding.root.context

            Glide.with(context).load(data.postImage).placeholder(R.drawable.image)
                .into(binding.postImage)
            binding.time.text = TimeAgo.using(data.time!!.toLong())
            binding.title.text = data.title.toString()


            binding.profileImage.setOnClickListener {
                val intent=Intent(context,ChatPageActivity::class.java)
                intent.putExtra("name",data.name)
                intent.putExtra("uid",data.userID)
                intent.putExtra("img",data.image)
                context.startActivity(intent)
            }


            val ref=databaseReference.child("user").child(data.userID!!)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val info=snapshot.getValue(UserData::class.java)
                        if (info!=null){
                            binding.name.text = info.name.toString()
                            Glide.with(context).load(info.profileImage).placeholder(R.drawable.user)
                                .into(binding.profileImage)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            if (data.userID == Utils.currentUserId()) {
                binding.followbtn.visibility = View.GONE
            }

            binding.textView15.setOnClickListener {
                addComment(context, data.postID, data)
            }
            binding.textView14.setOnClickListener {
                addComment(context, data.postID, data)
            }

            ///get like count on post
            getLikeCount(data, context)

            ///comment image click
            binding.comment.setOnClickListener {
                addComment(context, data.postID, data)
            }
            ///toassdfsdfs
            binding.like.setOnClickListener {
                handleLikeButton(data, context)
            }

            ////double click for  like
            binding.postImage.setOnClickListener {
                if (doubleClick!!) {

                    handleLikeButton(data, context)

                }
                doubleClick = true
                android.os.Handler().postDelayed({ doubleClick = false }, 2000)
            }


            binding.likeCountText.setOnClickListener {
                showLikeUSer(data, context)
            }

            checkUserLikeOrNot(data.postID, context)


            val commentsRef =
                FirebaseDatabase.getInstance().getReference("post").child(data.postID!!)
                    .child("comments")


            commentsRef.addValueEventListener(object : ValueEventListener {
                @SuppressLint("MissingInflatedId")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists() && snapshot.hasChildren()) {
                        val count = snapshot.childrenCount.toString()
                        val frist = snapshot.children.iterator().next()
                        val dataa = frist.getValue(Comment::class.java)

                        if (dataa?.comment!!.equals(0)) {
                            binding.textView14.visibility = View.GONE

                        } else {

                            binding.textView14.text =
                                dataa!!.comment + " and view other " + count + " comment"
                        }
                    }


                }


                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })


            ///get user profile
            getUSerProfile(context)


            ///following and followers feature
            checkFollowingStatus(data.userID, binding.followbtn)
            binding.followbtn.setOnClickListener {
                if (binding.followbtn.text.toString() == "Follow") {
                    Utils.currentUserId()?.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(data.userID!!)
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(data.userID!!)
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                addFollowNotification(data.userID,data.postID)
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
                            .child("Following").child(data.userID!!)
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(data.userID!!)
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

        private fun showLikeUSer(data: PostItem, context: Context?) {
            val likeData = mutableListOf<Likes>()
            val dialog = BottomSheetDialog(context!!)
            val view = LayoutInflater.from(context).inflate(R.layout.like_show_item, null)
            dialog.setCancelable(false)

            val recyclerView = view.findViewById<RecyclerView>(R.id.likeRecycler)
            recyclerView.layoutManager = LinearLayoutManager(context)
            val dataadapter = likeAdapter(likeData)
            recyclerView.adapter = dataadapter

            val ref = FirebaseDatabase.getInstance().reference.child("Likes").child(data.postID!!)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    likeData.clear()
                    if (snapshot.exists()) {
                        for (snapshot in snapshot.children) {
                            val like = snapshot.getValue(Likes::class.java)
                            if (like != null) {
                                likeData.add(like)
                            }
                        }
                        dataadapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()


        }


        private fun getLikeCount(data: PostItem, context: Context?) {
            val likeRef = FirebaseDatabase.getInstance().reference.child("Likes")
                .child(data.postID!!)

            likeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        val count = snapshot.childrenCount.toString()


                        val frist = snapshot.children.iterator().next()
                        val dataa = frist.getValue(Likes::class.java)
                        if (count.equals(0)) {
                            binding.likeCountText.visibility = View.GONE
                        } else {
                            binding.likeCountText.text =
                                "Liked by " + dataa!!.name + " and " + count + " other"

                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        private fun handleLikeButton(data: PostItem, context: Context?) {

            val ref =
                FirebaseDatabase.getInstance().getReference("user").child(Utils.currentUserId())
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val info = snapshot.getValue(UserData::class.java)
                        if (info != null) {
                            setImageWhenLikeClick(info.name, info.profileImage, data, context)
                        }
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        private fun setImageWhenLikeClick(
            name: String?,
            profileImage: String?,
            data: PostItem,
            context: Context?
        ) {
            val LikeData = Likes(
                name = name,
                image = profileImage,
                user = Utils.currentUserId()
            )
            if (binding.like.tag == "Like") {
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(data.postID!!)
                    .child(Utils.currentUserId())
                    .setValue(LikeData).addOnCompleteListener {
                        if (it.isSuccessful){
                            addLikeNotification(data.userID,data.postID)
                        }
                    }
            } else {
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(data.postID!!)
                    .child(Utils.currentUserId())
                    .removeValue()


            }

        }

        private fun checkUserLikeOrNot(postID: String?, context: Context) {
            val likeRef = FirebaseDatabase.getInstance().reference
                .child("Likes").child(postID!!)

            likeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(Utils.currentUserId()).exists()) {
                        binding.like.setImageResource(R.drawable.fillheart)
                        binding.like.tag = "Liked"
                    } else {
                        binding.like.setImageResource(R.drawable.heart)
                        binding.like.tag = "Like"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


        private fun addComment(context: Context, postID: String?, data: PostItem) {
            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.comment_item, null)
            dialog.setCancelable(false)
            var comment = view.findViewById<EditText?>(R.id.editTextText)
            comment.requestFocus()

            val sendbtn = view.findViewById<ImageView>(R.id.imageView5)
            val shimmer = view.findViewById<View>(R.id.shimmer)

            val database = FirebaseDatabase.getInstance()

            val commentsRef =
                database.getReference("post").child(postID!!).child("comments")

// Create a unique key for the new comment
            val commentId = commentsRef.push().key

// Create the comment object


// this is used for adding the use for for adding

            sendbtn.setOnClickListener {
                Utils.showDialog(context, "posting comment")


                var ActualCumment = comment.text.toString()
                comment.setText("")


                val newComment = Comment(
                    userName = userName,
                    userId = Utils.currentUserId(),
                    comment = ActualCumment
                )

                commentId?.let {


                    commentsRef.child(it).setValue(newComment)
                        .addOnSuccessListener {
                            addCommentNotification(data.userID,data.postID,ActualCumment)

                            val newcomment = data.comment + 1
                            data.comment = newcomment
                            val ref = FirebaseDatabase.getInstance().reference
                            ref.child("post").child(postID).child("comment")
                                .setValue(newcomment).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Utils.hideDialog()

                                    }
                                }

                            //this is used for comment featuer
                        }
                        .addOnFailureListener {
                            // Handle failure
                        }
                }


            }


            val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerViewCommrnt)
            recyclerview.layoutManager = LinearLayoutManager(context)
            val commentAdapter = CommentAdapter(commentsList)
            recyclerview.adapter = commentAdapter


            // Listen for changes in comments
            commentsRef.addValueEventListener(object : ValueEventListener {
                @SuppressLint("MissingInflatedId")
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentsList.clear()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        comment?.let {
                            commentsList.add(it)
                            shimmer.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }

                    }


                    // Update UI with commentsList
                    commentAdapter.notifyDataSetChanged()
                }


                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })



            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        private fun addCommentNotification(userID: String?, postID: String?, actualCumment: String) {
            val notiRef=FirebaseDatabase.getInstance().reference
                .child("Notification")
                .child(userID!!)

            val notiMap=HashMap<String,Any>()
            notiMap["userid"]=Utils.currentUserId()
            notiMap["text"]="comment on your post"
            notiMap["postid"]=postID!!
            notiMap["sign"]=actualCumment
            notiMap["ispost"]=true


            notiRef.push().setValue(notiMap)

        }
        private fun addLikeNotification(userID: String?,postID: String?){
            val notiRef=FirebaseDatabase.getInstance().reference
                .child("Notification")
                .child(userID!!)

            val notiMap=HashMap<String,Any>()
            notiMap["userid"]=Utils.currentUserId()
            notiMap["text"]="like your post"
            notiMap["postid"]=postID!!
            notiMap["sign"]=""
            notiMap["ispost"]=true


            notiRef.push().setValue(notiMap)
        }

        private fun addFollowNotification(userID: String?,postID: String?){
            val notiRef=FirebaseDatabase.getInstance().reference
                .child("Notification")
                .child(userID!!)

            val notiMap=HashMap<String,Any>()
            notiMap["userid"]=Utils.currentUserId()
            notiMap["text"]="start following you"
            notiMap["postid"]=userID
            notiMap["sign"]=""
            notiMap["ispost"]=true


            notiRef.push().setValue(notiMap)
        }

        private fun getUSerProfile(context: Context) {
            val ref =
                FirebaseDatabase.getInstance().reference.child("user").child(Utils.currentUserId())
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(UserData::class.java)
                    Glide.with(context).load(data!!.profileImage).placeholder(R.drawable.user)
                        .into(binding.currentUsreProfile)


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        private fun checkFollowingStatus(userID: String?, followbtn: TextView) {
            val folowingRef = Utils.currentUserId()?.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following")
            }

            folowingRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(userID!!).exists()) {
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
        val binding = HomeItemBinding.inflate(inflater)
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