package com.example.people.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Adapters.CommentAdapter
import com.example.people.Adapters.likeAdapter
import com.example.people.DataClass.Comment
import com.example.people.DataClass.Likes
import com.example.people.DataClass.PostItem
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.ActivityShowImageInFullPageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ShowImageInFullPageActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityShowImageInFullPageBinding.inflate(layoutInflater)
    }
    private var name: String? = null
    private val commentsList = mutableListOf<Comment>()
    private lateinit var databaseReference: DatabaseReference
    var doubleClick: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseReference = FirebaseDatabase.getInstance().reference
        val postId = intent.getStringExtra("id").toString()


        ///show comment in activity
        showCommentInActivity(postId)

        ///get post data
        getPostData(postId)



        binding.textView15.setOnClickListener {
            addComment(postId)
        }
        binding.textView14.setOnClickListener {
            addComment(postId)
        }

        ///get like count on post
        getLikeCount(postId)

        ///comment image click
        binding.comment.setOnClickListener {
            addComment(postId)
        }
        ///toassdfsdfs
        binding.like.setOnClickListener {
            handleLikeButton(postId)
        }

        ////double click for  like
        binding.postImage.setOnClickListener {
            if (doubleClick!!) {

                handleLikeButton(postId)

            }
            doubleClick = true
            android.os.Handler().postDelayed({ doubleClick = false }, 2000)
        }


        binding.likeCountText.setOnClickListener {
            showLikeUSer(postId)
        }

        checkUserLikeOrNot(postId)


    }

    private fun showCommentInActivity(postId: String) {
        val commentsRef =
            FirebaseDatabase.getInstance().getReference("post").child(postId)
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
    }

    private fun checkUserLikeOrNot(postID: String?) {
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

    private fun showLikeUSer(data: String) {
        val likeData = mutableListOf<Likes>()
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.like_show_item, null)
        dialog.setCancelable(false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.likeRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val dataadapter = likeAdapter(likeData)
        recyclerView.adapter = dataadapter

        val ref = FirebaseDatabase.getInstance().reference.child("Likes").child(data)
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

    private fun getLikeCount(data: String) {
        val likeRef = FirebaseDatabase.getInstance().reference.child("Likes")
            .child(data)

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

    private fun getPostData(postId: String) {
        val ref = databaseReference.child("post").child(postId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(PostItem::class.java)
                    if (data != null) {
                        Glide.with(this@ShowImageInFullPageActivity).load(data.postImage)
                            .placeholder(R.drawable.image).into(binding.postImage)

                        getPostUserInfo(data.userID)

                        // deleting an item
                        menuBtn(data)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun menuBtn(data: PostItem) {
        val ref = databaseReference.child("post").child(data.postID!!)
        if (data.userID == Utils.currentUserId()) {
            binding.menuBtn.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete post ?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, id ->
                        ref.removeValue()
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, id -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            }

        } else {
            binding.menuBtn.visibility = View.GONE
        }
    }

    private fun getPostUserInfo(userID: String?) {
        databaseReference.child("user").child(userID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(UserData::class.java)
                        if (data != null) {
                            binding.name.text = data.name
                            Glide.with(this@ShowImageInFullPageActivity).load(data.profileImage)
                                .placeholder(R.drawable.user).into(binding.profileImage)
                            Glide.with(this@ShowImageInFullPageActivity).load(binding.profileImage)
                                .placeholder(R.drawable.user).into(binding.currentUsreProfile)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun handleLikeButton(data: String) {

        val ref =
            FirebaseDatabase.getInstance().getReference("user").child(Utils.currentUserId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val info = snapshot.getValue(UserData::class.java)
                    if (info != null) {
                        name = info.name.toString()
                        setImageWhenLikeClick(info.name, info.profileImage, data)
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
        data: String
    ) {
        val LikeData = Likes(
            name = name,
            image = profileImage,
            user = Utils.currentUserId()
        )
        if (binding.like.tag == "Like") {
            FirebaseDatabase.getInstance().reference.child("Likes")
                .child(data)
                .child(Utils.currentUserId())
                .setValue(LikeData)
        } else {
            FirebaseDatabase.getInstance().reference.child("Likes")
                .child(data)
                .child(Utils.currentUserId())
                .removeValue()


        }

    }

    private fun addComment(postID: String?) {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.comment_item, null)
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
            Utils.showDialog(this, "posting comment")


            val ActualCumment = comment.text.toString()
            comment.setText("")


            val newComment = Comment(
                userName = name,
                userId = Utils.currentUserId(),
                comment = ActualCumment
            )

            commentId?.let {


                commentsRef.child(it).setValue(newComment)
                    .addOnSuccessListener {
                        Utils.hideDialog()
                    }
                    .addOnFailureListener {
                        // Handle failure
                    }
            }


        }


        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerViewCommrnt)
        recyclerview.layoutManager = LinearLayoutManager(this)
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
}