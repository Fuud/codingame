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

        val miniGames = mutableListOf<MiniGame>()
        for (i in 0 until nbGames) {
            val gpu = input.next()
            val reg0 = input.nextInt()
            val reg1 = input.nextInt()
            val reg2 = input.nextInt()
            val reg3 = input.nextInt()
            val reg4 = input.nextInt()
            val reg5 = input.nextInt()
            val reg6 = input.nextInt()
            when (i) {
                0 -> {
                    val hurdleRace = HurdleRace(
                        i, listOf(
                            HurdleRacePlayer(0, reg0, reg3),
                            HurdleRacePlayer(1, reg1, reg4),
                            HurdleRacePlayer(2, reg2, reg5)
                        ), gpu
                    )
                    System.err.println(hurdleRace)
                    miniGames.add(hurdleRace)
                }

                1 -> {
                    val archery =
                        Archery(
                            i,
                            listOf(
                                ArcheryPlayer(0, reg0, reg1),
                                ArcheryPlayer(1, reg2, reg3),
                                ArcheryPlayer(2, reg4, reg5)
                            ),
                            gpu
                        )
                    miniGames.add(archery)
                }

                2 -> {
                    val game = Roller(
                        i, listOf(
                            RollerPlayer(0, reg0, reg3),
                            RollerPlayer(1, reg1, reg4),
                            RollerPlayer(2, reg2, reg5)
                        ),
                        gpu,
                        reg6
                    )
                    System.err.println(game)
                    miniGames.add(game)
                }

                else -> {
                    val game = Diving(
                        i, listOf(
                            DiverPlayer(0, reg0, reg3),
                            DiverPlayer(1, reg1, reg4),
                            DiverPlayer(2, reg2, reg5)
                        ), gpu
                    )
                    System.err.println(game)
                    miniGames.add(game)
                }
            }
        }
        input.nextLine()
        val delta = miniGames.filterIsInstance<HurdleRace>(). map { board ->
            val player = board.hurdleRacePlayers[0]!!
            val ourPos = board.hurdleRacePlayers[0]!!.position
                val index = board.nextHurdle(ourPos)
                val delta = index - ourPos
                System.err.println("$delta:$player")
                return@map player to delta
        }.filter { it.second > 0 && it.first.stunned == 0 }
            .map { it.second }
            .minOrNull() ?: 0

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

data class HurdleRacePlayer(val id: Int, val position: Int, val stunned: Int)
data class ArcheryPlayer(val id: Int, val x: Int, val y: Int)
data class RollerPlayer(val id: Int, val distance: Int, val risk: Int)
data class DiverPlayer(val id: Int, val points: Int, val combo: Int)

interface MiniGame {
    fun next(): Direction
}

data class HurdleRace(val id: Int, val hurdleRacePlayers: List<HurdleRacePlayer>, val field: String) : MiniGame {
    fun nextHurdle(pos: Int): Int = field.indexOf('#', pos + 1)
    override fun next() =
        TODO("Not yet implemented")

}

data class Archery(val id: Int, val players: List<ArcheryPlayer>, val field: String) : MiniGame {
    override fun next() =
        TODO("Not yet implemented")
}

data class Roller(val id: Int, val players: List<RollerPlayer>, val field: String, val leftSteps: Int) : MiniGame {
    override fun next() =
        TODO("Not yet implemented")
}

data class Diving(val id: Int, val players: List<DiverPlayer>, val field: String) : MiniGame {
    override fun next() =
        TODO("Not yet implemented")
}

enum class Direction { LEFT, RIGHT, UP, DOWN }
enum class Medal { GOLD, SILVER, BRONZE }
data class MedalExpectations(val probabilities: Map<Medal, Double>)
