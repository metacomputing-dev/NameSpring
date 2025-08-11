package com.metacomputing.namespring.model.repository

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.metacomputing.namespring.control.ProfileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserDataCenter(
    private val localDataSource: UserDataSource = LocalDataSource()
    // TODO add server data source
) {
    var initialized = false
        private set

    private val ioScope = CoroutineScope(Dispatchers.IO)
    // TODO run CRUD tasks in async

    fun initialize(activity: AppCompatActivity) {
        runBlocking {
            // Load profile data
            localDataSource.loadProfileData(activity.baseContext).take(1).collect {
                if (it.isNotEmpty()) { // TODO remove this exception after debugging
                    ProfileManager.load(it)
                }
                initialized = true
                // Observe profile to sync data
                ProfileManager.profiles.drop(1).collect { list ->
                    ioScope.launch {
                        // TODO optimize it
                        localDataSource.saveProfileData(activity.baseContext).take(1).collect {}
                    }
                }
            }
        }
    }
}