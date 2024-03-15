package com.example.panchagapp.ui.dashboard_ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: RankingAdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var rankingArrayList: ArrayList<RankingDataClass>
    lateinit var eventimageList: Array<Int>
    lateinit var eventnameList: Array<String>

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
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.rankingrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RankingAdapterClass(rankingArrayList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: RankingAdapterClass.onItemClickListener{
            override fun onItemClick(position: Int) {
                findNavController().navigate(R.id.action_navigation_dashboard_to_navigation_profile)
                Toast.makeText(activity, "Seleccionado Jugador", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun dataInitialize() {
        rankingArrayList = arrayListOf<RankingDataClass>()
        eventimageList = arrayOf(
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
        )

        eventnameList = arrayOf(
            "Jugador 1",
            "Jugador 2",
            "Jugador 3" ,
            "Jugador 4",
            "Jugador 5",
            "Jugador 6",
            "Jugador 7",
            "Jugador 8",
            "Jugador 9" ,
            "Jugador 10",
            "Jugador 11",
            "Jugador 12",
        )

        for (i in eventimageList.indices) {
            val dataClass = RankingDataClass(eventimageList[i], eventnameList[i])
            rankingArrayList.add(dataClass)
        }
    }
}