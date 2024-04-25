package com.example.panchagapp.ui.crearEvento

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.panchagapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CrearEventoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrearEventoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val database = FirebaseDatabase.getInstance()
    val eventosRef = database.getReference("events")


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
        return inflater.inflate(R.layout.fragment_crear_evento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val items = arrayOf("Evento Casual", "Torneo")
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val crearbutton = view.findViewById<Button>(R.id.crearevento)
        crearbutton.setOnClickListener {
            val nombreventotv = view.findViewById<TextInputEditText>(R.id.nombreeventotv)
            val nombreevento = nombreventotv.text.toString()
            val dateedit = view.findViewById<EditText>(R.id.editTextDate)
            val date = dateedit.text.toString()
            val spinneroption = spinner.selectedItem.toString()
            val playered = view.findViewById<EditText>(R.id.editTextNumber)
            val maxPlayers = playered.text.toString()
            agregarEvento(date,"HOLA","Aqui",maxPlayers.toInt(),nombreevento,"nadie",spinneroption)
            findNavController().navigate(R.id.action_navigation_creareventos_to_navigation_home)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CrearEventoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // Método para agregar un nuevo elemento al array
    private fun agregarEvento(datatime: String, descripcion: String, location: String, maxPlayers: Int, name: String, players: String, type: String) {
        // Obtener referencia a la base de datos

        // Obtener el valor actual del array de eventos
        eventosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eventosList: MutableList<Map<String, Any>> = dataSnapshot.getValue() as MutableList<Map<String, Any>>? ?: mutableListOf()

                // Crear el nuevo evento
                val nuevoEvento = mapOf(
                    "datatime" to datatime,
                    "descripcion" to descripcion,
                    "location" to location,
                    "maxPlayers" to maxPlayers,
                    "name" to name,
                    "players" to players,
                    "type" to type
                )

                // Agregar el nuevo evento a la lista de eventos
                eventosList.add(nuevoEvento)

                // Actualizar la lista de eventos en la base de datos
                GlobalScope.launch {
                    try {
                        eventosRef.setValue(eventosList).await()
                        Toast.makeText(activity,"Nuevo evento agregado correctamente.",Toast.LENGTH_SHORT).show()
                    } catch (exception: Exception) {
                        Toast.makeText(activity,"Error al agregar el nuevo evento: $exception",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar cancelación de la operación
                println("Operación cancelada: ${databaseError.message}")
            }
        })
    }

}