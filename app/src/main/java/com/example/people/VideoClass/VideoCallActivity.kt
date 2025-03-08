package com.example.people.VideoClass

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.people.Activity.Utils
import com.example.people.DataClass.UserData
import com.example.people.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import java.util.Collections


class VideoCallActivity : AppCompatActivity() {
    private lateinit var databaseReference:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.call_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseReference=FirebaseDatabase.getInstance().reference

        val friendUserId = intent.getStringExtra("userId").toString()
        val userName = intent.getStringExtra("userName") ?: "Guest"

        val user=findViewById<ZegoSendCallInvitationButton>(R.id.call_button)
        user.setIsVideoCall(true)
        user.resourceID="zego_uikit_call"
        user.setInvitees(Collections.singletonList(ZegoUIKitUser(friendUserId.toString(),userName.toString())))


        val ref = databaseReference.child("user").child(Utils.currentUserId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(UserData::class.java)
                if (data != null) {


//                    initZegoCloud(data.userId,data.name,friendUserId)


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
//
//    private fun initZegoCloud(userId: String?, name: String?, friendUserId: String) {
//        val appID: Long = 1134852515 // Replace with your ZEGOCLOUD App ID
//        val appSign: String = "895c38ead943000526a8a6fc1ba415182cf5bc3c4ead657dfa5b9c60335afbc3" // Replace with your App Sign
//        val callID: String = friendUserId // Unique call ID, usually the friend's UID
//
//        val callConfig = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
//
//        val callFragment = ZegoUIKitPrebuiltCallFragment.newInstance(
//            appID, appSign, userId!!, name!!,callID , callConfig
//        )
//
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.call_container, callFragment)
//            .commit()
//    }

}
