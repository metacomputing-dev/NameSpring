package com.metacomputing.namespring.control

import androidx.lifecycle.MutableLiveData
import com.metacomputing.namespring.model.data.UserData
import com.metacomputing.namespring.model.service.ChargeToken
import com.metacomputing.namespring.model.service.ServiceToken

object UserDataManager {
    val userData = MutableLiveData<UserData>()
    var isLoaded = false

    // TODO put all of data managers here in future, profile and favorites

    fun loadInitialUser() {
        load(UserData.INITIAL)
    }

    fun load(data: UserData) {
        userData.value = data
        isLoaded = true
    }

    fun chargeSeed(token: ChargeToken) {
        if (!isLoaded) throw RuntimeException("UserData not loaded yet")
        userData.update {
            userData.value?.seed += token.seedCost
            userData.value?.tokens?.add(token)
        }
    }

    fun spendSeed(token: ServiceToken): Boolean {
        if (!isLoaded) throw RuntimeException("UserData not loaded yet")
        userData.update {
            if (token.seedCost <= it.seed) {
                it.seed -= token.seedCost
                it.tokens.add(token)
            }
        }
        return true
    }

    private fun MutableLiveData<UserData>.update(update: (userData: UserData) -> Unit): Boolean {
        if (value != null) {
            update.invoke(value!!)
            userData.value = userData.value?.copy()
            return true
        }
        return false
    }
}