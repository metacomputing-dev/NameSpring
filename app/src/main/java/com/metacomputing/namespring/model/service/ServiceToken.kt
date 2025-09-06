package com.metacomputing.namespring.model.service

interface ServiceToken {
    val timeStamp: Long
    val seedCost: Int
    fun getHistory(): String
    fun action(): Unit
    fun available(): Boolean
}