package com.example.panchagapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide
import android.os.Bundle
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class   MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerlayout: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var isFragmentOpen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("UserEmail")
        val username = intent.getStringExtra("Username")
        val photo = intent.getStringExtra("Photo")


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
            // If no photo is available, you can set a placeholder image
            headerview.findViewById<ImageView>(R.id.headerphoto).setImageResource(R.drawable.baseline_account_circle_24)
        }


        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);

        logoutll.setOnClickListener {
            val Intent = Intent(this,SignIn::class.java)
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
            signOutAndStartSignInActivity()
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
        val Intent = Intent(this,SignIn::class.java)
        when(item.itemId) {
            R.id.sidenav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragmentlayout, HomeFragment()).commit()
            R.id.sidenav_settings -> supportFragmentManager.beginTransaction().replace(R.id.drawer_layout, SettingsFragment()).commit()
            R.id.sidenav_share -> Toast.makeText(this, "Compartir", Toast.LENGTH_SHORT).show()
            R.id.sidenav_about -> Toast.makeText(this, "Sobre el proyecto", Toast.LENGTH_SHORT).show()
        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
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



}