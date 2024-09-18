package com.example.people.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.people.Activity.GetStart
import com.example.people.Activity.Utils
import com.example.people.Adapters.OtherUserAdapter
import com.example.people.DataClass.PostItem
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var databaseReference: DatabaseReference
    private val PIC_IMAGE_REQ = 1
    private var imageUri: Uri? = null
    private var isReadPermission = false
    private var isRecordAudio = false
    private lateinit var permissionlauncher: ActivityResultLauncher<Array<String>>
    private val userImage = mutableListOf<PostItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        storage = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        ///sign Out
        binding.imageView8.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            startActivity(Intent(requireContext(), GetStart::class.java))
            requireActivity().finish()
        }
        getUserPost()
        getFollowFollowingCount()

        //// loadprifile data
        prfileData()

        setToolBarColor()
        binding.editprofile.setOnClickListener {
            editProfileData()
        }


        ////// save image

        binding.editProfileImg.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                PIC_IMAGE_REQ
            )
            val storageRef = storage.reference.child("profile_image/${Utils.currentUserId()}.jpg")

            imageUri?.let {
                Utils.showDialog(requireContext(), "Saving image... ")

                storageRef.putFile(it)
                    .addOnCompleteListener {

                        if (it.isSuccessful) {

                            storageRef.downloadUrl.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Utils.showDialog(requireContext(), "Working on it...")
                                    val img = it.result.toString()
                                    setimageInRealtime(img)
                                }
                            }
                        } else {
                            Utils.hideDialog()
                            Toast.makeText(
                                requireContext(),
                                "error to add info...",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
            }
        }





        return binding.root
    }


    private fun getFollowFollowingCount() {
        val ref = databaseReference.child("Follow").child(Utils.currentUserId())
        ref.child("Followers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                binding.followersCount.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        ref.child("Following").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                binding.followingCount.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun getUserPost() {
        val recyclerView = binding.postRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val otherAdapter = OtherUserAdapter(userImage)
        recyclerView.adapter = otherAdapter
        val ref = databaseReference.child("post")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userImage.clear()
                if (snapshot.exists()) {
                    for (Snapshot in snapshot.children) {
                        val image = Snapshot.getValue(PostItem::class.java)
                        if (image != null && image.userID == Utils.currentUserId()) {
                            userImage.add(image)
                        }
                    }

                    otherAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "something went error", Toast.LENGTH_SHORT)
                    .show()
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


    private fun setimageInRealtime(img: String) {
        val editmap = mapOf(
            "profileImage" to img
        )
        databaseReference.child("user").child(Utils.currentUserId())
            .updateChildren(editmap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Utils.hideDialog()
                }
            }
    }

    @SuppressLint("InflateParams")
    private fun editProfileData() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.edit_profile, null)
        val saveBtn = view.findViewById<TextView>(R.id.editprofile)
        val cancel = view.findViewById<TextView>(R.id.cencel)
        val name: EditText = view.findViewById(R.id.name)
        val bio: EditText = view.findViewById(R.id.bio)


        ///// show previous uer info
        val ref = databaseReference.child("user").child(Utils.currentUserId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val data = snapshot.getValue(UserData::class.java)
                if (data != null) {
                    name.setText(data.name)
                    bio.setText(data.bio)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        cancel.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            val name1 = name.text.toString()
            val bio1 = bio.text.toString()


            Utils.showDialog(requireContext(), "Saving info... ")


            val editmap = mapOf(
                "name" to name1,
                "bio" to bio1,

                )
            databaseReference.child("user").child(Utils.currentUserId()).updateChildren(editmap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Utils.hideDialog()
                        dialog.dismiss()
                    }
                }


        }
        /// upload image on storage


        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun prfileData() {
        val ref = databaseReference.child("user").child(Utils.currentUserId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(UserData::class.java)
                if (data != null) {


                    binding.name.text = data.name

                    if (data.bio == "") {
                        binding.bio.text =
                            "There’s just one life – live it your way before your time comes"
                    } else {
                        binding.bio.text = data.bio
                    }



                    Glide.with(requireContext()).load(data.profileImage)
                        .placeholder(R.drawable.user).into(binding.profileImage)

                    binding.profileShimmer.visibility = View.GONE
                    binding.mainLayout.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PIC_IMAGE_REQ && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null)
            imageUri = data.data


    }

}