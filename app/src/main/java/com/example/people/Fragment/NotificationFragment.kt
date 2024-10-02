package com.example.people.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.people.Adapters.NotificationAdapter
import com.example.people.DataClass.Notification_item
import com.example.people.R
import com.example.people.databinding.FragmentNotificationBinding
import com.example.people.databinding.NotificationItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections


class NotificationFragment : Fragment() {
    private lateinit var binding:FragmentNotificationBinding
    private var notificationList = mutableListOf<Notification_item>()
    private var notificationAdapter: NotificationAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)


        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.setHasFixedSize(true)
        notificationAdapter = NotificationAdapter(requireContext(), notificationList)
        recyclerView.adapter = notificationAdapter
        readNotification()


        return binding.root

    }

    private fun readNotification() {
        val ref = FirebaseDatabase.getInstance().reference
            .child("Notification")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        notificationList.clear()

                        for (snapshot in snapshot.children){
                            val notification=snapshot.getValue(Notification_item::class.java)
                            notificationList.add(notification!!)

                        }
                        Collections.reverse(notificationList)
                        notificationAdapter!!.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}