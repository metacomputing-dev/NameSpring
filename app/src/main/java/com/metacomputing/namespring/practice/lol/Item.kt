package com.metacomputing.namespring.practice.lol

class Item(
    var name: String,
    var attackBonus: Int
    ) {
    fun apply(champion: Champion) {
        champion.attack += attackBonus
    } // WHAT?
}