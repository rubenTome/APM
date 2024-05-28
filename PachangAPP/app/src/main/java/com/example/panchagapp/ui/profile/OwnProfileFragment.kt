package com.example.panchagapp.ui.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

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
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private  val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    val key = "namekey"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                val file = File(currentPhotoPath)
                if (file.exists()) {
                    uploadImageToFirebase(file)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerNameTextView = view.findViewById(R.id.textView4)
        playerpartidos = view.findViewById(R.id.partidosnum)
        playergoles = view.findViewById(R.id.golesnum)
        playermeangoles = view.findViewById(R.id.golesmednum)
        profilepic = view.findViewById(R.id.imageView4)
        editTextApodo = view.findViewById(R.id.inputapodo)
        spinnerPosition = view.findViewById(R.id.spinnerPos)

        val captureButton: Button = view.findViewById(R.id.photobutton)
        captureButton.setOnClickListener {
            checkPermissionsAndDispatch()
        }

        val retrievedValue = SharedPreferencesHelper.getString(requireContext(), key)!!
        val items = arrayOf("Portero", "Defensa", "Medio", "Delantero")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPosition.adapter = adapter

        getPlayerInfoByName(retrievedValue) { uid, playerData ->
            if (playerData != null) {
                val playerName = playerData["name"] as? String
                val playerApodo = playerData["nickname"] as? String
                val playerPos = playerData["playablePos"] as? String
                val stats = playerData["stats"] as? Map<String, Any>
                val games = stats?.get("games")
                val meanGoals = stats?.get("meanGoals")
                val totalGoals = stats?.get("totalGoals")
                val url = playerData["profilePic"] as? String

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

                if (!url.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(url)
                        .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image while loading
                        .error(R.drawable.baseline_account_circle_24) // Error image if loading fails
                        .transform(CircleCrop())
                        .into(profilepic)
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

    private fun checkPermissionsAndDispatch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_IMAGE_CAPTURE)
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(requireContext(), "Camera and storage permissions are required.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.panchagapp.fileprovider",
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun uploadImageToFirebase(file: File) {
        val storageRef = FirebaseStorage.getInstance().reference
        val profilePicRef = storageRef.child("profile_pics/${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        val uri = Uri.fromFile(file)
        val uploadTask = profilePicRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                val user = FirebaseAuth.getInstance().currentUser?.uid
                updateProfilePicUrlInFirebase(user!!,uri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfilePicUrlInFirebase(playerId: String, url: String) {
        val playerRef = playersRef.orderByChild("idplayer").equalTo(playerId)
        playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (playerSnapshot in dataSnapshot.children) {
                        playerSnapshot.ref.child("profilePic").setValue(url).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Glide.with(requireContext())
                                    .load(url)
                                    .placeholder(R.drawable.baseline_account_circle_24)
                                    .error(R.drawable.baseline_account_circle_24)
                                    .transform(CircleCrop())
                                    .into(profilepic)
                            } else {
                                Toast.makeText(requireContext(), "Fallo al modificar URL de imagen.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No existe tal jugador.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                println("Error: ${databaseError.message}")
            }
        })
    }
    private fun updateApodoInFirebase(uid: String, apodo: String?) {
        val playerRef = playersRef.child(uid)
        playerRef.child("nickname").setValue(apodo)
    }

    private fun updatePositionInFirebase(uid: String, position: String) {
        val playerRef = playersRef.child(uid)
        playerRef.child("playablePos").setValue(position)
    }

    private fun getPlayerInfoByName(playerName: String, callback: (String, Map<String, Any>?) -> Unit) {
        playersRef.orderByChild("name").equalTo(playerName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (playerSnapshot in snapshot.children) {
                        val uid = playerSnapshot.key
                        val playerData = playerSnapshot.value as? Map<String, Any>
                        if (uid != null && playerData != null) {
                            callback(uid, playerData)
                            return
                        }
                    }
                }
                callback("", null)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OwnProfileFragment", "Database error: ${error.message}")
                callback("", null)
            }
        })
    }

    private fun formatText(prefix: String, value: String?): SpannableStringBuilder {
        val boldSpan = StyleSpan(Typeface.BOLD)
        val spannableString = SpannableStringBuilder(prefix + (value ?: ""))
        spannableString.setSpan(boldSpan, 0, prefix.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }
}