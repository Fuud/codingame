package my.reply

import java.io.File

fun Game.solve() {
    while (currentDay < maxTurns && daemons.isNotEmpty()) {
        val daemon = chooseBestDaemon() ?: return
        fight(daemon)
    }
}

fun output(game: Game, problemName: String) {
    val outputDirectory = "out"
    File(outputDirectory).mkdirs()
    val problemIndex = problemName.substring(0, 2)
    val currentScore = game.currentScore
    println("$problemIndex $currentScore")
    File("$outputDirectory/$problemIndex-$currentScore.txt").writer().use { writer ->
        game.defeatedDaemons.forEach {
            writer.appendLine("$it")
        }
        game.daemons.forEach {
            writer.appendLine("${it.id}")
        }
    }
    println()
}
