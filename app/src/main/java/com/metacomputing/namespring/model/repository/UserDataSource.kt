package com.metacomputing.namespring.model.repository

import android.content.Context
import com.metacomputing.namespring.model.viewmodel.Profile

interface UserDataSource {
    suspend fun saveProfileData(context: Context)
    suspend fun loadProfileData(context: Context): List<Profile>
    suspend fun saveProfileSelection(context: Context)
    suspend fun loadProfileSelection(context: Context): String
}