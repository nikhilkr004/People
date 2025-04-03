package com.example.people.Maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.people.Activity.Utils
import com.example.people.DataClass.UserData
import com.example.people.R
import com.example.people.services.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("location")
    private val userMarkers = HashMap<String, Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        if (checkPermissions()) {
            startLocationService()
        } else {
            requestPermissions()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        listenForAllUsersLocationUpdates()
        getCurrentUserLocation()
    }

    private fun listenForAllUsersLocationUpdates() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    val latitude = userSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = userSnapshot.child("longitude").getValue(Double::class.java)

                    if (latitude != null && longitude != null && userId != null) {
                        updateUserLocationOnMap(userId, latitude, longitude)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsActivity, "Failed to fetch locations", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserLocationOnMap(userId: String, lat: Double, lng: Double) {

        if (!isDestroyed && !isFinishing) {
            val userRef = FirebaseDatabase.getInstance().reference.child("user").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(UserData::class.java)
                        if (userData != null) {
                            val location = LatLng(lat, lng)
                            userMarkers[userId]?.position = location

                            if (!userMarkers.containsKey(userId)) {
                                Glide.with(this@MapsActivity)
                                    .asBitmap()
                                    .load(userData.profileImage)
                                    .placeholder(R.drawable.user)
                                    .override(130, 130)
                                    .circleCrop()
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            val markerIcon =
                                                BitmapDescriptorFactory.fromBitmap(resource)
                                            val marker = mMap.addMarker(
                                                MarkerOptions()
                                                    .position(location)
                                                    .icon(markerIcon)
                                                    .title(userData.name)
                                            )
                                            if (marker != null) {
                                                userMarkers[userId] = marker
                                            }
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {}
                                    })
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun getCurrentUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentUserLocation = LatLng(location.latitude, location.longitude)
                userMarkers["currentUser"]?.position = currentUserLocation

                if (!userMarkers.containsKey("currentUser")) {
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(currentUserLocation)
                            .title("You")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )
                    if (marker != null) {
                        userMarkers["currentUser"] = marker
                    }
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLocation, 16f))
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1001
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startLocationService()
        } else {
            Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}