package com.example.panchagapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    val database = FirebaseDatabase.getInstance()
    val playersRef = database.getReference("players")
    val key = "namekey"
    val retrievedValue = SharedPreferencesHelper.getString(requireContext(), key)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
        getPlayerInfoByName(retrievedValue) { dataSnapshot ->
            if (dataSnapshot != null) {
                // Player found
                for (playerSnapshot in dataSnapshot.children) {
                    val value = playerSnapshot.getValue<String>()
                }
            } else {
                // Player not found
                println("Player not found.")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    // Function to retrieve player information by name
    fun getPlayerInfoByName(playerName: String?, callback: (DataSnapshot?) -> Unit) {
        playersRef.orderByChild("name").equalTo(playerName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Player found, pass the dataSnapshot to the callback function
                        callback(dataSnapshot)
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