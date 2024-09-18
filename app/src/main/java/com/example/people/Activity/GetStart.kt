package com.example.people.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.people.MainActivity
import com.example.people.R
import com.example.people.databinding.ActivityGetStartBinding
import com.google.firebase.auth.FirebaseAuth

class GetStart : AppCompatActivity() {
    private val binding by lazy {
        ActivityGetStartBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (FirebaseAuth.getInstance().currentUser !=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.textView7.setOnClickListener {
            startActivity(Intent(this@GetStart,SignUpActivity::class.java))
        }
        binding.textView8.setOnClickListener {
            startActivity(Intent(this@GetStart,SignInActivity::class.java))
        }
    }
}