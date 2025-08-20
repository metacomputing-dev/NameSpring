package com.metacomputing.namespring.practice.vending

class Product(
    var quantity: Int,
    val price: Int,
    val name: String,
    val id: Int) {
    fun display() {}
    fun isAvail(): Boolean { return true }
    fun decrease() {}
    fun increase() {}
}