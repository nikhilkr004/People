package com.example.people.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.people.MainActivity
import com.example.people.R
import com.example.people.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        binding.signUpBtn.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {

        val email =binding.email.text.toString()
        val password =binding.password.text.toString()

        if(email.isEmpty()||password.isEmpty()){
            Toast.makeText(this, "Fill the all Require Details", Toast.LENGTH_SHORT).show()
        }

        else{
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    Utils.showDialog(this,"Sign in...")

                    startActivity(Intent(this,MainActivity::class.java))
                }

                else{
                    Utils.hideDialog()
                    Toast.makeText(this, it.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}