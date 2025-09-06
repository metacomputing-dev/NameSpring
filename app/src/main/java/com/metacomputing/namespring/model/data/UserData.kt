package com.metacomputing.namespring.model.data

import com.metacomputing.namespring.model.service.ServiceToken
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow

data class UserData(
    var seedData: Int,
    val tokens: PersistentList<ServiceToken> = persistentListOf()
) {
    companion object {
        val INITIAL = UserData(1000)
    }

    var seed: Int
        get() = seedData
        set(value) {
            seedData = value
        }
}