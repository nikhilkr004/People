package com.example.people.Chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.people.Activity.Utils
import com.example.people.Adapters.UserAndNotesAdapter
import com.example.people.DataClass.NotesData
import com.example.people.DataClass.RecentChat
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.ActivityChatHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.remote.WatchChange


class ChatHomeActivity : AppCompatActivity()  {
    private  val binding by lazy {
        ActivityChatHomeBinding.inflate(layoutInflater)
    }
    val firestore = FirebaseFirestore.getInstance()
    lateinit var recentadapter : chatUserAdapter
    private var followingList: MutableList<String>? = null

    private var FollowUserList= mutableListOf<UserData>()
    private lateinit var databaseReference: DatabaseReference
    private val userdata = mutableListOf<RecentChat>()
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imageView11.setOnClickListener { finish() }


        followingList = ArrayList()

        getFollowingUser()



        val recyclerView = binding.chatrecycler
        recyclerView.layoutManager = LinearLayoutManager(this)

        recentadapter = chatUserAdapter()
        recyclerView.adapter = recentadapter

// Add a real-time listener to the Firestore collection
        firestore.collection("Conversation${Utils.currentUserId()}")
            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Handle the error
                    Log.e("FirestoreError", "Error fetching data: ${error.message}")
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    for (documentChange in querySnapshot.documentChanges) {
                        val post = documentChange.document.toObject(RecentChat::class.java)
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                // Add new item to the adapter
                                recentadapter.addItem(post)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                // Update existing item in the adapter
                                recentadapter.updateItem(post)
                            }
                            DocumentChange.Type.REMOVED -> {
                                // Handle removed items if needed
                            }
                        }
                    }
                }
            }


    }

    private fun getFollowingUser() {



        val followingRef = FirebaseDatabase.getInstance().getReference("Follow").child(Utils.currentUserId()).child("Following")
        val usersRef = FirebaseDatabase.getInstance().getReference("Notes")

// List to hold the followed users' data
        val followedUsersList = mutableListOf<NotesData>()


        val recyclerView=binding.allUserRecyclerview
        recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val userAdapter=UserAndNotesAdapter(followedUsersList)
        recyclerView.adapter=userAdapter
// Query to get followed users' IDs
        followingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                followedUsersList.clear()

                for (followSnapshot in snapshot.children) {
                    val followedUserId = followSnapshot.key

                    // Fetch user details for each followed user
                    usersRef.child(followedUserId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val user = userSnapshot.getValue(NotesData::class.java)
                            if (user != null) {
                                followedUsersList.add(user)
                            }
                            // Update the RecyclerView when all user data has been fetched
                            userAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Error fetching followed user details: ${error.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching followed users: ${error.message}")
            }
        })






    }


}




