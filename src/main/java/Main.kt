import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main() {
    performGame()
}

fun performGame() {
    val input = Scanner(System.`in`)
    val playerIdx = input.nextInt()
    val nbGames = input.nextInt()
    if (input.hasNextLine()) {
        input.nextLine()
    }

    // game loop
    while (true) {
        for (i in 0 until 3) {
            val scoreInfo = input.nextLine()
        }

        val boards = mutableListOf<Board>()
        for (i in 0 until nbGames) {
            val gpu = input.next()
            val reg0 = input.nextInt()
            val reg1 = input.nextInt()
            val reg2 = input.nextInt()
            val reg3 = input.nextInt()
            val reg4 = input.nextInt()
            val reg5 = input.nextInt()
            val reg6 = input.nextInt()
            val board = Board(i, listOf(Player(0, reg0, reg3), Player(1, reg1, reg4), Player(2, reg2, reg5)), gpu)
            System.err.println(board)
            boards.add(board)
        }
        input.nextLine()
        val delta = boards.map { board ->
            val player = board.players[0]!!
            val ourPos = board.players[0]!!.position
            val index = board.nextHurdle(ourPos)
            val delta = index - ourPos
            System.err.println("$delta:$player")
            return@map player to delta
        }.filter { it.second > 0 && it.first.stunned == 0 }
            .map { it.second }
            .min() ?: 0

        if (delta == 1) {
            println("UP")
        } else if (delta == 2) {
            println("LEFT")
        } else if (delta == 3) {
            println("DOWN")
        } else {
            println("RIGHT")
        }
    }
}

data class Player(val id: Int, val position: Int, val stunned: Int) {

}

interface MiniGame {
    fun next(): Direction
}

data class Board(val id: Int, val players: List<Player>, val field: String) : MiniGame {
    fun nextHurdle(pos: Int): Int = field.indexOf('#', pos + 1)
    override fun next() =
        TODO("Not yet implemented")

}

enum class Direction { LEFT, RIGHT, UP, DOWN }
enum class Medal { GOLD, SILVER, BRONZE }
data class MedalExpectations(val probabilities: Map<Medal, Double>)
