package com.example.people.Chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthurivanets.adapster.listeners.OnItemClickListener
import com.example.people.Activity.Utils
import com.example.people.Adapters.StoryAdapter
import com.example.people.Adapters.UserAndNotesAdapter
import com.example.people.DataClass.RecentChat
import com.example.people.DataClass.Story
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.ActivityChatHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

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
        followingList = ArrayList()

        getFollowingUser()



        val recyclerView=binding.chatrecycler
        recyclerView.layoutManager=LinearLayoutManager(this)

        recentadapter=chatUserAdapter()

        recentadapter.setList(userdata)
        recyclerView.adapter=recentadapter



        firestore.collection("Conversation${Utils.currentUserId()}").orderBy("time",com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().addOnSuccessListener {

                query->
                for (document in query){
                    val post =document.toObject(RecentChat::class.java)
                    if (post.sender!!.equals(Utils.currentUserId())) userdata.add(post)
                    recentadapter.notifyDataSetChanged()
                }


            }



    }

    private fun getFollowingUser() {



        val followingRef = FirebaseDatabase.getInstance().getReference("Follow").child(Utils.currentUserId()).child("Following")
        val usersRef = FirebaseDatabase.getInstance().getReference("user")

// List to hold the followed users' data
        val followedUsersList = mutableListOf<UserData>()


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
                            val user = userSnapshot.getValue(UserData::class.java)
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

 private   fun  GetUSerData() {

     val recyclerView=binding.allUserRecyclerview
     recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
     val adapters=UserAndNotesAdapter(FollowUserList as ArrayList<UserData>)
     recyclerView.adapter=adapters

     val ref=FirebaseDatabase.getInstance().reference.child("user")
     ref.addValueEventListener(object : ValueEventListener {
         override fun onDataChange(snapshot: DataSnapshot) {
         FollowUserList.clear()

             for (id in followingList!!){

                 for (Snapshot in snapshot.child(id).children){
                     val data=Snapshot.getValue(UserData::class.java)
                     if (data!=null){
                      FollowUserList.add(data)
                     }
                 }
                 adapters.notifyDataSetChanged()
             }

         }

         override fun onCancelled(error: DatabaseError) {
             TODO("Not yet implemented")
         }
     })

    }

}




