package my.reply

import java.io.File
import java.time.Instant
import java.time.ZoneId

fun Game.solve() {
    while (currentDay < maxTurns && daemons.isNotEmpty()) {
        val daemon = chooseBestDaemon() ?: return
        fight(daemon)
    }
}

val scores = mutableMapOf<String, Int>()

fun output(game: Game, problemName: String) {
    val outputDirectory = "out"
    File(outputDirectory).mkdirs()
    val now = Instant.now().atZone(ZoneId.of("Europe/Moscow"))
    val problemIndex = problemName.substring(0, 2)
    val currentScore = game.currentScore.toString().padStart(7, '0')
    val skip = (scores[problemName] ?: 0) >= game.currentScore
    if (skip) {
        println(problemIndex)
        return
    }
    scores[problemName] = game.currentScore
    val hour = now.hour.toString().padStart(2, '0')
    val minute = now.minute.toString().padStart(2, '0')
    val outputFileName = "$problemIndex-$currentScore-$hour-$minute.txt"
    println(outputFileName)
    File("$outputDirectory/$outputFileName").writer().use { writer ->
        game.defeatedDaemons.forEach {
            writer.appendLine("$it")
        }
        game.daemons.forEach {
            writer.appendLine("${it.id}")
        }
    }
}
