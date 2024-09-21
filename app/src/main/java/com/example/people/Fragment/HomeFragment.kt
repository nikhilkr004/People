package com.example.people.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.people.Adapters.postAdapter
import com.example.people.DataClass.PostItem
import com.example.people.R
import com.example.people.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    val postData= mutableListOf<PostItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        databaseReference=FirebaseDatabase.getInstance().reference

        val recyclerView=binding.homeRecyclerview
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        val daapter=postAdapter(postData)

        val ref=databaseReference.child("post")


        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postData.clear()
                for(Snapsht in snapshot.children){
                    val data =Snapsht.getValue(PostItem::class.java)
                    if (data!=null) {
                        postData.add(data!!)
                        postData.reverse()
                        binding.shimer.visibility=View.GONE
                        binding.homeRecyclerview.visibility=View.VISIBLE
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        recyclerView.adapter=daapter
        daapter.notifyDataSetChanged()
        return binding.root
    }

    private fun getPostInfo() {

    }


}