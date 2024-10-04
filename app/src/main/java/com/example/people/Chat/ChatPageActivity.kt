package com.example.people.Chat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.people.Activity.Utils
import com.example.people.DataClass.messageModel
import com.example.people.R
import com.example.people.databinding.ActivityChatPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatPageActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityChatPageBinding.inflate(layoutInflater)
    }
    private lateinit var messageList: ArrayList<messageModel>
    val firestore = FirebaseFirestore.getInstance()
    var senderRoom: String? = null
    var reciverRoom: String? = null


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
        messageList = ArrayList()
        val name = intent.getStringExtra("name").toString()
        val uid = intent.getStringExtra("uid").toString()
        val profileimg = intent.getStringExtra("img").toString()
        binding.name.text = name.toString()

        Glide.with(this@ChatPageActivity).load(profileimg).into(binding.profile)

        binding.imageView17.setOnClickListener {
            finish()
        }

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference()

        senderRoom = uid + senderUid
        reciverRoom = senderUid + uid


        val recyclerView = binding.chatRecyclerView
        val messageBox = binding.messageBox
        val sentBtbn = binding.sendButton

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager


        ////logic to fatch message
        databaseReference.child("Chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (Snapshot in snapshot.children) {
                        val message = Snapshot.getValue(messageModel::class.java)
                        messageList.add(message!!)
                    }

                    val messageAdapter =
                        MessageAdapter(messageList, this@ChatPageActivity, profileimg)
                    recyclerView.adapter = messageAdapter
                    messageAdapter.notifyDataSetChanged()

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

///////////adding message on database
        sentBtbn.setOnClickListener {

            val message = messageBox.text.toString()
            val messageObject = messageModel(message, uid, Utils.getTime())

            databaseReference.child("Chats").child(senderRoom!!).child("messages")
                .push().setValue(messageObject).addOnSuccessListener {
                    databaseReference.child("Chats").child(reciverRoom!!).child("messages")
                        .push().setValue(messageObject).addOnSuccessListener {
                            val sethasmap= hashMapOf<String,Any>(
                                "friendid" to uid,
                                "time" to Utils.getTime(),
                                "sender" to Utils.currentUserId(),
                                "message" to message,
                                "friendsimage" to profileimg,
                                "name" to name,
                                "person" to "you"
                            )

                            firestore.collection("Conversation${Utils.currentUserId()}").document(uid)
                                .set(sethasmap)

                            firestore.collection("Conversation${uid}").document(Utils.currentUserId())
                                .update(
                                    "message",
                                    message,
                                    "time",
                                    Utils.getTime(),
                                    "person",
                                    name
                                )

                        }
                }

            messageBox.setText("")
        }

    }
}
