import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Integer.max
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main() {
    try{
        performGame()
    }catch (t: Throwable){
        t.printStackTrace()
    }
}

fun performGame() {
    val input = Scanner(TeeInputStream(System.`in`, System.err))
    val playerIdx = input.nextInt()
    val nbGames = input.nextInt()
    if (input.hasNextLine()) {
        input.nextLine()
    }

    data class GameScore(val gold: Int, val silver: Int, val bronze: Int)
    data class PlayerScore(val sum: Int, val games: List<GameScore>)

    // game loop
    while (true) {
        var playerScores = (0 until 3).map {
            PlayerScore(
                input.nextInt(),
                (1..4).map {
                    GameScore(
                        input.nextInt(),
                        input.nextInt(),
                        input.nextInt(),
                    )
                }
            )
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
                        playerIdx, i, listOf(
                            HurdleRacePlayer(0, reg0, reg3),
                            HurdleRacePlayer(1, reg1, reg4),
                            HurdleRacePlayer(2, reg2, reg5)
                        ), gpu
                    )
                    System.err.println("#$hurdleRace")
                    miniGames.add(hurdleRace)
                }

                1 -> {
                    val archery =
                        Archery(
                            playerIdx, i,
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
                        playerIdx, i, listOf(
                            RollerPlayer(0, reg0, reg3),
                            RollerPlayer(1, reg1, reg4),
                            RollerPlayer(2, reg2, reg5)
                        ),
                        gpu,
                        reg6
                    )
                    System.err.println("#$game")
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
                    System.err.println("#$game")
                    miniGames.add(game)
                }
            }
        }
        input.nextLine()

        var (ourScores, gameScores) = playerScores[playerIdx]
        val bestGame = gameScores.indexOf(gameScores.minBy { it.gold * 3 + it.silver })
        System.err.println("# will play game $bestGame")
        println(miniGames[bestGame].next())
        miniGames.forEach{ it.next() }
    }
}

data class HurdleRacePlayer(val id: Int, val position: Int, val stunned: Int)
data class ArcheryPlayer(val id: Int, val x: Int, val y: Int)
data class RollerPlayer(val id: Int, val distance: Int, val risk: Int)
data class DiverPlayer(val id: Int, val points: Int, val combo: Int)

interface MiniGame {
    fun next(): Direction
}

data class HurdleRace(val playerIdx: Int, val id: Int, val hurdleRacePlayers: List<HurdleRacePlayer>, val field: String) : MiniGame {
    fun nextHurdle(pos: Int): Int = field.indexOf('#', pos + 1)
    override fun next(): Direction {
        val player = hurdleRacePlayers[playerIdx]
        val ourPos = hurdleRacePlayers[playerIdx].position
        val index = nextHurdle(ourPos)
        val delta = index - ourPos
        System.err.println("#$delta:$player")
        return when (delta) {
            1 -> Direction.UP
            2 -> Direction.LEFT
            3 -> Direction.DOWN
            else -> Direction.RIGHT
        }
    }

}

data class Archery(val playerIdx:Int, val id: Int, val players: List<ArcheryPlayer>, val field: String) : MiniGame {

    data class Point private constructor(val x: Int, val y: Int, val ignored: Int) {
        constructor(x: Int, y: Int) : this(max(-20, min(20, x)), max(-20, min(20, y)), 0)
    }

    class ArcheryField {
        val array: MutableMap<Point, Double> = mutableMapOf();
    }

    override fun next(): Direction {
        val steps = field.length
        var prevField: ArcheryField? = null;
        val array: Array<ArcheryField> = Array(steps + 1) { step ->
            ArcheryField().apply {
                val map = array
                if (prevField == null) {
                    for (s in 0..steps) {
                        for (x in -20..20) {
                            for (y in -20..20) {
                                map[Point(x, y)] = sqrt(x * x.toDouble() + y * y)
                            }
                        }
                    }
                } else {
                    val prevMap = prevField!!.array
                    for (x in -20..20) {
                        for (y in -20..20) {
                            val strength: Int = field[steps - step] - '0'
                            val r = prevMap[Point(x + strength, y)]!!;
                            val l = prevMap[Point(x - strength, y)]!!;
                            val d = prevMap[Point(x, y + strength)]!!;
                            val u = prevMap[Point(x, y - strength)]!!;

                            val m = min(min(r, l), min(d, u))
                            map[Point(x, y)] = m
                        }
                    }
                }
                prevField = this;
            }
        }
        val player = players[playerIdx]
        var best = array.last().array[Point(player.x, player.y)]
        val strength: Int = field[0] - '0'
        val prevMap = array[array.size - 2].array
        val r = prevMap[Point(player.x + strength, player.y)]!!;
        val l = prevMap[Point(player.x - strength, player.y)]!!;
        val d = prevMap[Point(player.x, player.y + strength)]!!;
        val u = prevMap[Point(player.x, player.y - strength)]!!;

        return when (best) {
            r -> Direction.RIGHT;
            l -> Direction.LEFT;
            u -> Direction.UP;
            d -> Direction.DOWN;
            else -> throw IllegalStateException("aaaa");
        }

    }
}

data class Roller(val playerIdx: Int, val id: Int, val players: List<RollerPlayer>, val field: String, val leftSteps: Int) : MiniGame {
    override fun next(): Direction {
        val player = players[playerIdx]
        val riskDirection = if (player.risk >= 4 - ThreadLocalRandom.current().nextInt(6)/5) {
            0
        } else if (player.risk == 3) {
            1
        } else if (player.risk == 2) {
            3
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

