package com.example.people.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.example.people.DataClass.UserData
import com.example.people.MainActivity
import com.example.people.R
import com.example.people.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private var userInfo = mutableListOf<String>()
    private lateinit var firebaseAuth: FirebaseAuth
    val refrence = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpBtn.setOnClickListener {
            signup()
        }
        fatchExistUserData()
    }

    private fun fatchExistUserData() {
        val ref = FirebaseDatabase.getInstance().reference.child("user")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userInfo.clear()
                for (snapshot in snapshot.children) {
                    val data = snapshot.getValue(UserData::class.java)
                    if (data != null) {
                        userInfo.add(data.name!!)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun signup() {
        val name = binding.name.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val userName = binding.userName.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
            Toast.makeText(this, "Fill the all Require Details", Toast.LENGTH_SHORT).show()
        }
        if (userName.equals(userInfo)){
            binding.userName.setError("username already exist")
        }
        else {
            Utils.apply {
                showDialog(this@SignUpActivity, "Working on it.......")
            }
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val UserID = firebaseAuth.currentUser!!.uid
                        val userData = UserData(
                            name = name,
                            email = email,
                            userId = UserID,
                            userName = userName
                        )

                        val ref = refrence.child("user").child(UserID)
                        ref.setValue(userData).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Utils.showDialog(this, "Sign In Successful.....")
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }

                        }

                    } else {
                        Utils.hideDialog()
                        Toast.makeText(this, it.exception!!.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }


        }
    }
}