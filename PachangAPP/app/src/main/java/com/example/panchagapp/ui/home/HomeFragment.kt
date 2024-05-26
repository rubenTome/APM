package com.example.panchagapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.panchagapp.R
import com.example.panchagapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private val markerList = mutableListOf<MarkerOptions>()
    private var isMarkerPlaced = false
    private lateinit var selectedMarkerLocation: LatLng
    private lateinit var database: DatabaseReference
    private var selectedMarker: Marker? = null

    data class EventData(
        val name: String,
        val type: String,
        val lat: Double,
        val lon: Double
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    currentLocation = location
                    updateLocationOnMap(location)
                }
            }
        }
        database = FirebaseDatabase.getInstance().reference.child("events")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val eventButton = root.findViewById<Button>(R.id.eventbutton)
        eventButton.isEnabled = isMarkerPlaced
        val listButton = root.findViewById<Button>(R.id.listbutton)
        createFragment()

        eventButton.setOnClickListener {
            if (isMarkerPlaced) {
                val action = HomeFragmentDirections.actionNavigationHomeToNavigationCreareventos(selectedMarkerLocation)
                findNavController().navigate(action)
                Toast.makeText(activity, "Crear Evento", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Debe colocar un marcador en el mapa primero", Toast.LENGTH_SHORT).show()
            }
        }

        listButton.setOnClickListener {
            currentLocation?.let { location ->
                val locationString = "${location.latitude},${location.longitude}"
                val action = HomeFragmentDirections.actionNavigationHomeToNavigationListaeventos(locationString)
                findNavController().navigate(action)
                Toast.makeText(activity, "Lista de Eventos", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(activity, "Location not available", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private val locationRequest = LocationRequest.Builder(1000)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateLocationOnMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.clear()
        enableMyLocation()
        map.setOnMapClickListener { latLng ->
            if (!isMarkerPlaced) {
                selectedMarker = map.addMarker(MarkerOptions().position(latLng))
                isMarkerPlaced = true
                binding.eventbutton.isEnabled = true
            } else {
                selectedMarker?.position = latLng
            }
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            selectedMarkerLocation = latLng
        }

        map.setOnMarkerClickListener { marker ->
            val tag = marker.tag
            if (tag is EventData) {
                val action = if (tag.type == "Torneo") {
                    HomeFragmentDirections.actionNavigationHomeToNavigationTorneo(tag.name)
                } else {
                    HomeFragmentDirections.actionNavigationHomeToNavigationEvento2(tag.name)
                }
                findNavController().navigate(action)
                true
            } else {
                false
            }
        }
    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)  {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        currentLocation = it
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        loadMarkersFromFirebase()
                    }
                }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(requireContext(), "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            currentLocation = it
                            val currentLatLng = LatLng(it.latitude, it.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                            startLocationUpdates()
                            loadMarkersFromFirebase()
                        }
                    }

            } else {
                Toast.makeText(requireContext(), "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun loadMarkersFromFirebase() {
        currentLocation?.let { location ->
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val locationSnapshot = snapshot.child("location")
                        val lat = locationSnapshot.child("lat").getValue(String::class.java)?.toDoubleOrNull() ?: continue
                        val lon = locationSnapshot.child("lon").getValue(String::class.java)?.toDoubleOrNull() ?: continue
                        val eventType = snapshot.child("type").getValue(String::class.java) ?: continue
                        val eventName = snapshot.child("name").getValue(String::class.java) ?: continue


                        val bitmapLocation1 = BitmapFactory.decodeResource(resources, R.drawable.location1)
                        val bitmapLocation2 = BitmapFactory.decodeResource(resources, R.drawable.location2)

                        val resizedBitmapLocation1 = Bitmap.createScaledBitmap(bitmapLocation1, 140, 140, false)
                        val resizedBitmapLocation2 = Bitmap.createScaledBitmap(bitmapLocation2, 140, 140, false)


                        // Determine the marker icon based on the event type
                        val markerIcon = when (eventType) {
                            "Torneo" -> BitmapDescriptorFactory.fromBitmap(resizedBitmapLocation2)
                            "Evento Casual" -> BitmapDescriptorFactory.fromBitmap(resizedBitmapLocation1)
                            else -> BitmapDescriptorFactory.defaultMarker()
                        }



                        val event = EventData(eventName, eventType, lat, lon)

                        // Calculate the distance
                        val eventLocation = Location("").apply {
                            latitude = lat
                            longitude = lon
                        }
                        val distance = location.distanceTo(eventLocation)

                        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                        val distancePreference = sharedPreferences.getString("markersdistance", "5")?.toDoubleOrNull() ?: 5.0

                        if (distance <= (distancePreference * 1000)) {
                            val markerOptions = MarkerOptions().position(LatLng(lat, lon)).icon(markerIcon)
                            val marker = map.addMarker(markerOptions)
                            marker?.tag = event
                            markerList.add(markerOptions)
                        }

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "Error loading markers", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}