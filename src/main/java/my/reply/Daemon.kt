package my.reply

fun accumulatedPrices(prises: List<Int>): List<Int> {
    val acc = prises.toMutableList()
    prises.forEachIndexed { i, _ -> acc[i] += if (i == 0) 0 else acc[i - 1] }
    return acc
}

data class Daemon(
    val id: Int,
    val stamina: Int,
    val turnToRecover: Int,
    val staminaRecover: Int,
    val prises: List<Int>,
    val accumulatedPrices: List<Int> = accumulatedPrices(prises),
) {

    fun calcFinalStamina(remindingDays: Int): Int {
        return if (turnToRecover > remindingDays) 0
        else staminaRecover
    }

    fun calcFinalScore(remindingDays: Int): Int {
        return if (remindingDays < accumulatedPrices.size) accumulatedPrices[remindingDays]
        else accumulatedPrices.lastOrNull() ?: 0
    }
}
