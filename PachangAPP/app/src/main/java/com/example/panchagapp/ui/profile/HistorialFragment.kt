package com.example.panchagapp.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.ui.listaeventos.EventosAdapterClass
import com.example.panchagapp.ui.listaeventos.EventosDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistorialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: HistorialAdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var historialArrayList: ArrayList<HistorialDataClass>

    lateinit var team1imagelist: Array<Int>
    lateinit var team2imagelist: Array<Int>
    lateinit var teamnamelist: Array<String>
    lateinit var resultList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventbutton = view.findViewById<Button>(R.id.eventbutton)
        val layoutManager = LinearLayoutManager(context)
        historialArrayList = arrayListOf<HistorialDataClass>()
        recyclerView = view.findViewById(R.id.historialrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = HistorialAdapterClass(historialArrayList)
        recyclerView.adapter = adapter

        val eventsRef = FirebaseDatabase.getInstance().getReference("events")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val events = ArrayList<Map<String, Any>>()
                for (eventSnapshot in dataSnapshot.children) {
                    val event = eventSnapshot.value as? Map<String, Any>
                    if (event != null) {
                        val playersList = event["players"] as? List<String>
                        if (playersList != null && userId in playersList) {
                            events.add(event)

                            val eventname = event["name"] as? String
                            if (eventname != null) {
                                val resultsRef = FirebaseDatabase.getInstance().getReference("tournamentMatches").child(eventname)
                                resultsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(resultsSnapshot: DataSnapshot) {
                                        for (snapshot in resultsSnapshot.children) {
                                            val team1Name = snapshot.child("team1").getValue(String::class.java)
                                            val team2Name = snapshot.child("team2").getValue(String::class.java)
                                            val score = snapshot.child("score").getValue(String::class.java)
                                            if (team1Name != null && team2Name != null && score != null) {
                                                val evento = HistorialDataClass(
                                                    R.drawable.baseline_account_circle_24,
                                                    team1Name,
                                                    score,
                                                    team2Name,
                                                    R.drawable.baseline_account_circle_24
                                                )
                                                historialArrayList.add(evento)
                                                Log.d("HistorialFragment", "Added event: $evento")
                                            }
                                        }
                                        adapter.notifyDataSetChanged()
                                        Log.d("HistorialFragment", "Adapter notified of data change")
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.e("HistorialFragment", "Failed to read tournament matches", databaseError.toException())
                                    }
                                })
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("HistorialFragment", "Failed to read events", databaseError.toException())
            }
        })
    }

        companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistorialFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun dataInitialize() {

        team1imagelist = arrayOf(
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
        )
        team2imagelist = arrayOf(
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
        )

        teamnamelist = arrayOf(
            "Equipo 1",
            "Equipo 2",
            "Equipo 3",
        )
        resultList = arrayOf(
            "1-2",
            "2-3",
            "0-3",
        )

        for (i in team1imagelist.indices) {
            val dataClass = HistorialDataClass(team1imagelist[i], teamnamelist[i],resultList[i],teamnamelist[i],team1imagelist[i])
            historialArrayList.add(dataClass)
        }
    }


}