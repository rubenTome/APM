package com.example.panchagapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class   MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerlayout: DrawerLayout
    private var isFragmentOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val toolbar: Toolbar = binding.toolbar
        val sidemenu : NavigationView = binding.sideView
        val logoutll : LinearLayout = binding.logoutll
        drawerlayout= findViewById(R.id.drawer_layout)

        logoutll.setOnClickListener {
            val Intent = Intent(this,SignIn::class.java)
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
            startActivity(Intent)

        }



        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentlayout, HomeFragment()).commit()
            sidemenu.setCheckedItem(R.id.sidenav_home)
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.sidenav_home, R.id.sidenav_settings, R.id.sidenav_share),
            drawerlayout
        )

        toggle =  ActionBarDrawerToggle(this,drawerlayout, toolbar, R.string.open,R.string.close)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()



        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        navView.setupWithNavController(navController)
        sidemenu.setupWithNavController(navController)
        sidemenu.setNavigationItemSelectedListener(this)


        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);
        setupActionBarWithNavController(navController)
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
        } else {
            return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }



}