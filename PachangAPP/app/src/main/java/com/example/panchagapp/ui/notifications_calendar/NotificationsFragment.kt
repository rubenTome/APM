package com.example.panchagapp.ui.notifications_calendar

import android.os.Bundle
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

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: EventosAdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventosArrayList: ArrayList<EventosDataClass>
    lateinit var eventimageList: Array<Int>
    lateinit var eventnameList: Array<String>
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
        dataInitialize()
        recyclerView = view.findViewById(R.id.eventrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = EventosAdapterClass(eventosArrayList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: EventosAdapterClass.onItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItem = eventosArrayList[position]
                Toast.makeText(activity, "Seleccionado "+ selectedItem.eventTitle.toString(), Toast.LENGTH_SHORT).show()
                val action = MobileNavigationDirections.actionGlobalViewPagerFragment("CALENDAR")
                findNavController().navigate(action)
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}