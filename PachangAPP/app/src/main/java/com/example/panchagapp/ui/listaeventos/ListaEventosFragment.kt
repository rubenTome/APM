package com.example.panchagapp.ui.listaeventos

import android.os.Bundle
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

    lateinit var eventimageList: Array<Int>
    lateinit var eventnameList: Array<String>

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
        val eventbutton = view.findViewById<Button>(R.id.eventbutton)
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.eventrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventosAdapterClass(eventosArrayList)
        recyclerView.adapter = adapter

        eventbutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_listaeventos_to_navigation_creareventos)
            Toast.makeText(activity, "Crear Evento", Toast.LENGTH_SHORT).show()
        }
        adapter.setOnItemClickListener(object: EventosAdapterClass.onItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedItem = eventosArrayList[position]
                if (selectedItem.eventTitle.toString().startsWith("Torneo")) {
                    Toast.makeText(activity, "Seleccionado " + selectedItem.eventTitle.toString(), Toast.LENGTH_SHORT).show()
                    val directions = ListaEventosFragmentDirections.actionNavigationListaeventosToNavigationTorneo(selectedItem.eventTitle.toString())
                    findNavController().navigate(directions)
                } else if (selectedItem.eventTitle.toString().startsWith("Evento")) {
                    Toast.makeText(activity, "Seleccionado " + selectedItem.eventTitle.toString(), Toast.LENGTH_SHORT).show()
                    val directions = ListaEventosFragmentDirections.actionNavigationListaeventosToNavigationEvento(selectedItem.eventTitle.toString())
                    findNavController().navigate(directions)
                }
            }
        })
    }

    private fun dataInitialize() {
        eventosArrayList = arrayListOf<EventosDataClass>()
        eventimageList = arrayOf(
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
        )

        eventnameList = arrayOf(
            "Evento 1",
            "Evento 2",
            "Torneo 1",
            "Evento 3",
            "Evento 4",
            "Torneo 2",
        )

        for (i in eventimageList.indices) {
            val dataClass = EventosDataClass(eventimageList[i], eventnameList[i])
            eventosArrayList.add(dataClass)
        }
    }

}