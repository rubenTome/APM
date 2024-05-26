package com.example.panchagapp.ui.notifications_calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.MobileNavigationDirections
import com.example.panchagapp.R
import com.example.panchagapp.databinding.FragmentNotificationsBinding
import com.example.panchagapp.ui.listaeventos.EventosAdapterClass
import com.example.panchagapp.ui.listaeventos.EventosDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: EventosAdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventosArrayList: ArrayList<EventosDataClass>
    val database = Firebase.database
    val myRef = database.getReference("events")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventosArrayList = arrayListOf()
        adapter = EventosAdapterClass(eventosArrayList)
        fetchEventsFromDatabase()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.eventrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventosAdapterClass(eventosArrayList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: EventosAdapterClass.onItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItem = eventosArrayList[position]
                Toast.makeText(activity, "Seleccionado "+ selectedItem.eventTitle.toString(), Toast.LENGTH_SHORT).show()
                val action = NotificationsFragmentDirections.actionNavigationNotificationsToNavigationHistorialequipo(selectedItem.eventTitle.toString())
                findNavController().navigate(action)
            }
        })
    }

    private fun fetchEventsFromDatabase() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventosArrayList.clear()
                val dateFormat = SimpleDateFormat("dd-MM-YYYY", Locale.getDefault())
                val calendar = Calendar.getInstance()

                for (snapshot in dataSnapshot.children) {
                    val datetimeString = snapshot.child("datatime").getValue(String::class.java)

                    if (datetimeString != null) {
                        val datetime = dateFormat.parse(datetimeString)
                        if (datetime != null) {
                            val eventName = snapshot.child("name").getValue(String::class.java)
                            val eventImagename = snapshot.child("type").getValue(String::class.java)
                            val eventImage = when (eventImagename) {
                                "Torneo" -> R.drawable.trophy_svgrepo_com
                                "Evento Casual" -> R.drawable.football_game
                                else -> R.drawable.football_game // Default image if type doesn't match
                            }
                            val event = EventosDataClass(eventImage, eventName)
                            eventosArrayList.add(event)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    fun isWithinLast7Days(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val past7Days = Calendar.getInstance()
        past7Days.add(Calendar.DAY_OF_YEAR, -7)
        return !calendar.before(past7Days) && calendar.before(Calendar.getInstance())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}