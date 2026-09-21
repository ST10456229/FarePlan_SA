package com.example.fareplansa

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

object NavigationHelper {

    fun setupBottomNavigation(activity: Activity, bottomNav: BottomNavigationView, currentItemId: Int) {
        if (currentItemId != -1) {
            bottomNav.selectedItemId = currentItemId
        } else {
            // Uncheck all items if none should be selected
            bottomNav.menu.setGroupCheckable(0, true, false)
            for (i in 0 until bottomNav.menu.size()) {
                bottomNav.menu.getItem(i).isChecked = false
            }
            bottomNav.menu.setGroupCheckable(0, true, true)
        }

        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == currentItemId) return@setOnItemSelectedListener true

            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(activity, DashboardActivity::class.java)
                R.id.nav_plan -> Intent(activity, CreateTripActivity::class.java)
                R.id.nav_trips -> Intent(activity, TripsActivity::class.java)
                R.id.nav_live -> Intent(activity, LiveActivity::class.java)
                R.id.nav_budget -> Intent(activity, BudgetActivity::class.java)
                else -> null
            }

            intent?.let {
                it.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                activity.startActivity(it)
                // Do not finish the activity to allow back navigation if desired,
                // or finish if you want a flat navigation structure.
                true
            } ?: false
        }
    }

    fun setupDrawer(activity: Activity, drawerLayout: DrawerLayout, btnMenu: View?) {
        btnMenu?.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val btnSettings = activity.findViewById<View>(R.id.btnSettings)
        val btnLogout = activity.findViewById<View>(R.id.btnLogout)
        val tvDrawerGreeting = activity.findViewById<TextView>(R.id.tvDrawerGreeting)

        val user = FirebaseAuth.getInstance().currentUser
        tvDrawerGreeting?.text = activity.getString(R.string.dashboard_hello, user?.email ?: activity.getString(R.string.dashboard_traveller))

        btnSettings?.setOnClickListener {
            activity.startActivity(Intent(activity, SettingsActivity::class.java))
            drawerLayout.closeDrawers()
        }

        btnLogout?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
            activity.finish()
        }
    }
    
    fun setupCommonActions(activity: Activity) {
        val imgProfile = activity.findViewById<View>(R.id.imgProfile)
        imgProfile?.setOnClickListener {
            activity.startActivity(Intent(activity, SettingsActivity::class.java))
        }
    }
}