package com.metacomputing.namespring.model.token

import com.metacomputing.namespring.model.data.Profile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("charge_token")
class ComponentDetailToken(
    override val id: String, // TODO to make simple in dev phase.
    override val history: String
) : ServiceToken() {
    override val seedCost: Int = 50
    override val timestamp: Long = System.currentTimeMillis()

    override fun action() {
        TODO("Not yet implemented")
    }

    override fun available(): Boolean {
        TODO("Not yet implemented")
    }
}