package com.example.panchagapp.ui.inscribirseEventos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.panchagapp.MainActivity
import com.example.panchagapp.R
import com.example.panchagapp.WeatherService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.processNextEventInCurrentThread
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [inscribirseEventoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class inscribirseEventoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val apiKey = "3e3d57cbd2e191d212257ab99f6ab698"
    private lateinit var weatherService: WeatherService
    val database = FirebaseDatabase.getInstance()
    val eventRef = database.getReference("events")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        weatherService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inscribirse_evento, container, false)
    }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            var tvname = view.findViewById<TextView>(R.id.tveventname)
            var inscribirseButton = view.findViewById<Button>(R.id.buttoninscribirse)
            var trackbutton = view.findViewById<Button>(R.id.trackbutton)
            var tveventdate = view.findViewById<TextView>(R.id.tveventdate)
            var maxplayerstv = view.findViewById<TextView>(R.id.maxplayers)
            var currentplayerstv = view.findViewById<TextView>(R.id.currentplayers)
            var descriptiontv = view.findViewById<TextView>(R.id.textView3)
            var hourtv = view.findViewById<TextView>(R.id.tveventhora)
            val bundle = arguments
            var lat = ""
            var lon = ""
            if (bundle == null) {
                Log.d("Confirmation", "FragmentEvento sin arguments")
                return
            }
            val args = inscribirseEventoFragmentArgs.fromBundle(bundle)
            if(args.eventName.isNullOrBlank()){
                tvname.text = "Evento sin Nombre"
            }
            else {
                tvname.text = args.eventName
                getEventInfobyName(args.eventName) {
                    eventData -> if (eventData != null) {
                        val date = eventData.get("datatime") as? String
                        val hour = eventData.get("hour") as? String
                        val description = eventData.get("description") as? String
                        val maxplayers = eventData.get("maxPlayers") as? Long
                        val location = eventData.get("location") as? Map<String, Any>

                         lat = location?.get("lat") as String
                         lon = location?.get("lon") as String
                        GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val weatherData = weatherService.getWeather(lat, lon, apiKey)
                            withContext(Dispatchers.Main) {
                                updateUI(view, weatherData)
                                hourtv.text = hour
                                tveventdate.text = date
                                maxplayerstv.text = maxplayers.toString()
                                descriptiontv.text = description
                            }

                        } catch (e: Exception) {
                            // Handle exceptions
                            // Optionally, you can show a message to the user informing them about the error.
                        }



                    }

                    } else {
                        // Player not found
                        Toast.makeText(requireContext(), "Event not found.", Toast.LENGTH_SHORT).show()
                        println("Event not found.")
                        // Navigate back to previous fragment
                        findNavController().popBackStack()
                    }
                }
            }

            inscribirseButton.setOnClickListener {
                // Get the event name from wherever it's stored
                val eventName = args.eventName

                // Get a reference to the events node in the database
                val eventsRef = FirebaseDatabase.getInstance().getReference("events")


                // Perform a query to find the event by its name
                eventsRef.orderByChild("name").equalTo(eventName).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Get the event ID from the dataSnapshot
                            val eventId = dataSnapshot.children.first().key

                            // Get a reference to the event node in the database using the event ID
                            val eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId!!)

                            // Get the current user's UID
                            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                            // Check if the current user's UID is not null
                            if (currentUserUid != null) {
                                // Check if the player is already in the event's player list
                                val playerAlreadyInList = dataSnapshot.child(eventId).child("players").children.any { it.getValue(String::class.java) == currentUserUid }
                                if (!playerAlreadyInList) {
                                    // Add the current user's UID to the event's player list
                                    eventRef.child("players").push().setValue(currentUserUid)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Navigation and toast message after successfully adding the player
                                                findNavController().navigate(R.id.action_navigation_evento_to_navigation_home)
                                                Toast.makeText(activity, "Inscrito en evento!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(activity, "Error al inscribirse en el evento.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    // Player is already in the event's player list
                                    Toast.makeText(activity, "Ya estás inscrito en este evento.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Handle the case when the current user's UID is null (user not authenticated)
                                Toast.makeText(activity, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle the case when the event with the given name does not exist
                            Toast.makeText(activity, "Evento no encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("Operación cancelada: ${databaseError.message}")
                    }
                })
            }


            trackbutton.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_evento_to_navigation_direction)
                Toast.makeText(activity, "Como llegar a localizacion!", Toast.LENGTH_SHORT).show()
            }

        }
    private fun updateUI(view: View,weatherData: MainActivity.WeatherData) {
        view.findViewById<TextView>(R.id.weathertemp).text = "${weatherData.main.temp.toInt() - 273}°C"
        val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"
        Glide.with(this).load(iconUrl).into(view.findViewById(R.id.imageView2))
    }


    fun getEventInfobyName(eventName: String?, callback: (HashMap<String, Any>?) -> Unit) {
        eventRef.orderByChild("name").equalTo(eventName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (eventSnapshot in dataSnapshot.children) {
                            val eventData = eventSnapshot.value as HashMap<String, Any>?
                            callback(eventData)
                            return // Exit loop after the first match if you only expect one player with the given name
                        }
                    } else {
                        // Player not found
                        callback(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    println("Error: ${databaseError.message}")
                    callback(null)
                }
            })
    }




    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            inscribirseEventoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}