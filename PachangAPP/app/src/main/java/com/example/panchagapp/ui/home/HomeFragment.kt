package com.example.panchagapp.ui.home

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.panchagapp.R
import com.example.panchagapp.databinding.FragmentHomeBinding
import com.example.panchagapp.ui.listaeventos.ListaEventosFragmentDirections


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val eventbutton = root.findViewById<Button>(R.id.eventbutton)
        val listbutton = root.findViewById<Button>(R.id.listbutton)
        val location1 = root.findViewById<ImageView>(R.id.location1)
        val location2 = root.findViewById<ImageView>(R.id.location2)

        eventbutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_creareventos)
            Toast.makeText(activity, "Crear Evento", Toast.LENGTH_SHORT).show()
        }
        location1.setOnClickListener {
            Toast.makeText(activity, "Datos Evento", Toast.LENGTH_SHORT).show()
            val directions = HomeFragmentDirections.actionNavigationHomeToNavigationEvento2("Evento 1")
            findNavController().navigate(directions)
        }
        location2.setOnClickListener {
            val directions = HomeFragmentDirections.actionNavigationHomeToNavigationTorneo("Torneo 1")
            Toast.makeText(activity, "Datos Torneo", Toast.LENGTH_SHORT).show()
            findNavController().navigate(directions)
        }

        listbutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_listaeventos)
            Toast.makeText(activity, "Lista de Eventos", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}