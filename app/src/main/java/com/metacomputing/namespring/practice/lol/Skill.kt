package com.metacomputing.namespring.practice.lol

class Skill(
    var name: String,
    var damage: Int,
    var cost: Int
) {
    fun cast(caster: Champion, target: Champion) {}
}