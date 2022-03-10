package my.reply

fun stat(game: Game) {
    val scores = game.daemons.map { it.prises.sum() }
    hist("score", scores)

    println("hero max stamina = ${game.hero.maxStamina}")
    println("hero init stamina = ${game.hero.stamina}")

    val staminas = game.daemons.map { it.staminaRecover - it.stamina }
    hist("delta staminas", staminas)


    val pricePerDay = game.daemons.map { (it.prises.sum() / it.prises.size) }
    hist("pricePerDay", pricePerDay)
}

private fun hist(name: String, scores: List<Int>) {
    println()
    println()
    val max = scores.maxOrNull()!!
    val min = scores.minOrNull()!!
    println("max $name = $max")
    println("min $name = $min")

    val bucket = (max - min) / 10
    repeat(11) {
        val minBucket = min + it * bucket
        val maxBucket = min + it * (bucket + 1)
        val count = scores.count { it in minBucket..maxBucket }
        println("  ${minBucket}..${maxBucket} --> $count")
    }
}