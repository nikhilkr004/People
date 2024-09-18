package com.example.people.Activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.people.Adapters.OtherUserAdapter
import com.example.people.DataClass.PostItem
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.ActivityOtherUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OtherUserActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOtherUserBinding.inflate(layoutInflater)
    }
    val userImage = mutableListOf<PostItem>()
    private lateinit var databaseReference: DatabaseReference
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


        val userid = intent.getStringExtra("userid").toString()
        //to get followers and following
        getFollowFollowingCount(userid)

        checkFollowingStatus(userid,binding.followbtn)
        //get user all post
        getUserPost(userid)
        ///follow
        binding.followbtn.setOnClickListener {
            follow(userid,)
        }
        val userdataRef = databaseReference.child("user").child(userid)
        userdataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserData::class.java)
                if (userData != null) {
                    binding.profileShimmer.visibility = View.GONE
                    binding.maincontent.visibility = View.VISIBLE
                    binding.name.text = userData.name.toString()
                    binding.bio.text = userData.bio.toString()
                    Glide.with(this@OtherUserActivity).load(userData.profileImage)
                        .placeholder(R.drawable.user).into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
    private fun follow(userid: String) {

            if (binding.followbtn.text.toString() == "Follow") {
                Utils.currentUserId()?.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(userid)
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(userid)
                                    .child("Followers").child(it1.toString())
                                    .setValue(true).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this@OtherUserActivity,
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
                        .child("Following").child(userid)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(userid)
                                    .child("Followers").child(it1.toString())
                                    .removeValue().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this,
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

    private fun getUserPost(userid: String) {
        val recyclerView = binding.postRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this@OtherUserActivity, 3)
        val otherAdapter = OtherUserAdapter(userImage)
        recyclerView.adapter = otherAdapter
        val ref = databaseReference.child("post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userImage.clear()
                if (snapshot.exists()) {
                    for (Snapshot in snapshot.children) {
                        val image = Snapshot.getValue(PostItem::class.java)
                        if (image != null && image.userID == userid) {
                            userImage.add(image)
                        }
                    }

                    otherAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OtherUserActivity, "something went error", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }


    private fun getFollowFollowingCount(userid: String) {
        val ref = databaseReference.child("Follow").child(userid)
        ref.child("Followers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                binding.followersCount.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        ref.child("Following").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                binding.followingCount.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}