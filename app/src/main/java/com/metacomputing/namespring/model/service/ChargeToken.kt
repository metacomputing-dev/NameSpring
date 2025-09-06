package com.metacomputing.namespring.model.service

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("charge_token")
class ChargeToken(
    override val seedCost: Int,
    override val timestamp: Long
) : ServiceToken() {

    override fun getHistory(): String {
        TODO("Not yet implemented")
    }

    override fun action() {
        TODO("Not yet implemented")
    }

    override fun available(): Boolean {
        TODO("Not yet implemented")
    }
}