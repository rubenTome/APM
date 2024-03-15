package com.example.panchagapp.ui.inscribirseEventos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.panchagapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [inscribirseEventoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class inscribirseEventoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_inscribirse_evento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tvname = view.findViewById<TextView>(R.id.tveventname)
        var inscribirseButton = view.findViewById<Button>(R.id.buttoninscribirse)
        var trackbutton = view.findViewById<Button>(R.id.trackbutton)
        val bundle = arguments
        if (bundle == null) {
            Log.d("Confirmation", "FragmentEvento sin arguments")
            return
        }
        val args = inscribirseEventoFragmentArgs.fromBundle(bundle)
        if(args.eventName.isNullOrBlank()){
            tvname.text = "Evento sin Nombre"
        }
        else {
            tvname.text = args.eventName
        }

        inscribirseButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_evento_to_navigation_home)
            Toast.makeText(activity, "Inscrito en evento!", Toast.LENGTH_SHORT).show()
        }

        trackbutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_evento_to_navigation_direction)
            Toast.makeText(activity, "Como llegar a localizacion!", Toast.LENGTH_SHORT).show()
        }



    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            inscribirseEventoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}