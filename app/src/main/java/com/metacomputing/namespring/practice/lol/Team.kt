package com.metacomputing.namespring.practice.lol

class Team(
    var name: String,
    var champions: ArrayList<Champion>
) {
    fun add(champion: Champion) {}
    fun checkAlive(): Boolean { return true }
}