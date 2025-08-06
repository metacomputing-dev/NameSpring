package com.metacomputing.namespring.ui.utils

import android.util.Log
import com.metacomputing.seed.logger.SeedLogger

class AndroidLogger(level: Level = Level.ALL): SeedLogger(level) {
    override fun logImpl(level: Level, tag: String, desc: String) {
        when (level) {
            Level.ERROR -> Log.e(tag, desc)
            Level.WARNING -> Log.w(tag, desc)
            Level.INFO -> Log.i(tag, desc)
            Level.DEBUG -> Log.d(tag, desc)
            else -> Log.d(tag, desc)
        }
    }
}