package com.metacomputing.namespring.model.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.metacomputing.namespring.control.FavoriteManager
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.control.UserDataManager
import com.metacomputing.namespring.model.dto.DTO
import com.metacomputing.namespring.model.dto.DTOProfile
import com.metacomputing.namespring.model.data.Profile
import com.metacomputing.namespring.model.data.UserData
import com.metacomputing.namespring.model.service.ServiceToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val KEY_PROFILE = "profiles_json"
private const val KEY_PROFILE_PROFILE_MAIN = "profiles_state_json"

private const val KEY_FAVORITE = "favorites_json"

private const val KEY_USERDATA = "userdata"

private val Context.profileStore by preferencesDataStore(name = KEY_PROFILE)
private val Context.profileStateStore by preferencesDataStore(name = KEY_PROFILE_PROFILE_MAIN)
private val Context.favoriteStore by preferencesDataStore(name = KEY_FAVORITE)
private val Context.userdataStore by preferencesDataStore(name = KEY_USERDATA)

class LocalDataSource: UserDataSource {

    companion object {
        const val TAG = "LocalDataSource"
        private val JSON_PROFILES = stringPreferencesKey(KEY_PROFILE)
        private val JSON_PROFILE_MAIN_ID = stringPreferencesKey(KEY_PROFILE_PROFILE_MAIN)
        private val JSON_FAVORITES = stringPreferencesKey(KEY_FAVORITE)
        private val JSON_KEY_SEED = intPreferencesKey("userdata_seed")
        private val JSON_KEY_USER_INITIAL = booleanPreferencesKey("userdata_initial_user")
        private val JSON_KEY_TOKEN_PREFIX = stringPreferencesKey("userdata_token_")
        fun tokenKey(idx: Int) = stringPreferencesKey("${JSON_KEY_TOKEN_PREFIX}$idx")
    }

    override suspend fun isInitialUser(context: Context): Boolean {
        return context.userdataStore.data.map { prefs ->
            (prefs[JSON_KEY_USER_INITIAL] ?: true).also {
                Log.i(TAG, "Initial User detected")
            }
        }.first()
    }

    override suspend fun disableInitialUser(context: Context) {
        context.userdataStore.edit {
            it[JSON_KEY_USER_INITIAL] = false
        }
        Log.i(TAG, "Disabled Initial User")
    }

    override suspend fun saveUserData(context: Context) {
        UserDataManager.userData.seed.run {
            context.userdataStore.edit { it[JSON_KEY_SEED] = 0 }
            Log.i(TAG, "Saved Seed(amount=$this)")
        }
    }

    override suspend fun loadUserData(context: Context): UserData {
        return UserData().apply {
            seed = context.userdataStore.data.map { prefs ->
                (prefs[JSON_KEY_SEED] ?: 0).also {
                    Log.i(TAG, "Loaded Seed(amount=$it)")
                }
            }.first()
        }
    }

    override suspend fun addToken(context: Context, token: ServiceToken, idx: Int) {
        context.userdataStore.edit { it[tokenKey(idx)] = token.toString() }
    }

    override suspend fun loadTokens(context: Context): List<ServiceToken> {
        return listOf()
        // TODO
//        return context.userdataStore.data.map { prefs ->
//            prefs.asMap()
//                .filterKeys { it.name.startsWith(JSON_KEY_TOKEN_PREFIX) }
//                .values
//                .mapNotNull { it as? String }
//                .map { DTO.json.decodeFromString<List<ServiceToken>>(it) }
//        }.first()
    }

    override suspend fun saveProfileData(context: Context) {
        ProfileManager.profiles.value.map { DTOProfile.from(it) }.run {
            context.profileStore.edit { it[JSON_PROFILES] = DTO.JSON.encodeToString(this) }
            Log.i(TAG, "Saved Profiles(count=${this.size})")
        }
    }

    override suspend fun loadProfileData(context: Context): List<Profile> {
        return context.profileStore.data.map { prefs ->
            (prefs[JSON_PROFILES]?.let {
                DTO.JSON.decodeFromString<List<DTOProfile>>(it)
            }?.map { it.toProfile() } ?: emptyList()).also {
                Log.i(TAG, "Loaded Profiles(count=${it.size})")
            }
        }.first()
    }

    override suspend fun saveProfileSelection(context: Context) {
        ProfileManager.mainProfileId.value.run {
            context.profileStateStore.edit {
                it[JSON_PROFILE_MAIN_ID] = this ?: ""
            }
            Log.i(TAG, "Saved Main Profile Selection (${ProfileManager.mainProfile?.title})")
        }
    }

    override suspend fun loadProfileSelection(context: Context): String {
        return context.profileStateStore.data.map { prefs ->
            (prefs[JSON_PROFILE_MAIN_ID] ?: "").also {
                Log.i(TAG, "Loaded Main Profile Selection($it)")
            }
        }.first()
    }

    override suspend fun saveFavorites(context: Context) {
        FavoriteManager.favorites.value.map { DTOProfile.from(it) }.run {
            context.favoriteStore.edit { it[JSON_FAVORITES] = DTO.JSON.encodeToString(this) }
            Log.i(TAG, "Saved Favorites(count=${this.size})")
        }
    }

    override suspend fun loadFavorites(context: Context): List<Profile> {
        return context.favoriteStore.data.map { prefs ->
            (prefs[JSON_FAVORITES]?.let {
                DTO.JSON.decodeFromString<List<DTOProfile>>(it)
            }?.map { it.toProfile() } ?: emptyList()).also {
                Log.i(TAG, "Loaded Favorites(count=${it.size})")
            }
        }.first()
    }
}