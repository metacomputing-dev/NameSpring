package com.metacomputing.namespring

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.metacomputing.namespring.ui.HomeFragment
import com.metacomputing.namespring.ui.NavigationHeaderUI
import com.metacomputing.namespring.ui.ProfileListFragment

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        showSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeUI()
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    openFragment(HomeFragment(), false)
                }
            }
        })
    }

    override fun onResume() {
        openFragment(HomeFragment())
        super.onResume()
    }

    private fun showSplashScreen() {
        val splash = installSplashScreen()
        var splashVisible = true
        splash.setKeepOnScreenCondition { splashVisible }
        Handler(Looper.getMainLooper()).postDelayed({
            splashVisible = false
        }, 1000)
    }

    private fun initializeUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.main_drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        initNavigationView()
    }

    private fun openFragment(fragment: Fragment, stack: Boolean = true) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .apply { if (stack) addToBackStack(null) }
            .commit()
    }

    private fun initNavigationView() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> openFragment(HomeFragment())
                R.id.menu_item_profile_management -> openFragment(ProfileListFragment())
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        NavigationHeaderUI(this, navigationView).initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_drawer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i(TAG, "Menu item selected ${item.itemId}")

        return super.onOptionsItemSelected(item)
    }
}