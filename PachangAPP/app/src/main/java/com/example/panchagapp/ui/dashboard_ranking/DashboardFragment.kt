package com.example.panchagapp.ui.dashboard_ranking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.MobileNavigationDirections
import com.example.panchagapp.R
import com.example.panchagapp.databinding.FragmentDashboardBinding
import com.example.panchagapp.ui.listaeventos.EventosAdapterClass
import com.example.panchagapp.ui.listaeventos.EventosDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: RankingAdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var rankingArrayList: ArrayList<RankingDataClass>
    val database = Firebase.database
    val myRef = database.getReference("players")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rankingArrayList = arrayListOf()
        adapter = RankingAdapterClass(rankingArrayList)
        fetchPlayersFromDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rankingrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RankingAdapterClass(rankingArrayList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: RankingAdapterClass.onItemClickListener{
            override fun onItemClick(position: Int, username: String) {
                val action = DashboardFragmentDirections.actionNavigationDashboardToNavigationProfile(username)
                findNavController().navigate(action)
                Toast.makeText(activity, "Seleccionado Jugador: $username", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun fetchPlayersFromDatabase() {
        myRef.orderByChild("totalPoints").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                rankingArrayList.clear()
                for (snapshot in dataSnapshot.children) {
                    val playername = snapshot.child("name").getValue(String::class.java)
                    val playerimage = R.drawable.baseline_account_circle_24
                    val player = RankingDataClass(playerimage, playername)
                    rankingArrayList.add(player)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}