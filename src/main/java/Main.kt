import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main() {
    performGame()
}

fun performGame() {
    val input = Scanner(TeeInputStream(System.`in`, System.err))
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
//        println(miniGames.filterIsInstance<HurdleRace>().single().next())
//        println(miniGames.filterIsInstance<Archery>().single().next())
        println(miniGames.filterIsInstance<Roller>().single().next())
//        println(miniGames.filterIsInstance<Diving>().single().next())

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
    override fun next(): Direction {
        val player = hurdleRacePlayers[0]
        val ourPos = hurdleRacePlayers[0].position
        val index = nextHurdle(ourPos)
        val delta = index - ourPos
        System.err.println("$delta:$player")
        return when (delta) {
            1 -> Direction.UP
            2 -> Direction.LEFT
            3 -> Direction.DOWN
            else -> Direction.RIGHT
        }
    }

}

data class Archery(val id: Int, val players: List<ArcheryPlayer>, val field: String) : MiniGame {
    override fun next(): Direction {
        TODO("Not yet implemented")


    fun next(player:ArcheryPlayer) :Direction {
        val steps = field.length
        val array: Array<Array<Array<Double>>> = Array(steps) { _ -> Array(41) { _ -> Array(41) { _ -> 0.0 } } }
        for (s in 0..steps) {
            for (x in -20..20) {
                for (y in -20..20) {
                    if (s == 0) {
                        array[steps - s][x - 20][y - 20] = sqrt(x * x.toDouble() + y * y)
                    } else {
                        val strength: Int = field[s] - '0'
                        array[steps -s][x - 20][y - 20] = min()
                    }
                }
            }
        }
    }
    }
}

data class Roller(val id: Int, val players: List<RollerPlayer>, val field: String, val leftSteps: Int) : MiniGame {
    override fun next(): Direction {
        val player = players[0]
        val riskDirection = if (player.risk >= 4) {
            0
        } else if (player.risk == 3) {
            1
        } else if (player.risk == 2) {
            2
        } else /*if (player.risk == 1)*/ {
            3
        }
        return when (field[riskDirection]) {
            'U' -> Direction.UP
            'D' -> Direction.DOWN
            'L' -> Direction.LEFT
            else -> Direction.RIGHT
        }
    }
}

data class Diving(val id: Int, val players: List<DiverPlayer>, val field: String) : MiniGame {
    override fun next(): Direction{
        return when (field.first()) {
            'U' -> Direction.UP
            'D' -> Direction.DOWN
            'L' -> Direction.LEFT
            else -> Direction.RIGHT
        }
    }
}

enum class Direction { LEFT, RIGHT, UP, DOWN }
enum class Medal { GOLD, SILVER, BRONZE }
data class MedalExpectations(val probabilities: Map<Medal, Double>)

class TeeInputStream(private var source: InputStream, private var copySink: OutputStream) : InputStream() {
    @Throws(IOException::class)
    override fun read(): Int {
        val result = source.read()
        if (result >= 0) {
            copySink.write(result)
        }
        return result
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return source.available()
    }

    @Throws(IOException::class)
    override fun close() {
        source.close()
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        source.mark(readlimit)
    }

    override fun markSupported(): Boolean {
        return source.markSupported()
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val result = source.read(b, off, len)
        if (result >= 0) {
            copySink.write(b, off, result)
        }
        return result
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        val result = source.read(b)
        if (result >= 0) {
            copySink.write(b, 0, result)
        }
        return result
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        source.reset()
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        return source.skip(n)
    }
}

