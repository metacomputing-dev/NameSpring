package com.metacomputing.namespring.practice.lol

open class Champion(
    var name: String,
    var hp: Int,
    var mana: Int,
    var attack: Int,
    var skills: ArrayList<Skill> = ArrayList(),
    var inventory: Inventory = Inventory()) {

    fun add(skill: Skill) {
        skills.add(skill)
    }

    fun add(item: Item) {
        inventory.add(item)
        onItemEquipped()
    }

    fun basicAttack(target: Champion) {
        target.hp -= attack
    }

    fun isAlive() {

    }

    fun onItemEquipped() {
        inventory.items.forEach {
            attack += it.attackBonus
        }
    }
}