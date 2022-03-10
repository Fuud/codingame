package my.reply

fun Game.chooseBestDaemon(): Daemon? {
    return daemons.maxByOrNull { this.rate(it) }
}


fun Game.rate(daemon: Daemon): Double {
    val days = this.getDaysToFight(daemon) ?: return 0.0
    if (this.remindingDays < days) return 0.0
    val reminding = this.remindingDays - days
    val stamina = daemon.calcFinalStamina(reminding)
    val score = daemon.calcFinalScore(reminding)
    return score.toDouble()
}

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
