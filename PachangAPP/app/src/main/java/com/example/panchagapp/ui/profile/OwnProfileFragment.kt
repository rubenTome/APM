package com.example.panchagapp.ui.profile

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.panchagapp.R
import com.example.panchagapp.util.SharedPreferencesHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 * Use the [OwnProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OwnProfileFragment : Fragment(R.layout.fragment_ownprofile) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var playerNameTextView: TextView
    private lateinit var playerNameApodo: TextView
    private lateinit var playerNamepos: TextView
    private lateinit var playergoles: TextView
    private lateinit var playermeangoles: TextView
    private lateinit var playerpartidos: TextView
    private lateinit var profilepic: ImageView
    private lateinit var editTextApodo: TextInputEditText
    private lateinit var spinnerPosition: Spinner
    val database = FirebaseDatabase.getInstance()
    val playersRef = database.getReference("players")
    val key = "namekey"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            playerNameTextView = view.findViewById(R.id.textView4)
            playerpartidos = view.findViewById(R.id.partidosnum)
            playergoles= view.findViewById(R.id.golesnum)
            playermeangoles = view.findViewById(R.id.golesmednum)
            profilepic = view.findViewById(R.id.imageView4)
            editTextApodo = view.findViewById(R.id.inputapodo)
            spinnerPosition = view.findViewById(R.id.spinnerPos)

        var retrievedValue = SharedPreferencesHelper.getString(requireContext(), key)!!
        val items = arrayOf("Portero", "Defensa", "Medio","Delantero")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPosition.adapter = adapter




        getPlayerInfoByName(retrievedValue) { uid,playerData ->
            if (playerData != null) {
                val playerName = playerData["name"] as? String
                val playerApodo = playerData["nickname"] as? String
                val playerPos = playerData["playablePos"] as? String
                val stats = playerData.get("stats") as? Map<String, Any>
                val games = stats?.get("games")
                val meanGoals = stats?.get("meanGoals")
                val totalGoals = stats?.get("totalGoals")

                editTextApodo.hint = playerApodo
                val positionIndex = (spinnerPosition.adapter as ArrayAdapter<String>).getPosition(playerPos)
                spinnerPosition.setSelection(positionIndex)
                playerNameTextView.text = formatText("Nombre: ", playerName)
                playergoles.text = totalGoals.toString()
                playermeangoles.text = meanGoals.toString()
                playerpartidos.text = games.toString()

                editTextApodo.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        val apodo = s?.toString()
                        updateApodoInFirebase(uid, apodo)
                    }
                })

                spinnerPosition.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedPos = parent?.getItemAtPosition(position).toString()
                        updatePositionInFirebase(uid, selectedPos)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }


            } else {
                // Player not found
                Toast.makeText(requireContext(), "Player not found.", Toast.LENGTH_SHORT).show()
                println("Player not found.")
                // Navigate back to previous fragment
                findNavController().popBackStack()
            }
        }
    }


    private fun updateApodoInFirebase(uid: String, apodo: String?) {
        uid?.let {
            val playerRef = playersRef.child(uid)
            playerRef.child("nickname").setValue(apodo)
        }
    }
    private fun updatePositionInFirebase(uid: String, position: String?) {
        uid?.let {
            val playerRef = playersRef.child(uid)
            playerRef.child("playablePos").setValue(position)
        }
    }


    private fun formatText(prefix: String, value: String?): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(prefix)
        spannableStringBuilder.append(value ?: "")

        // Make the prefix bold
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            prefix.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableStringBuilder
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ownprofile, container, false)
    }

    // Function to retrieve player information by name
    fun getPlayerInfoByName(playerName: String?, callback: (String, HashMap<String, Any>?) -> Unit) {
        playersRef.orderByChild("name").equalTo(playerName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (playerSnapshot in dataSnapshot.children) {
                            val uid = playerSnapshot.key
                            val playerData = playerSnapshot.value as HashMap<String, Any>?
                            callback(uid!!, playerData) // Pass UID along with player data

                            val user = FirebaseAuth.getInstance().currentUser
                            val profilePicUrl = user?.photoUrl.toString()

                            if (!profilePicUrl.isNullOrEmpty()) {
                                Glide.with(requireContext())
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image while loading
                                    .error(R.drawable.baseline_account_circle_24) // Error image if loading fails
                                    .transform(CircleCrop())
                                    .into(profilepic)
                            }


                            Log.d("MainActivity", playerData.toString())
                            return // Exit loop after the first match if you only expect one player with the given name
                        }
                    } else {
                        // Player not found
                        Toast.makeText(requireContext(), "Player not found.", Toast.LENGTH_SHORT).show()
                        println("Player not found.")
                        // Navigate back to previous fragment
                        findNavController().popBackStack()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    println("Error: ${databaseError.message}")
                }
            })
    }


}