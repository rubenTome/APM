package com.example.panchagapp.ui.signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.panchagapp.MainActivity
import com.example.panchagapp.R
import com.example.panchagapp.util.SharedPreferencesHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignIn : AppCompatActivity() {


    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    val playersRef = database.getReference("players")

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sig_in)

         auth = FirebaseAuth.getInstance()

         val loginbutton = findViewById<Button>(R.id.signInButton)
         loginbutton.setOnClickListener {
             signIn()
         }

    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_clientid))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val key = "namekey"
                    val user = auth.currentUser
                    SharedPreferencesHelper.saveString(application, key, user?.displayName!!)
                    SharedPreferencesHelper.saveBoolean(application, "isLoggedIn", true)
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                    if (isNewUser) {
                       agregarplayer(user?.displayName!!,"","",0,"",0)
                    }

                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java).also {
                        it.putExtra("UserEmail", user?.email)
                        it.putExtra("Username", user?.displayName)
                        it.putExtra("Photo", user?.photoUrl.toString())
                    })
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun agregarplayer(nombre: String, nickname: String, position: String, stats: Int, team: String, totalpoints: Int) {
        // Obtener referencia a la base de datos

        // Obtener el valor actual del array de eventos
        playersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val playersList: MutableList<Map<String, Any>> = dataSnapshot.getValue() as MutableList<Map<String, Any>>? ?: mutableListOf()

                // Crear el nuevo evento
                val nuevoEvento = mapOf(
                    "name" to nombre,
                    "nickname" to nickname,
                    "playablePos" to position,
                    "stats" to stats,
                    "team" to team,
                    "totalPoints" to totalpoints
                )

                // Agregar el nuevo evento a la lista de eventos
                playersList.add(nuevoEvento)

                // Actualizar la lista de eventos en la base de datos
                GlobalScope.launch {
                    try {
                        playersRef.setValue(playersList).await()
                        println("Nuevo jugador agregado correctamente.")
                    } catch (exception: Exception) {
                        println("Error al agregar el nuevo jugador: $exception")
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