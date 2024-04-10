package com.example.panchagapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!
    private lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val eventbutton = root.findViewById<Button>(R.id.eventbutton)
        val listbutton = root.findViewById<Button>(R.id.listbutton)
        createFragment()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        eventbutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_creareventos)
            Toast.makeText(activity, "Crear Evento", Toast.LENGTH_SHORT).show()
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        addMarker()
            /*map.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {
                // Clears the previously touched position
                map.clear();
                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLng(latlng));

                val location = LatLng(latlng.latitude, latlng.longitude)
                Toast.makeText(requireContext(), "Estás en ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
                map.addMarker(MarkerOptions().position(location))
            }
        })*/
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
                        }
                    }

            } else {
                Toast.makeText(requireContext(), "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun addMarker() {
        val favoritePlace = LatLng(43.364195,-8.414)
        val favoritePlace2 = LatLng(43.364195,-8.402)
        val evento1 = map.addMarker(MarkerOptions().position(favoritePlace).title("Evento 1"))
        val evento2 = map.addMarker(MarkerOptions().position(favoritePlace2).title("Torneo 1"))
        map.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker != null && clickedMarker == evento1) {
                val directions = HomeFragmentDirections.actionNavigationHomeToNavigationEvento2("Evento 1")
                Toast.makeText(activity, "Datos Evento", Toast.LENGTH_SHORT).show()
                findNavController().navigate(directions)
                true // Returning true consumes the event and indicates that it has been handled
            } else {
                false // Returning false indicates that the event has not been handled
            }
        }
        map.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker != null && clickedMarker == evento2) {
                val directions = HomeFragmentDirections.actionNavigationHomeToNavigationTorneo("Torneo 1")
                Toast.makeText(activity, "Datos Torneo", Toast.LENGTH_SHORT).show()
                findNavController().navigate(directions)
                true // Returning true consumes the event and indicates that it has been handled
            } else {
                false // Returning false indicates that the event has not been handled
            }
        }
    }

}

