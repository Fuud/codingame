package my.reply

import java.lang.Integer.min
import java.util.*

data class Game(
    val maxTurns: Int,
    val hero: Hero,
    val daemons: MutableList<Daemon>,
    val defeatedDaemons: MutableList<Int> = mutableListOf(),
    var currentDay: Int = 0,
    var currentScore: Int = 0,
    val staminaIncrease: TreeMap<Int, Int> = TreeMap()
) {

    val remindingDays: Int
        get() = maxTurns - currentDay - 1
}

fun Game.fight(daemon: Daemon) {
    if (staminaIncrease[currentDay] != null) {
        hero.stamina = min(hero.maxStamina, hero.stamina + staminaIncrease[currentDay]!!)
        staminaIncrease.remove(currentDay)
    }
    while (hero.stamina < daemon.stamina && currentDay < maxTurns) {
        if (staminaIncrease.isEmpty()) {
            currentDay = maxTurns
            return
        }
        val next = staminaIncrease.firstEntry()
        currentDay = next.key
        hero.stamina = min(hero.maxStamina, hero.stamina + next.value)
        staminaIncrease.remove(next.key)
    }
    if (currentDay < maxTurns) {
        staminaIncrease.compute(currentDay + daemon.turnToRecover) { key, value ->
            (value ?: 0) + daemon.staminaRecover
        }
        hero.stamina -= daemon.stamina
        val addScore = daemon.prises.subList(0, min(maxTurns - currentDay, daemon.prises.size)).sum()
        currentScore += addScore
        daemons.remove(daemon)
        defeatedDaemons.add(daemon.id)
        currentDay++
    }
}
