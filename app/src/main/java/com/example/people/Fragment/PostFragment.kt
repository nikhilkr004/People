package com.example.people.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.people.Activity.Utils
import com.example.people.DataClass.PostItem
import com.example.people.DataClass.UserData
import com.example.people.MainActivity
import com.example.people.R
import com.example.people.databinding.FragmentPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private lateinit var databaseReference: DatabaseReference
    private var postRef = FirebaseDatabase.getInstance().reference.child("postimage")
    private lateinit var storage: FirebaseStorage
    var PIC_IMAGE = 0
    private var imageUri: Uri? = null
    private lateinit var key: String
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        key = postRef.push().key.toString()
        ///setting up toolbar color
        setToolBarColor()
        loadUserInfo()

        binding.postBtn.setOnClickListener {
            uploadPostImage()
        }

        binding.backButton.setOnClickListener {
            requireActivity().finish()
        }

///to select
        binding.postImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "select image"),
                PIC_IMAGE
            )
        }

        return binding.root


    }

    private fun uploadPostImage() {
        val user: FirebaseUser? = auth.currentUser
        var title = binding.editTextText.text.toString()

        if (user != null) {
            Utils.showDialog(requireContext(), "posting Image")

            val userId = user.uid
            val userName = user.displayName ?: "User"
            val userImage = user.photoUrl ?: ""

//////fatch the profile for user
            databaseReference.child("user").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserData::class.java)

                        if (userData != null) {
                            val useNameFromDb = userData.name
                            val userImageFromDb = userData.profileImage
///////////////////////upload image ////////////////////////

                            val storageRefe = storage.getReference("postImage")
                                .child(UUID.randomUUID().toString())

                            imageUri?.let { it ->
                                storageRefe.putFile(it)
                                    .addOnCompleteListener {

                                        if (it.isSuccessful) {

                                            storageRefe.downloadUrl.addOnCompleteListener {
                                                if (it.isSuccessful) {

                                                    val image = it.result.toString()
//                                                    ///realtime database
//                                                    databaseReference.child("post").child(key).child("postImage").setValue(image)

                                                    val time = System.currentTimeMillis().toString()

                                                    val postItem = PostItem(
                                                        useNameFromDb,
                                                        time,
                                                        title,
                                                        userImageFromDb,
                                                        userId,
                                                        image,
                                                        0,
                                                        0
                                                    )


                                                    ImageStoreInREaltiem(postItem)
                                                }
                                            }
                                        }

                                    }
                            }


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        } else {
            Toast.makeText(requireContext(), "First Register", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadUserInfo() {
        val ref = databaseReference.child("user").child(Utils.currentUserId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(UserData::class.java)
                if (data != null) {
                    binding.userName.text = data.name

                    Glide.with(requireContext()).load(data.profileImage)
                        .placeholder(R.drawable.user).into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "something error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setToolBarColor() {
        activity?.window?.apply {
            val statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue)
            setStatusBarColor(statusBarColor)
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PIC_IMAGE && resultCode == RESULT_OK && data != null && data.data != null)

            imageUri = data.data

        Glide.with(this).load(imageUri).placeholder(R.drawable.image).into(binding.postImage)
    }

    private fun ImageStoreInREaltiem(postItem: PostItem) {
        if (key != null) {
            postItem.postID = key
            val postReference = databaseReference.child("post").child(key)
            postReference.setValue(postItem).addOnCompleteListener {
                if (it.isSuccessful) {
                    Utils.hideDialog()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }
}


