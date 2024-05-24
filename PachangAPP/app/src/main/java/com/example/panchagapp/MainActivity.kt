package com.example.panchagapp

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import com.bumptech.glide.Glide
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.panchagapp.databinding.ActivityMainBinding
import com.example.panchagapp.ui.home.HomeFragment
import com.example.panchagapp.ui.settings.SettingsFragment
import com.example.panchagapp.ui.signin.SignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class   MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerlayout: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var isFragmentOpen = false
    private var currentBrightness = -1f // Initial brightness value

    data class WeatherData(
        val name: String,
        val main: Main,
        val weather: List<Weather>
    )

    data class Main(
        val temp: Double
    )

    data class Weather(
        val icon: String
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("UserEmail")
        val username = intent.getStringExtra("Username")
        val photo = intent.getStringExtra("Photo")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor == null) {
            Toast.makeText(this, "No hay sensor de luz, no se ajustará el brillo", Toast.LENGTH_SHORT).show()
        }

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_clientid))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val navView: BottomNavigationView = binding.navView
        val toolbar: Toolbar = binding.toolbar
        val sidemenu : NavigationView = binding.sideView
        val logoutll : LinearLayout = binding.logoutll
        drawerlayout= findViewById(R.id.drawer_layout)
        val headerview = sidemenu.getHeaderView(0)
        if (username != null) {
            headerview.findViewById<TextView>(R.id.headername).text = username
        }
        if (email != null) {
            headerview.findViewById<TextView>(R.id.headeremail).text = email
        }
        if (photo != null) {
            Glide.with(this)
                .load(photo)
                .circleCrop()
                .into(headerview.findViewById(R.id.headerphoto))
        } else {
            headerview.findViewById<ImageView>(R.id.headerphoto).setImageResource(R.drawable.baseline_account_circle_24)
        }

        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);

        logoutll.setOnClickListener {
            val Intent = Intent(this,SignIn::class.java)
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
            signOutAndStartSignInActivity()
            val sharedPref = this.getSharedPreferences("login_state", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", false)
                    apply()
                }
            startActivity(Intent)

        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentlayout, HomeFragment()).commit()
            sidemenu.setCheckedItem(R.id.sidenav_home)
        }


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        navView.setupWithNavController(navController)
        sidemenu.setupWithNavController(navController)
        sidemenu.setNavigationItemSelectedListener(this)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.sidenav_settings),
            drawerlayout
        )
        setupActionBarWithNavController(navController,appBarConfiguration)
    }






    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@MainActivity, SignIn::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sidenav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentlayout, HomeFragment())
                    .commit()
            }
            R.id.sidenav_settings -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
            }
            R.id.sidenav_share -> {
               shareApp()
            }
            R.id.sidenav_about -> {
                val url = "https://github.com/rubenTome/APM"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Prueba esta aplicación para crear pachangas!!!: PachangAPP")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir por:"))
    }

    override fun onBackPressed() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)){
            drawerlayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         if (item.itemId == R.id.appbar_profile) {
             Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            val action = MobileNavigationDirections.actionGlobalViewPagerFragment("APPBAR")
            navController.navigate(action)
            return true
        }   else
        {
            return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_LIGHT) {
                val lightValue = it.values[0]
                //Filtro paso bajo
                val smoothedLightValue = smoothValue(lightValue)
                adjustBrightness(smoothedLightValue)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    private fun smoothValue(rawValue: Float): Float {
        val alpha = 0.1f
        currentBrightness = currentBrightness * (1 - alpha) + rawValue * alpha
        return currentBrightness
    }

    private fun adjustBrightness(lightValue: Float) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = lightValue / SensorManager.LIGHT_FULLMOON
        window.attributes = layoutParams
    }
}



