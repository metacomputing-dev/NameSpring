package com.metacomputing.namespring.model.repository

import android.content.Context
import com.metacomputing.namespring.model.data.Profile
import com.metacomputing.namespring.model.data.UserData
import com.metacomputing.namespring.model.service.ServiceToken

interface UserDataSource {
    suspend fun isInitialUser(context: Context): Boolean
    suspend fun disableInitialUser(context: Context)

    suspend fun saveUserData(context: Context)
    suspend fun loadUserData(context: Context): UserData

    suspend fun addToken(context: Context, token: ServiceToken, idx: Int)
    suspend fun loadTokens(context: Context): List<ServiceToken>

    suspend fun saveProfileData(context: Context)
    suspend fun loadProfileData(context: Context): List<Profile>

    suspend fun saveProfileSelection(context: Context)
    suspend fun loadProfileSelection(context: Context): String

    suspend fun saveFavorites(context: Context)
    suspend fun loadFavorites(context: Context): List<Profile>
}