package com.metacomputing.namespring.model.token

import kotlinx.serialization.Serializable

@Serializable
sealed class ServiceToken {
    abstract val id: String
    abstract val seedCost: Int
    abstract val timestamp: Long
    abstract val history: String
    abstract fun action(): Unit
    abstract fun available(): Boolean
}