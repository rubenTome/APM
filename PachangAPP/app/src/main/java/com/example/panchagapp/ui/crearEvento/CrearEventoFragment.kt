package com.example.panchagapp.ui.crearEvento

import TeamAdapterClass
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.ui.inscribirseEventos.TeamDataClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    private lateinit var recyclerView: RecyclerView
    private val eventosRef = database.getReference("events")
    private val teamsRef = database.getReference("teams")
    private val calendar = Calendar.getInstance()
    private lateinit var dateedit: EditText
    private lateinit var datehour: EditText
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var adapterrv: TeamAdapterClass
    private lateinit var mlistener: TeamAdapterClass.onItemClickListener


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
        val layoutManager = LinearLayoutManager(context)
        val args = CrearEventoFragmentArgs.fromBundle(requireArguments())
        val latLng = args.coordenates
        latitude = latLng.latitude.toString()
        longitude = latLng.longitude.toString()

        mlistener = object : TeamAdapterClass.onItemClickListener {
            override fun onItemClick(position: Int) {
            }
        }


        // Setup the spinner
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val items = arrayOf("Evento Casual", "Torneo")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val defaultValue = "Evento Casual"
        val defaultPosition = items.indexOf(defaultValue)
        spinner.setSelection(defaultPosition)

        // Find the views
        val crearbutton = view.findViewById<Button>(R.id.crearevento)
        val nombreventotv = view.findViewById<TextInputEditText>(R.id.nombreeventotv)
        dateedit = view.findViewById<EditText>(R.id.editTextDate)
        datehour = view.findViewById<EditText>(R.id.editTextHour)
        val playered = view.findViewById<EditText>(R.id.editTextNumber)

        val capacidactv = view.findViewById<TextView>(R.id.textView6)
        val equipostv = view.findViewById<TextView>(R.id.textViewequipos)
        val addTeamButton = view.findViewById<FloatingActionButton>(R.id.addTeamButton)


        // Setup date and time pickers
        dateedit.setOnClickListener { showDateDialog() }
        datehour.setOnClickListener { showTimePicker() }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                // Update the TextView based on the selected item
                when (selectedItem) {
                    "Torneo" -> {
                        capacidactv.text = "Miembros por equipo"
                        equipostv.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                        addTeamButton.visibility = View.VISIBLE

                    }

                    "Evento Casual" -> {
                        capacidactv.text = "Capacidad Maxima"
                        equipostv.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        addTeamButton.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                capacidactv.text = "Capacidad Maxima"
                equipostv.visibility = View.GONE
                recyclerView.visibility = View.GONE
                addTeamButton.visibility = View.GONE
            }
        }
        var dataList = ArrayList<TeamDataClass>()
        adapterrv = TeamAdapterClass(dataList, R.layout.layout_team_list_item_crear)
        // Set the item click listener for the adapter
        adapterrv.setOnItemClickListener(object : TeamAdapterClass.onItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle item click here
            }
        })



        addTeamButton.setOnClickListener {
            showNameInputDialog()
        }

        recyclerView = view.findViewById(R.id.recyclerViewTeams)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapterrv

        adapterrv.setOnItemClickListener(object : TeamAdapterClass.onItemClickListener {
            override fun onItemClick(position: Int) {
                adapterrv.removeItem(position)
            }
        })

        crearbutton.setOnClickListener {
            val nombreevento = nombreventotv.text.toString()
            val date = dateedit.text.toString()
            val hour = datehour.text.toString()
            val spinneroption = spinner.selectedItem.toString()
            val maxPlayers = playered.text.toString()

            if (nombreevento.isNotEmpty() && date.isNotEmpty() && hour.isNotEmpty() && maxPlayers.isNotEmpty()) {
                val teams = adapterrv.dataList.map { it.teamTitle }
                showDescriptionInputDialog { enteredText ->
                    agregarEvento(date, hour, enteredText , maxPlayers.toInt(), nombreevento, spinneroption, teams )
                }

            } else {
                // Handle the case when maxPlayers is empty or show a validation error
                Toast.makeText(
                    requireContext(),
                    "Por favor rellene todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun showNameInputDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_input_name, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Introduzca el nombre")
        dialogBuilder.setPositiveButton("Añadir") { dialog, which ->
            val enteredName = editTextName.text.toString().trim()
            if (enteredName.isNotEmpty()) {
                if (!isNameAlreadyAdded(enteredName)) {
                    adapterrv.addItem(enteredName)
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun showDescriptionInputDialog(callback: (String) -> Unit) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_input_name, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        editTextName.hint = "Ej. Traer balon"
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Introduzca comentarios si quiere")
        dialogBuilder.setPositiveButton("Añadir") { dialog, which ->
            val enteredName = editTextName.text.toString().trim()
            callback(enteredName)
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun isNameAlreadyAdded(name: String): Boolean {
        for (item in adapterrv.dataList) {
            if (item.teamTitle == name) {
                return true
            }
        }
        return false
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = sdf.format(selectedDate.time)
                dateedit.setText(formattedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Set the minimum date to the current day
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePicker = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { view: TimePicker, hourOfDay: Int, minute: Int ->
                // Set the selected time on the edit text
                datehour.setText("$hourOfDay:$minute")
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 24-hour format
        )
        timePicker.show()
    }

    // Método para agregar un nuevo elemento al array
    private fun agregarEvento(
        datatime: String,
        hour: String,
        descripcion: String,
        maxPlayers: Int,
        name: String,
        type: String,
        teams: List<String>
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        userId?.let { uid ->
            eventosRef.orderByChild("name").equalTo(name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(activity, "El evento ya existe.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            var totalsize = 0
                            if (teams.size != 0) {
                                totalsize = teams.size * maxPlayers
                            } else {
                                totalsize = maxPlayers
                            }

                            eventosRef.push().setValue(
                                mapOf(
                                    "datatime" to datatime,
                                    "hour" to hour,
                                    "descripcion" to descripcion,
                                    "location" to mapOf(
                                        "lat" to latitude,
                                        "lon" to longitude
                                    ),
                                    "maxPlayers" to totalsize,
                                    "name" to name,
                                    "players" to emptyList<String>(),
                                    "type" to type,
                                    "teams" to teams
                                )
                            ).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    addTeamsToTeamsNode(teams,name,maxPlayers, listOf(uid) )
                                    Toast.makeText(
                                        activity,
                                        "Nuevo evento agregado correctamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigate(R.id.action_navigation_creareventos_to_navigation_home)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Error al agregar el evento.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("Operación cancelada: ${databaseError.message}")
                    }
                })
        }
    }

    private fun addTeamsToTeamsNode(teams: List<String>, eventName: String, maxPlayers: Int, players: List<String>) {
        teams.forEach { teamName ->
            val teamRef = teamsRef.child(eventName).child(teamName)
            val teamData = mapOf(
                "name" to teamName,
                "maxPlayers" to maxPlayers,
                "participants" to players
            )
            teamRef.setValue(teamData)
        }
    }







}