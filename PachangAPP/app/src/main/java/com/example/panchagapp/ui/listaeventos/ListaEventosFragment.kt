package com.example.panchagapp.ui.listaeventos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.ui.inscribirseEventos.TeamAdapterClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListaEventosFragment : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: EventosAdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventosArrayList: ArrayList<EventosDataClass>
    private lateinit var eventosAdapter: EventosAdapterClass
    val database = Firebase.database
    val myRef = database.getReference("events")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        eventosArrayList = arrayListOf()
        eventosAdapter = EventosAdapterClass(eventosArrayList)
        fetchEventsFromDatabase()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_eventos, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListaEventosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.eventrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventosAdapterClass(eventosArrayList)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object: EventosAdapterClass.onItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedItem = eventosArrayList[position]
                val title = selectedItem.eventTitle ?: ""
                Toast.makeText(activity, "Seleccionado $title", Toast.LENGTH_SHORT).show()
                val directions =  if (selectedItem.eventImage == R.drawable.trophy_svgrepo_com) {
                    ListaEventosFragmentDirections.actionNavigationListaeventosToNavigationTorneo(title)
                } else {
                    ListaEventosFragmentDirections.actionNavigationListaeventosToNavigationEvento(title)
                }
                findNavController().navigate(directions)
            }
        })
    }


    private fun fetchEventsFromDatabase() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventosArrayList.clear()
                for (snapshot in dataSnapshot.children) {
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
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }


}