package com.metacomputing.namespring

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.databinding.ActivityMainBinding
import com.metacomputing.namespring.ui.HomeFragment
import com.metacomputing.namespring.ui.NavigationHeader
import com.metacomputing.namespring.ui.ProfileListFragment

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val SPLASH_VISIBLE_MIN_DURATION = 1000L
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        showSplashScreen()
        ProfileManager.putMockup(baseContext) // TODO for debugging
        TaskManager.launch("Initialize Seed Engine",
            block = {
                SeedProxy.initialize()
            }
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        initNavigationView()
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
        splash.setKeepOnScreenCondition { splashVisible || !SeedProxy.initialized }
        Handler(Looper.getMainLooper()).postDelayed({
            splashVisible = false
        }, SPLASH_VISIBLE_MIN_DURATION)
    }

    private fun openFragment(fragment: Fragment, stack: Boolean = true) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .apply { if (stack) addToBackStack(null) }
            .commit()
    }

    private fun initNavigationView() {
        with(binding) {
            val toggle = ActionBarDrawerToggle(
                this@MainActivity, mainDrawerLayout, binding.toolbar.toolbar,
                R.string.open,
                R.string.close
            )
            mainDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            navigationView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_item_home -> openFragment(HomeFragment())
                    R.id.menu_item_profile_management -> openFragment(ProfileListFragment())
                }
                mainDrawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            NavigationHeader(this@MainActivity, navigationView)
        }
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