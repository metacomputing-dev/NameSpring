package com.metacomputing.namespring.practice.lol

class Game(
    var blue: Team,
    var red: Team
) {
    fun start() {
        var blueChamp = blue.champions[0]
        var redChamp = red.champions[0]
        blueChamp.basicAttack(redChamp)
        redChamp.basicAttack(blueChamp)

        redChamp.skills[0].cast(redChamp, blueChamp)
        blueChamp.skills[0].cast(blueChamp, redChamp)
        blueChamp.basicAttack(redChamp)
        redChamp.basicAttack(blueChamp)

        redChamp.skills[0].cast(redChamp, blueChamp)
        blueChamp.skills[0].cast(blueChamp, redChamp)
        blueChamp.basicAttack(redChamp)
        redChamp.basicAttack(blueChamp)

        redChamp.skills[0].cast(redChamp, blueChamp)
        blueChamp.skills[0].cast(blueChamp, redChamp)
        blueChamp.basicAttack(redChamp)
        redChamp.basicAttack(blueChamp)

        redChamp.skills[0].cast(redChamp, blueChamp)
        blueChamp.skills[0].cast(blueChamp, redChamp)

        while (true) {
            // action
            if (red.checkAlive()) {
                print("블루승리")
            }
            if (blue.checkAlive()) {
                print("레드승리")
            }
        }
    }
}