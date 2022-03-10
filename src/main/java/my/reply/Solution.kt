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

fun output(game: Game, problemName: String) {
    val outputDirectory = "out"
    File(outputDirectory).mkdirs()
    val now = Instant.now().atZone(ZoneId.of("Europe/Moscow"))
    val problemIndex = problemName.substring(0, 2)
    val currentScore = game.currentScore.toString().padStart(6, '0')
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
    println()
}
