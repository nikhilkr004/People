package com.example.people.Notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshTokel = FirebaseMessaging.getInstance().token

        if (firebaseUser != null) {
            updateToken(refreshTokel.toString())
        }
    }

    private fun updateToken(refreshTokel: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref=FirebaseDatabase.getInstance().getReference().child("Tokens")
        val token=Token(refreshTokel!!)
        ref.child(firebaseUser!!.uid).setValue(token)
    }

}