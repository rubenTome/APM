package com.example.panchagapp.ui.profile

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.panchagapp.R
import com.example.panchagapp.util.SharedPreferencesHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var playerNameTextView: TextView
    private lateinit var playerNameApodo: TextView
    private lateinit var playerNamepos: TextView
    private lateinit var playergoles: TextView
    private lateinit var playermeangoles: TextView
    private lateinit var playerpartidos: TextView
    val database = FirebaseDatabase.getInstance()
    val playersRef = database.getReference("players")
    val key = "namekey"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            playerNameTextView = view.findViewById(R.id.textView4)
            playerNameApodo = view.findViewById(R.id.textView3)
            playerNamepos = view.findViewById(R.id.textView)
            playerpartidos = view.findViewById(R.id.partidosnum)
            playergoles= view.findViewById(R.id.golesnum)
            playermeangoles = view.findViewById(R.id.golesmednum)
        var retrievedValue = SharedPreferencesHelper.getString(requireContext(), key)!!
        val args = safeArgs()
        if (retrievedValue == null || args != null) {
            retrievedValue = args?.getString("playername")!!
        }
        getPlayerInfoByName(retrievedValue) { playerData ->
            if (playerData != null) {
                val playerName = playerData["name"] as? String
                val playerapodo = playerData["nickname"] as? String
                val playerpos = playerData["playablePos"] as? String
                val stats = playerData.get("stats") as? Map<String, Any>
                val games = stats?.get("games")
                val meanGoals = stats?.get("meanGoals")
                val totalGoals = stats?.get("totalGoals")
                // Access other player attributes similarly


                playerNameTextView.text = formatText("Nombre: ", playerName)
                playerNameApodo.text = formatText("Apodo: ", playerapodo)
                playerNamepos.text = formatText("Posición: ", playerpos)
                playergoles.text = totalGoals.toString()
                playermeangoles.text = meanGoals.toString()
                playerpartidos.text = games.toString()
            } else {
                // Player not found
                Toast.makeText(requireContext(), "Player not found.", Toast.LENGTH_SHORT).show()
                println("Player not found.")
                // Navigate back to previous fragment
                findNavController().popBackStack()
            }
        }
    }

    fun Fragment.safeArgs(): Bundle? {
        return arguments
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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    // Function to retrieve player information by name
    fun getPlayerInfoByName(playerName: String?, callback: (HashMap<String, Any>?) -> Unit) {
        playersRef.orderByChild("name").equalTo(playerName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (playerSnapshot in dataSnapshot.children) {
                            val playerData = playerSnapshot.value as HashMap<String, Any>?
                            callback(playerData)
                            Log.d("MainActivity", playerData.toString())
                            return // Exit loop after the first match if you only expect one player with the given name
                        }
                    } else {
                        // Player not found
                        callback(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    println("Error: ${databaseError.message}")
                    callback(null)
                }
            })
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}