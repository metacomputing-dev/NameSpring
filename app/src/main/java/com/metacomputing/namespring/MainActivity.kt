package com.metacomputing.namespring

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        var splashVisible = true
        splash.setKeepOnScreenCondition { splashVisible }
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
                splashVisible = false
            }, 1000)
        setContentView(R.layout.activity_main)
    }
}