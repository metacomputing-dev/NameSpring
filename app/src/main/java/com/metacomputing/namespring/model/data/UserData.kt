package com.metacomputing.namespring.model.data

import com.metacomputing.namespring.model.service.ServiceToken

data class UserData(
    var seedData: Int,
    val tokens: ArrayList<ServiceToken> = arrayListOf()
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