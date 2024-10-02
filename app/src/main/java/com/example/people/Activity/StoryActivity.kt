package com.example.people.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.people.Adapters.StoryLikeAdapter
import com.example.people.DataClass.Story
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.ActivityStoryBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import jp.shts.android.storiesprogressview.StoriesProgressView

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {
    private val binding by lazy {
        ActivityStoryBinding.inflate(layoutInflater)
    }
    var storySennUser = mutableListOf<UserData>()
    var currentUserId: String = ""
    var userId: String = ""
    var seenuserCount: Long = 0
    var imageList: List<String>? = null
    var storiesIds: List<String>? = null
    var username: String? = null
    var userimage: String? = null
    var storyProgressView: StoriesProgressView? = null
    var counter = 0
    private var pressTime = 0L
    private var limit = 1000L


    private val onTouchListener = View.OnTouchListener { v, event ->

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storyProgressView!!.pause()
                return@OnTouchListener false
            }

            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storyProgressView!!.resume()
                return@OnTouchListener limit < now - pressTime
            }

            else -> {
                false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        storyProgressView = binding.storiesProgress
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        userId = intent.getStringExtra("userId").toString()

        binding.layoutSeen.visibility = View.GONE
        binding.storyDelete.visibility = View.GONE

        if (userId == currentUserId) {
            binding.layoutSeen.visibility = View.VISIBLE
            binding.storyDelete.visibility = View.VISIBLE

        }

        binding.layoutSeen.setOnClickListener {
            showUserInformation(userId, storiesIds!!.get(counter), seenuserCount)
        }


        val currentuser = FirebaseAuth.getInstance().currentUser!!.uid

        val ref = FirebaseDatabase.getInstance().reference.child("users").child(currentuser)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserData::class.java)

                    username = user!!.name
                    userimage = user.profileImage

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        getStories(userId)
        userInfo(userId)


        ///delete story

        binding.storyDelete.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().reference
                .child("Story")
                .child(userId)
                .child(storiesIds!![counter])

            ref.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Deleted...", Toast.LENGTH_SHORT).show()
                }
            }
        }


//////reverse feature
        val reverse = binding.reverse
        reverse.setOnClickListener { storyProgressView!!.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        //// next or skip feature
        val skip = binding.skip
        skip.setOnClickListener { storyProgressView!!.skip() }
        skip.setOnTouchListener(onTouchListener)


        binding.seenNumber.setOnClickListener {

//            showUserInformation(userId,storiesIds!!.get(counter),seenuserCount)

            storyProgressView!!.pause()
        }
        storyProgressView!!.resume()
    }

    private fun showUserInformation(userId: String, storiesIds: String, seenuserCount: Long) {

        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.story_like_all_user, null)
        dialog.setCancelable(false)
        storyProgressView!!.pause()


        val recyclerView = view.findViewById<RecyclerView>(R.id.story_like_recyclerview)
        val shimmer = view.findViewById<ShimmerFrameLayout>(R.id.shimmer)
        val deletebtn = view.findViewById<ImageView>(R.id.delete_story)
        val number = view.findViewById<TextView>(R.id.seenusernumber)
        number.text = "seen by " + seenuserCount
        deletebtn.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().reference
                .child("Story")
                .child(userId)
                .child(storiesIds)

            ref.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Deleted...", Toast.LENGTH_SHORT).show()
                }
            }
        }


        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId)
            .child(storiesIds)
            .child("views")


        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = StoryLikeAdapter(storySennUser)
        recyclerView.adapter = adapter

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                storySennUser.clear()

                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(UserData::class.java)
                    if (user != null) {
                        storySennUser.add(user)
                        storySennUser.reverse()
                        shimmer.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE

                    }


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



        adapter.notifyDataSetChanged()
        dialog.setCancelable(true)

        dialog.setContentView(view)
    }

    private fun getStories(userId: String) {
        imageList = ArrayList()
        storiesIds = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (imageList as ArrayList<String>).clear()
                (storiesIds as ArrayList<String>).clear()



                for (Snapshot in snapshot.children) {
                    val story: Story? = Snapshot.getValue(Story::class.java)

                    val currentTime = System.currentTimeMillis()
                    //// eska matlab hai agar story ka 24 hr nhi hua hai to hi arraylist me add karna hai
                    if (currentTime > story!!.timeStart && currentTime < story.timeEnd) {
                        (imageList as ArrayList<String>).add(story.imageUrl!!)
                        (storiesIds as ArrayList<String>).add(story.storyId!!)
                    }
                }


                storyProgressView!!.setStoriesCount((imageList as ArrayList<String>).size)
                storyProgressView!!.setStoryDuration(10000L)
                storyProgressView!!.setStoriesListener(this@StoryActivity)
                storyProgressView!!.startStories(counter)


                Glide.with(this@StoryActivity).load(imageList!![counter])
                    .placeholder(R.drawable.image)
                    .into(binding.imageStory)


                ///image and name who see the 

                addViewToStory(storiesIds!!.get(counter))
                seenNumber(storiesIds!!.get(counter))
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun userInfo(userId: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("user").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {


                    val user = snapshot.getValue(UserData::class.java)

                    Glide.with(this@StoryActivity).load(user!!.profileImage)
                        .placeholder(R.drawable.user).into(binding.storyProfileImage)
                    binding.storyUsername.text = user.name.toString()

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun addViewToStory(storyId: String) {

        val ref =
            FirebaseDatabase.getInstance().reference.child("user").child(Utils.currentUserId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(UserData::class.java)
                    if (data != null) {
                        val userData = UserData(
                            name = data.name,
                            "",
                            userName = Utils.currentUserId(),
                            profileImage = data.profileImage,
                            bio = System.currentTimeMillis().toString()
                        )
                        setStorySeenUserData(userData, storyId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    private fun setStorySeenUserData(userData: UserData, storyId: String) {
        FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId)
            .child(storyId)
            .child("views")
            .child(currentUserId)
            .setValue(userData)
    }

    private fun seenNumber(storyId: String) {

        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId)
            .child(storyId)
            .child("views")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                seenuserCount = snapshot.childrenCount
                binding.seenNumber.text = "" + snapshot.childrenCount
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    override fun onNext() {
        Glide.with(this@StoryActivity).load(imageList!![++counter]).placeholder(R.drawable.image)
            .into(binding.imageStory)
        addViewToStory(storiesIds!!.get(counter))
        seenNumber(storiesIds!!.get(counter))
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        Glide.with(this@StoryActivity).load(imageList!![--counter]).placeholder(R.drawable.image)
            .into(binding.imageStory)
        seenNumber(storiesIds!!.get(counter))
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        storyProgressView!!.destroy()
    }

    override fun onResume() {
        super.onResume()
        storyProgressView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        storyProgressView!!.pause()
    }

}
