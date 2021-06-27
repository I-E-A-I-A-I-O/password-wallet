package com.example.passwordwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        val navController = Navigation.findNavController(this, R.id.bottom_nav_fragment_container)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_add_password, R.id.navigation_generate_password
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}