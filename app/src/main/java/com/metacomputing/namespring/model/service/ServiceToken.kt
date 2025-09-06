package com.metacomputing.namespring.model.service

import kotlinx.serialization.Serializable

@Serializable
sealed class ServiceToken {
    abstract val seedCost: Int
    abstract val timestamp: Long
    abstract fun getHistory(): String
    abstract fun action(): Unit
    abstract fun available(): Boolean
}