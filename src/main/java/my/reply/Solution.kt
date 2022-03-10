package my.reply

import java.io.File

fun Game.solve() {
    while (currentDay < maxTurns && daemons.isNotEmpty()) {
        val daemon = chooseBestDaemon() ?: return
        fight(daemon)
    }
}

fun output(game: Game, file: String) {
    File("out").mkdirs()
    File("out/$file.${game.currentScore}.txt").writer().use { writer ->
        game.defeatedDaemons.forEach {
            writer.appendLine("$it")
        }
        game.daemons.forEach {
            writer.appendLine("${it.id}")
        }
    }
}
