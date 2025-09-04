package com.metacomputing.namespring.model.repository

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.metacomputing.namespring.control.FavoriteManager
import com.metacomputing.namespring.control.ProfileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserDataCenter(
    private val localDataSource: UserDataSource = LocalDataSource()
    // TODO add server data source
) {
    companion object {
        const val TAG = "UserDataCenter"
        @Volatile
        private var instance: UserDataCenter? = null

        fun getInstance(): UserDataCenter {
            if (instance == null) {
                instance = UserDataCenter()
            }
            return instance!!
        }
    }
    var initialized = false
        private set

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)
    // TODO run CRUD tasks in async

    fun initialize(activity: AppCompatActivity) {
        // Load and observe profile data
        mainScope.launch {
            val profiles = localDataSource.loadProfileData(activity)
            val selectionId = localDataSource.loadProfileSelection(activity)
            val favorites = localDataSource.loadFavorites(activity)

            Log.i(TAG, "Loading profile data from local data source (size=${profiles.size}")
            ProfileManager.loadProfiles(activity, profiles)
            if (selectionId.isNotEmpty() && ProfileManager.getProfileById(selectionId) != null) {
                ProfileManager.mainProfileId.value = selectionId
                Log.i(TAG, "Loading profile selection from local data source (title=${ProfileManager.mainProfile}")
            }

            Log.i(TAG, "Loading favorites from local data source (size=${favorites.size}")
            FavoriteManager.load(favorites)

            ProfileManager.observeProfileSelection(activity, ioScope) { id ->
                if (id.isEmpty() || ProfileManager.getProfileById(id) != null) {
                    localDataSource.saveProfileSelection(activity)
                    Log.i(TAG, "saved main profile selection to ${ProfileManager.getProfileById(id) ?: "EMPTY"}")
                }
            }

            ProfileManager.observeProfiles(activity, ioScope) {
                localDataSource.saveProfileData(activity.baseContext)
                Log.i(TAG, "saved profile data")
            }

            FavoriteManager.observeFavorites(activity, ioScope) {
                localDataSource.saveFavorites(activity)
                Log.i(TAG, "saved favorites")
            }

            initialized = true
            Log.i(TAG, "Initialized")
        }
    }
}