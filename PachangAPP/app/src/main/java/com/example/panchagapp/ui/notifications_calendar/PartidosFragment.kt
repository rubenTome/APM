package com.example.panchagapp.ui.notifications_calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.ui.listaeventos.EventosDataClass
import com.example.panchagapp.ui.profile.HistorialAdapterClass
import com.example.panchagapp.ui.profile.HistorialDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 * Use the [PartidosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PartidosFragment : Fragment() {

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
    private var eventName: String? = null
    val database = Firebase.database
    val myRef = database.getReference("tournamentMatches")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historialArrayList= arrayListOf()
        adapter = HistorialAdapterClass(historialArrayList)
        fetchEventsFromDatabase(eventName!!)
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
        recyclerView = view.findViewById(R.id.historialrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = HistorialAdapterClass(historialArrayList)
        recyclerView.adapter = adapter
    }



    private fun dataInitialize() {
        historialArrayList = arrayListOf<HistorialDataClass>()
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
            val dataClass = HistorialDataClass(
                team1imagelist[i],
                teamnamelist[i],
                resultList[i],
                teamnamelist[i],
                team1imagelist[i]
            )
            historialArrayList.add(dataClass)
        }
    }

    private fun fetchEventsFromDatabase(eventname: String) {
        val tournamentRef = myRef.child(eventname)
        tournamentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                historialArrayList.clear()
                for (snapshot in dataSnapshot.children) {
                    val score = snapshot.child("score").getValue(String::class.java)
                    val team1 = snapshot.child("team1").getValue(String::class.java)
                    val team2 = snapshot.child("team2").getValue(String::class.java)
                    val event = HistorialDataClass(R.drawable.baseline_account_circle_24, team1!!, score!!, team2!!,  R.drawable.baseline_account_circle_24)
                    historialArrayList.add(event)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }


}