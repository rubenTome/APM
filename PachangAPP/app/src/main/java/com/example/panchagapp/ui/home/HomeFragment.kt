package com.example.panchagapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.panchagapp.R
import com.example.panchagapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!
    private lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private val markerList = mutableListOf<MarkerOptions>()
    private var isMarkerPlaced = false
    private lateinit var selectedMarkerLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    currentLocation = location
                    updateLocationOnMap(location)
                    updateMarkersOnMap()
                }
            }
        }


}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val eventbutton = root.findViewById<Button>(R.id.eventbutton)
        eventbutton.isEnabled = isMarkerPlaced
        val listbutton = root.findViewById<Button>(R.id.listbutton)
        createFragment()



        eventbutton.setOnClickListener {
            if (isMarkerPlaced) {
                val action = HomeFragmentDirections.actionNavigationHomeToNavigationCreareventos(selectedMarkerLocation)
                findNavController().navigate(action)
                Toast.makeText(activity, "Crear Evento", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Debe colocar un marcador en el mapa primero", Toast.LENGTH_SHORT).show()
            }

        }
        listbutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_listaeventos)

            Toast.makeText(activity, "Lista de Eventos", Toast.LENGTH_SHORT).show()
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
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).build()





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

    private fun updateMarkersOnMap() {
        map.clear() // Clear existing markers

        for (marker in markerList) {
            val markerLocation = Location("")
            markerLocation.latitude = marker.position.latitude
            markerLocation.longitude = marker.position.longitude

            // Calculate distance between marker and current location
            val distance = currentLocation.distanceTo(markerLocation)

            // Show marker if within threshold distance (e.g., 1000 meters)
            if (distance <= 1000) {
                map.addMarker(marker)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        addMarker()
        map.setOnMapClickListener { latLng ->
            // Clears the previously placed marker
            map.clear()

            // Animating to the touched position
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            // Adding a marker on the touched position
            val markerOptions = MarkerOptions().position(latLng)
            map.addMarker(markerOptions)

            // Update flag and save coordinates
            isMarkerPlaced = true
            selectedMarkerLocation = latLng

            // Enable the event button
            binding.eventbutton.isEnabled = true
        }
    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)  {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled == true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    }
                }
        } else {
            requestLocationPermission()
        }
    }

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(requireContext(), "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode) {
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled==true
                map.uiSettings.isMyLocationButtonEnabled == true
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            val currentLatLng = LatLng(it.latitude, it.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                            startLocationUpdates()
                        }
                    }

            } else {
                Toast.makeText(requireContext(), "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun addMarker() {
        val favoritePlace = LatLng(43.334195,-8.414)
        val favoritePlace2 = LatLng(43.364195,-8.402)
        val evento1 =  MarkerOptions().position(favoritePlace2).title("Torneo 1")
        val evento2 = MarkerOptions().position(favoritePlace).title("Evento 1")
        val torneo = map.addMarker(evento1)
        val evento = map.addMarker(evento2)
        markerList.add(evento1)
        markerList.add(evento2)
        map.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker != null && clickedMarker == evento) {
                val directions = HomeFragmentDirections.actionNavigationHomeToNavigationEvento2("Evento 1")
                Toast.makeText(activity, "Datos Evento", Toast.LENGTH_SHORT).show()
                findNavController().navigate(directions)
                true // Returning true consumes the event and indicates that it has been handled
            } else if (clickedMarker != null && clickedMarker == torneo) {
                val directions = HomeFragmentDirections.actionNavigationHomeToNavigationTorneo("Torneo 1")
                Toast.makeText(activity, "Datos Torneo", Toast.LENGTH_SHORT).show()
                findNavController().navigate(directions)
                true // Returning true consumes the event and indicates that it has been handled
            }  else  {
                false // Returning false indicates that the event has not been handled
            }
        }

    }




}

