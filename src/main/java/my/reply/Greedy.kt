package my.reply

import java.util.*


fun Game.chooseBestDaemon(): Daemon? {
    return daemons.maxByOrNull { this.rate(it) }
}

val random = Random()

fun Game.rate(daemon: Daemon): Double {
    val days = this.getDaysToFight(daemon) ?: return 0.0
    if (this.remindingDays < days) return 0.0
    val reminding = this.remindingDays - days
    val stamina = daemon.calcFinalStamina(reminding)
    val score = daemon.calcFinalScore(reminding) / (days + 1)
    val staminaScore = (stamina - daemon.stamina) * this.staminaPrice / (days + daemon.turnToRecover + 1)
    val scoreR = score.toDouble() + staminaScore
    return scoreR * 0.8 + random.nextDouble() * 0.2
}
//fun Game.rate(daemon: Daemon): Double {
//    val days = this.getDaysToFight(daemon) ?: return 0.0
//    if (this.remindingDays < days) return 0.0
//    val reminding = this.remindingDays - days
//    val stamina = daemon.calcFinalStamina(reminding)
//    val score = daemon.calcFinalScore(reminding)
//    return score.toDouble()
//}

fun Game.getDaysToFight(d: Daemon): Int? {
    if (this.hero.stamina >= d.stamina) return 0
    if (this.hero.maxStamina < d.stamina) return null
    var s = this.hero.stamina
    for (e in this.staminaIncrease.entries) {
        s += e.value
        if (s >= d.stamina) {
            return e.key - this.currentDay
        }
    }
    return null
}
