package com.metacomputing.namespring.model.repository

import android.content.Context
import com.metacomputing.namespring.model.viewmodel.Profile
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    fun saveProfileData(context: Context): Flow<Unit>
    fun loadProfileData(context: Context): Flow<List<Profile>>
    fun saveProfileSelection(context: Context): Flow<Unit>
    fun loadProfileSelection(context: Context): Flow<String>
}