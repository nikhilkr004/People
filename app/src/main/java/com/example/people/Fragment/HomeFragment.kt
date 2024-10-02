package com.example.people.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.example.people.Activity.Utils
import com.example.people.Adapters.StoryAdapter
import com.example.people.Adapters.postAdapter
import com.example.people.DataClass.PostItem
import com.example.people.DataClass.Story
import com.example.people.R
import com.example.people.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    val postData = mutableListOf<PostItem>()
    private var storyAdapter: StoryAdapter? = null
    private var storyList: MutableList<Story>? = null

    private var followingList: MutableList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference

        val recyclerView = binding.homeRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val daapter = postAdapter(postData)

        val ref = databaseReference.child("post")


        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postData.clear()
                for (Snapsht in snapshot.children) {
                    val data = Snapsht.getValue(PostItem::class.java)
                    if (data != null) {
                        postData.add(data!!)
                        postData.reverse()
                        binding.shimer.visibility = View.GONE
                        binding.homeRecyclerview.visibility = View.VISIBLE
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        recyclerView.adapter = daapter
        daapter.notifyDataSetChanged()


        //////////////////////////////////////////////////////////////////////////////////


        //// to get the following list  user id
        val followingReferen = FirebaseDatabase.getInstance().reference
        followingList = ArrayList()
        followingReferen.child("Follow").child(Utils.currentUserId()).child("Following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        (followingList as ArrayList<String>).clear()

                        for (Snapshot in snapshot.children) {
                            Snapshot?.key.let {
                                (followingList as ArrayList<String>).add(it!!)

                            }


                        }
                        retriveStorys()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, "error to loding following list", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        ///////////// story item

        var storyRecyclerviw: RecyclerView? = null
        storyRecyclerviw = binding.storyRecycler
        storyRecyclerviw.setHasFixedSize(true)
        val linearlayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        storyRecyclerviw.layoutManager = linearlayout

        storyList = ArrayList()
        storyAdapter = context?.let {
            StoryAdapter(it, storyList as ArrayList<Story>)
        }
        storyRecyclerviw.adapter = storyAdapter

        storyAdapter!!.notifyDataSetChanged()


















        return binding.root
    }

    private fun retriveStorys() {
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story")

        storyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentTime = System.currentTimeMillis()
                (storyList as ArrayList<Story>).clear()

                (storyList as ArrayList<Story>).add(
                    Story(
                        "",
                        0,
                        0,
                        "",
                        FirebaseAuth.getInstance().currentUser?.uid
                    )
                )

                for (id in followingList!!) {
                    var countStory = 0
                    var story: Story? = null

                    for (snapshot in snapshot.child(id).children) {

                        story = snapshot.getValue(Story::class.java)
                        if (currentTime > story!!.timeStart && currentTime < story!!.timeEnd) {
                            countStory++
                        }
                    }


                    if (countStory > 0) {
                        (storyList as ArrayList<Story>).add(story!!)
                    }
                }


                storyAdapter!!.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}