package com.example.panchagapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R

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
        val eventbutton = view.findViewById<Button>(R.id.eventbutton)
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.historialrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = HistorialAdapterClass(historialArrayList)
        recyclerView.adapter = adapter
    }

        companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistorialEquipoFragment().apply {
                arguments = Bundle().apply {
                }
            }
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


}