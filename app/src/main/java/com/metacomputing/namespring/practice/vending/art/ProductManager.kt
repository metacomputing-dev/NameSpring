package com.metacomputing.namespring.practice.vending.art

class ProductManager(
    val products: ArrayList<ArrayList<Product>> = ArrayList()
) {

    class Product(
        val name: String ="",
        val price: Int,
        val count: Int =0) {

    }

    fun add(row: Int, col: Int, product: Product) {

    }

    fun subtract(row: Int, col: Int, count: Int) {

    }
}