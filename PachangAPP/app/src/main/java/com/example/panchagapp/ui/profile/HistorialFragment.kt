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
        val layoutManager = LinearLayoutManager(context)
        historialArrayList = arrayListOf<HistorialDataClass>()
        recyclerView = view.findViewById(R.id.historialrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = HistorialAdapterClass(historialArrayList)
        recyclerView.adapter = adapter


        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val userTeams = mutableListOf<String>()
        val userMatches = mutableListOf<Map<String, Any>>()


        val teamsRef = FirebaseDatabase.getInstance().getReference("teams")
        teamsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { eventSnapshot ->
                    eventSnapshot.children.forEach { teamSnapshot ->
                        val team = teamSnapshot.getValue<Map<String, Any>>()
                        val participants = team?.get("participants") as? List<String>
                        if (participants != null && userId in participants) {
                            val teamName = team["name"] as? String
                            teamName?.let { userTeams.add(it) }
                        }
                    }
                }


                val casualMatchesRef = FirebaseDatabase.getInstance().getReference("casualMatches")
                casualMatchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(casualMatchesSnapshot: DataSnapshot) {
                        casualMatchesSnapshot.children.forEach { matchSnapshot ->
                            val match = matchSnapshot.getValue<Map<String, Any>>()
                            val team1Name = match?.get("team1") as? String
                            val team2Name = match?.get("team2") as? String
                            if (team1Name in userTeams || team2Name in userTeams) {
                                userMatches.add(match!!)
                            }
                        }


                        val tournamentMatchesRef = FirebaseDatabase.getInstance().getReference("tournamentMatches")
                        tournamentMatchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(tournamentMatchesSnapshot: DataSnapshot) {
                                tournamentMatchesSnapshot.children.forEach { tournamentSnapshot ->
                                    tournamentSnapshot.children.forEach { matchSnapshot ->
                                        val match = matchSnapshot.getValue<Map<String, Any>>()
                                        val team1Name = match?.get("team1") as? String
                                        val team2Name = match?.get("team2") as? String
                                        if (team1Name in userTeams || team2Name in userTeams) {
                                            userMatches.add(match!!)
                                            //Toast.makeText(requireContext(), match.toString(), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                                //Log.d("UserMatches", "Partidos que juegan los equipos del usuario: $userMatches")
                                //Toast.makeText(requireContext(), userMatches.toString(), Toast.LENGTH_LONG).show()

                                representarPartidosUsuario(userMatches)
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("HistorialFragment", "Failed to read matches", databaseError.toException())
                            }
                        })

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("HistorialFragment", "Failed to read matches", databaseError.toException())
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("HistorialFragment", "Failed to read matches", databaseError.toException())
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

    private fun representarPartidosUsuario(userMatches: List<Map<String, Any>>) {
        val defaultTeamIcon = R.drawable.baseline_account_circle_24
        historialArrayList.clear()

        userMatches.forEach { match ->
            val team1Name = match["team1"] as? String ?: "Equipo Desconocido"
            val team2Name = match["team2"] as? String ?: "Equipo Desconocido"
            val score = match["score"] as? String ?: "Sin Resultado"

            val partido = HistorialDataClass(
                team1Image = defaultTeamIcon,
                team1Name = team1Name,
                result = score,
                team2Name = team2Name,
                team2Image = defaultTeamIcon
            )
            historialArrayList.add(partido)
        }


        adapter.notifyDataSetChanged()
    }

}