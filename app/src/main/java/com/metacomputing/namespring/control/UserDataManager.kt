package com.metacomputing.namespring.control

import androidx.lifecycle.MutableLiveData
import com.metacomputing.namespring.model.data.UserData
import com.metacomputing.namespring.model.token.ChargeToken
import com.metacomputing.namespring.model.token.ServiceToken

object UserDataManager {
    val userData = MutableLiveData<UserData>()
    var isLoaded = false
    var data: UserData
        get() = userData.value ?: throw RuntimeException("Empty UserData")
        private set(value) {
            userData.value = value
        }

    // TODO put all of data managers here in future, profile and favorites

    fun load(data: UserData) {
        this.data = data
        isLoaded = true
    }

    fun chargeSeed(token: ChargeToken) {
        if (!isLoaded) throw RuntimeException("UserData not loaded yet")
        userData.update {
            data.seed += token.seedCost
            data.tokens.add(token)
        }
    }

    fun spendSeed(token: ServiceToken): Boolean {
        if (!isLoaded) throw RuntimeException("UserData not loaded yet")
        userData.update {
            if (it.seed >= token.seedCost) {
                it.seed -= token.seedCost
                it.tokens.add(token)
            }
        }
        return true
    }

    fun hasPurchased(id: String): Boolean {
        return data.tokens.any { it.id == id }
    }

    private fun MutableLiveData<UserData>.update(process: (userData: UserData) -> Unit) {
        process.invoke(data)
        data = data.copy()
    }
}