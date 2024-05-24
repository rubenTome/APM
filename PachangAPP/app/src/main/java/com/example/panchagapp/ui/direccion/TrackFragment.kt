package com.example.panchagapp.ui.direccion

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.example.panchagapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class TrackFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var textview: TextView
    private val args: TrackFragmentArgs by navArgs()
    private var call: Call? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        textview = view.findViewById(R.id.distanceTextView)

        // Create a callback for back press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Cancel the ongoing request if any
                call?.cancel()
                // Navigate back
                findNavController().popBackStack()
            }
        }

        // Add the callback to the back press dispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Create a location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Update the UI with location data
                    // ...
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        // Get the current location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {

                // Parse the destination coordinates from arguments
                val destLatitude = args.destLatitude.toDoubleOrNull() ?: 0.0
                val destLongitude = args.destLongitude.toDoubleOrNull() ?: 0.0
                val destinationLatLng = LatLng(destLatitude, destLongitude)

                // Add a marker for the destination
                map.addMarker(MarkerOptions().position(destinationLatLng).title("Destination"))

                // Create a route from the current location to the destination
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                drawRoute(currentLatLng, destinationLatLng)
            }
        }
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val distanceInKm = calculateDistance(origin, destination)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val navigationMode = sharedPreferences.getString("navigation_mode", "driving") ?: "driving"
        val defaultNavigationMode = when (navigationMode) {
            "Transporte Público" -> "transit"
            "Bicicleta" -> "bicycling"
            else -> "driving"
        }


        // Determine mode based on distance
        val mode = if (distanceInKm > 5) defaultNavigationMode else "walking"

        // Use the Directions API to get route data
        val url = getDirectionsUrl(origin, destination, mode)
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    response.body?.let {
                        val jsonResponse = it.string()
                        val jsonObject = JSONObject(jsonResponse)
                        val routes = jsonObject.getJSONArray("routes")
                        val points = ArrayList<LatLng>()

                        for (i in 0 until routes.length()) {
                            val legs = routes.getJSONObject(i).getJSONArray("legs")
                            val leg = legs.getJSONObject(0)
                            val distance = leg.getJSONObject("distance").getString("text")
                            val duration = leg.getJSONObject("duration").getString("text")
                            for (j in 0 until legs.length()) {
                                val steps = legs.getJSONObject(j).getJSONArray("steps")
                                for (k in 0 until steps.length()) {
                                    val polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points")
                                    points.addAll(decodePolyline(polyline))
                                    updateDistanceAndDuration(distance, duration, mode)
                                }
                            }
                        }

                        activity?.runOnUiThread {
                            map.addPolyline(PolylineOptions().addAll(points).color(Color.BLUE).width(5f))
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the ongoing request if any
        call?.cancel()
    }

    private fun updateDistanceAndDuration(distance: String, duration: String, mode: String) {
        val emoji = when (mode) {
            "driving" -> "\uD83D\uDE97"
            "walking" -> "\uD83D\uDEB6"
            "Bicicleta" -> "\uD83D\uDEB2"
            else -> "\uD83D\uDE8C"
        }
        textview.text = "$emoji Distancia: $distance   Duración: $duration"
    }

    private fun getDirectionsUrl(origin: LatLng, destination: LatLng, mode: String): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${destination.latitude},${destination.longitude}"
        val parameters = "$strOrigin&$strDest&mode=$mode&key=AIzaSyCwCJfKV_0vQNbQuE_SEW7t4UAL_yiJiLE"
        return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
    }

    private fun calculateDistance(startP: LatLng, endP: LatLng): Double {
        val radius = 6371 // radius of earth in Km
        val lat1 = startP.latitude
        val lng1 = startP.longitude
        val lat2 = endP.latitude
        val lng2 = endP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lng2 - lng1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radius * c
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat / 1E5, lng / 1E5)
            poly.add(p)
        }

        return poly
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
