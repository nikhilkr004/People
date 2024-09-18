package com.example.people.Fragment

import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract.Root
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.people.Activity.Utils
import com.example.people.Adapters.searchAdapter
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.databinding.FragmentSearch1Binding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale


class SearchFragment1 : Fragment() {
    private lateinit var binding: FragmentSearch1Binding
    private lateinit var searchAdapter: searchAdapter
    private lateinit var databaseReference: DatabaseReference
    private val userInfo = mutableListOf<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearch1Binding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference
        setToolBarColor()
        showAllUser()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        return binding.root
    }

    private fun filterList(newText: String?) {
        if (newText != null) {
            val filterList = ArrayList<UserData>()
            for (i in userInfo) {
                if (i.name?.lowercase(Locale.ROOT)!!.contains(newText)) {
                    filterList.add(i)
                }
            }
            if (filterList.isEmpty()) {
//                Toast.makeText(requireContext(), "Data not found", Toast.LENGTH_SHORT).show()
            } else {
                searchAdapter.setFilterList(filterList)
            }
        }
    }

    private fun showAllUser() {
        val recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = searchAdapter(userInfo)
        recyclerView.adapter = searchAdapter
        val ref = databaseReference.child("user")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userInfo.clear()
                for (Snapshot in snapshot.children) {
                    val data = Snapshot.getValue(UserData::class.java)
                    if (data != null && data.userId != Utils.currentUserId()) {
                        userInfo.add(data)
                        binding.searchShimmerLayout.visibility = View.GONE
                        binding.searchRecyclerView.visibility = View.VISIBLE
                    }
                }

                searchAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "something went wrong", Toast.LENGTH_SHORT).show()
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


}