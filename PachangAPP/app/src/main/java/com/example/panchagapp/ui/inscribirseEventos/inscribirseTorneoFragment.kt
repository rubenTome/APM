package com.example.panchagapp.ui.inscribirseEventos

import TeamAdapterClass
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.panchagapp.MainActivity
import com.example.panchagapp.R
import com.example.panchagapp.WeatherService
import com.example.panchagapp.ui.listaeventos.EventosAdapterClass
import com.example.panchagapp.ui.listaeventos.EventosDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var lat: String
private lateinit var lon: String

/**
 * A simple [Fragment] subclass.
 * Use the [inscribirseTorneoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class inscribirseTorneoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<TeamDataClass>
    private lateinit var adapter: TeamAdapterClass
    lateinit var teamimageList: Array<Int>
    lateinit var teamnameList: Array<String>
    lateinit var teamplayersList: Array<String>
    private val apiKey = "3e3d57cbd2e191d212257ab99f6ab698"
    private lateinit var weatherService: WeatherService
    val database = FirebaseDatabase.getInstance()
    val eventRef = database.getReference("events")
    val teamsRef = database.getReference("teams")
    val playersRef = database.getReference("players")



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

        // Replace "CityName" with the desired city

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_inscribirse_torneo, container, false)
        GlobalScope.launch(Dispatchers.IO) {
            val weatherData = weatherService.getWeather("30.3", "30.4", apiKey)
            withContext(Dispatchers.Main) {
                updateUI(rootview, weatherData)
            }
        }
        return rootview
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            inscribirseTorneoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        var tvname = view.findViewById<TextView>(R.id.tveventname)
        var hourtv = view.findViewById<TextView>(R.id.tvtorneohora)
        var trackbutton = view.findViewById<Button>(R.id.trackbutton)
        var tveventdate = view.findViewById<TextView>(R.id.tveventdate)
        var descriptiontv = view.findViewById<TextView>(R.id.admincomment)
        val bundle = arguments
        lat = ""
        lon = ""
        dataList = arrayListOf()
        if (bundle == null) {
            Log.d("Confirmation", "FragmentEvento sin arguments")
            return
        }
        val args = inscribirseEventoFragmentArgs.fromBundle(bundle)
        if (args.eventName.isNullOrBlank()) {
            tvname.text = "Evento sin Nombre"
        } else {
            tvname.text = args.eventName
            getEventInfobyName(args.eventName) { eventData ->
                if (eventData != null) {
                    val date = eventData.get("datatime") as? String
                    val hour = eventData.get("hour") as? String
                    val description = eventData.get("description") as? String
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

        recyclerView = view.findViewById(R.id.teamrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = TeamAdapterClass(dataList, R.layout.layout_team_list_item)
        recyclerView.adapter = adapter

        getTeamsForTournament(args.eventName)

        adapter.setOnItemClickListener(object : TeamAdapterClass.onItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItem = dataList[position]
                val teamName = selectedItem.teamTitle.toString()
                val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                val authenticatedPlayerId: String? = currentUser?.uid
                teamsRef.child(args.eventName).child(teamName).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val maxPlayers = dataSnapshot.child("maxPlayers").value as Long
                        val participantsSnapshot = dataSnapshot.child("participants")
                        val participantCount = participantsSnapshot.childrenCount
                        if (participantCount < maxPlayers) {
                            val isPlayerAlreadyParticipant = participantsSnapshot.children.any { it.getValue(String::class.java) == authenticatedPlayerId }
                            if (!isPlayerAlreadyParticipant) {
                                incrementUserPoints(authenticatedPlayerId!!)
                                teamsRef.child(args.eventName).child(teamName).child("participants").child(participantCount.toString()).setValue(authenticatedPlayerId)
                                Toast.makeText(activity, "Inscrito en $teamName", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_navigation_torneo_to_navigation_home)
                            } else {
                                Toast.makeText(activity, "Ya está inscrito en $teamName", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(activity, "El equipo $teamName está completo. No se puede inscribir más jugadores.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle potential errors here
                    }
                })
            }
        })



        trackbutton.setOnClickListener {
            val action =
                inscribirseTorneoFragmentDirections.actionNavigationTorneoToTrackFragment(lat, lon)
            findNavController().navigate(action)
            Toast.makeText(activity, "Como llegar a localizacion!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun incrementUserPoints(userId: String) {
        val userRef = playersRef.child(userId)
        userRef.child("totalPoints").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentPoints = dataSnapshot.getValue(Int::class.java) ?: 0
                userRef.child("totalPoints").setValue(currentPoints + 20)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Failed to read totalPoints", databaseError.toException())
            }
        })
    }

    private fun updateUI(view: View, weatherData: MainActivity.WeatherData) {
        view.findViewById<TextView>(R.id.weathertemp).text =
            "${weatherData.main.temp.toInt() - 273}°C"
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

    fun getTeamsForTournament(tournamentName: String) {
        teamsRef.child(tournamentName).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (teamSnapshot in snapshot.children) {
                    val teamName = teamSnapshot.key
                    val players = teamSnapshot.child("maxPlayers").getValue(Int::class.java).toString()
                    val participantsSnapshot = teamSnapshot.child("participants")
                    var participantsCount = 0
                    for (participantSnapshot in participantsSnapshot.children) {
                        participantsCount++
                    }

                    val team = TeamDataClass(R.drawable.baseline_account_circle_24, teamName!!,"$participantsCount/$players")
                    dataList.add(team)
                }
                adapter.notifyDataSetChanged() // Move notifyDataSetChanged outside the loop
            } else {
                println("No teams found for the given tournament name.")
            }
        }.addOnFailureListener {
            println("Failed to retrieve teams: ${it.message}")
        }
    }



}