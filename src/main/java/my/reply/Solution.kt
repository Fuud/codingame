package my.reply

import java.io.File

fun solve(game: Game) {
    listOf(1,3,2,4,0).map {idx -> game.daemons.single { it.id ==idx } }.forEach {
        game.fight(it)
    }
    println(game.currentScore)
    println(game)
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