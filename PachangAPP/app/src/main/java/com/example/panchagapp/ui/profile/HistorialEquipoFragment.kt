package com.example.panchagapp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

/**
 * A simple [Fragment] subclass.
 * Use the [HistorialEquipoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialEquipoFragment : Fragment() {
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


        val layoutManager = LinearLayoutManager(context)
        historialArrayList = arrayListOf<HistorialDataClass>()
        recyclerView = view.findViewById(R.id.historialrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = HistorialAdapterClass(historialArrayList)
        recyclerView.adapter = adapter


        val eventName = arguments?.getString("eventTitle")


        eventName?.let { name ->

            val casualMatchesRef = FirebaseDatabase.getInstance().getReference("casualMatches").child(name)
            casualMatchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        val match = dataSnapshot.getValue<Match>()
                        match?.let {
                            historialArrayList.add(HistorialDataClass(
                                team1Image = R.drawable.baseline_account_circle_24,
                                team1Name = it.team1 ?: "",
                                result = it.score ?: "",
                                team2Name = it.team2 ?: "",
                                team2Image = R.drawable.baseline_account_circle_24
                            ))
                        }
                        adapter.notifyDataSetChanged()
                    } else {

                        val tournamentMatchesRef = FirebaseDatabase.getInstance().getReference("tournamentMatches").child(name)
                        tournamentMatchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(tournamentSnapshot: DataSnapshot) {
                                if (tournamentSnapshot.exists()) {

                                    val matches = tournamentSnapshot.children.mapNotNull { it.getValue<Match>() }
                                    matches.forEach { match ->
                                        historialArrayList.add(HistorialDataClass(
                                            team1Image = R.drawable.baseline_account_circle_24,
                                            team1Name = match.team1 ?: "",
                                            result = match.score ?: "",
                                            team2Name = match.team2 ?: "",
                                            team2Image = R.drawable.baseline_account_circle_24
                                        ))
                                    }
                                    adapter.notifyDataSetChanged()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("HistorialEquipoFragment", "Failed to read tournament matches", databaseError.toException())
                            }
                        })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("HistorialEquipoFragment", "Failed to read casual matches", databaseError.toException())
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistorialEquipoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    data class Match(
        val score: String? = null,
        val team1: String? = null,
        val team2: String? = null
    )


}