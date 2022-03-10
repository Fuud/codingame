package my.reply

import java.io.File

fun solve(game: Game) {
    while (game.currentDay < game.maxTurns && game.daemons.isNotEmpty()){
        game.fight(game.daemons.first())
    }
}

fun output(game: Game, file: String){
    File("$file.${game.currentScore}.txt").writer().use { writer ->
        game.defeatedDaemons.forEach {
            writer.appendLine("$it")
        }
        game.daemons.forEach {
            writer.appendLine("${it.id}")
        }
    }
}