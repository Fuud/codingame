package my.reply

fun solve(game: Game) {
    listOf(1,3,2,4,0).map {idx -> game.daemons.single { it.id ==idx } }.forEach {
        game.fight(it)
    }
    println(game.currentScore)
    println(game)
}