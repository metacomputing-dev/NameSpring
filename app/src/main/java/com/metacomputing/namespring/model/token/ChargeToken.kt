package com.metacomputing.namespring.model.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("charge_token")
class ChargeToken(
    override val seedCost: Int,
    override val timestamp: Long
) : ServiceToken() {
    override val id: String = "ChargeToken_cost=${seedCost}_stamp=$timestamp"
    override val history: String
        get() = TODO("Not yet implemented")

    override fun action() {
        TODO("Not yet implemented")
    }

    override fun available(): Boolean {
        TODO("Not yet implemented")
    }
}