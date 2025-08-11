package com.metacomputing.namespring.model.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.model.dto.DTO
import com.metacomputing.namespring.model.dto.DTOProfile
import com.metacomputing.namespring.model.viewmodel.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

private const val KEY_PROFILE = "profiles_json"
private const val KEY_PROFILE_PROFILE_MAIN = "profiles_state_json"
private val Context.profileStore by preferencesDataStore(name = KEY_PROFILE)
private val Context.profileStateStore by preferencesDataStore(name = KEY_PROFILE_PROFILE_MAIN)

class LocalDataSource: UserDataSource {
    companion object {
        const val TAG = "LocalDataSource"
        private val JSON_PROFILES = stringPreferencesKey(KEY_PROFILE)
        private val JSON_PROFILE_MAIN_ID = stringPreferencesKey(KEY_PROFILE_PROFILE_MAIN)

    }

    override fun saveProfileData(context: Context): Flow<Unit> = flow {
        ProfileManager.profiles.value.map { DTOProfile.from(it) }.run {
            context.profileStore.edit { it[JSON_PROFILES] = DTO.JSON.encodeToString(this) }
            Log.i(TAG, "Saved Profiles(count=${this.size})")
        }
    }

    override fun loadProfileData(context: Context)
    : Flow<List<Profile>> = context.profileStore.data.map { prefs ->
        (prefs[JSON_PROFILES]?.let {
            DTO.JSON.decodeFromString<List<DTOProfile>>(it)
        }?.map { it.toProfile() } ?: emptyList()).also {
            Log.i(TAG, "Loaded Profiles(count=${it.size})")
        }
    }

    override fun saveProfileSelection(context: Context): Flow<Unit> = flow {
        ProfileManager.mainProfileId.value?.run {
            context.profileStateStore.edit {
                it[JSON_PROFILE_MAIN_ID] = this
            }
            Log.i(TAG, "Saved Main Profile Selection (${ProfileManager.mainProfile?.title})")
        }
    }

    override fun loadProfileSelection(context: Context)
    : Flow<String> = context.profileStateStore.data.map { prefs ->
        (prefs[JSON_PROFILE_MAIN_ID] ?: "").also {
            Log.i(TAG, "Loaded Main Profile Selection(${it})")
        }
    }
}