package com.example.people.Activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.people.DataClass.Story
import com.example.people.MainActivity
import com.example.people.R
import com.example.people.databinding.ActivityAddStoryBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask


class AddStoryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }
    private var myUri = ""
    var PIC_IMAGE = 0
    private var imageUri: Uri? = null
    private var storageStoryPostPicRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        storageStoryPostPicRef = FirebaseStorage.getInstance().reference.child("storyImage")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT



        startActivityForResult(
            Intent.createChooser(intent, "selectImage"),
            PIC_IMAGE
        )

    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PIC_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data

            uploadStory()
        } else {
            Toast.makeText(this, "Nhi babu ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadStory() {
        when {
            imageUri == null -> Toast.makeText(this, "please select the image ", Toast.LENGTH_SHORT)
                .show()

            else -> {
                Utils.showDialog(this@AddStoryActivity, "Adding Story")


                val fileRef =
                    storageStoryPostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
                var uploadTask: StorageTask<*>

                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it

                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->

                    val downloadingUrl = task.result
                    myUri = downloadingUrl.toString()
                    val ref = FirebaseDatabase.getInstance().reference.child("Story")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    val postId = ref.push().key.toString()
                    val timend = System.currentTimeMillis() + 86400000  // one day later

//                    val postMap = HashMap<String, Any>()
//                    postMap["storyId"] = postId!!
//                    postMap["timeStart"] = System.currentTimeMillis()
//                    postMap["timeEnd"] = timend
//                    postMap["userId"] = FirebaseAuth.getInstance().currentUser!!.uid
//                    postMap["imageUrl"] = myUri

                    val story=Story(
                        imageUrl = myUri,
                        timeStart = System.currentTimeMillis(),
                        timeEnd = timend,
                        storyId = postId,
                        userId = Utils.currentUserId(),
                        )

                    ref.child(postId).setValue(story).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "post uploded sucessfully", Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(this, MainActivity::class.java)

                            startActivity(intent)


                            finish()

                            Utils.hideDialog()
                        }
                        else{
                            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                        }
                    }


                })

            }
        }
    }
}