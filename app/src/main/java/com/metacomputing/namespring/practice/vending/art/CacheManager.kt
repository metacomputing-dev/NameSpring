package com.metacomputing.namespring.practice.vending.art

class CacheManager(
    var currentCache: Int
) {
    fun add(cache: Int) {

    }

    fun subtract(cache: Int) {

    }

    fun returnChange(): Int { return currentCache.also { currentCache = 0} }
}