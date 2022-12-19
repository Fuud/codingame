import Owner.ENEMY
import Owner.ME
import java.util.*
import kotlin.math.abs

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args: Array<String>) {
    try {
        val input = Scanner(System.`in`)
        val width = input.nextInt()
        val height = input.nextInt()

        var countDownRecycles = 1
        // game loop
        while (true) {
            val myMatter = input.nextInt()
            val oppMatter = input.nextInt()

            val cells = (0 until height).flatMap { i ->
                (0 until width).map { j ->
                    val scrapAmount = input.nextInt()
                    val owner = input.nextInt() // 1 = me, 0 = foe, -1 = neutral
                    val units =
                        input.nextInt() // the number of units on this cell. These units belong to the owner of the cell.
                    val recycler = input.nextInt()
                    val canBuild = input.nextInt()
                    val canSpawn = input.nextInt()
                    val inRangeOfRecycler = input.nextInt()

                    Cell(
                        point = Point(j, i),
                        scrapAmount = scrapAmount,
                        owner = when (owner) {
                            1 -> ME
                            0 -> ENEMY
                            else -> Owner.NEUTRAL
                        },
                        units = units,
                        recycler = recycler == 1,
                        canBuild = canBuild == 1,
                        canSpawn = canSpawn == 1,
                        inRangeOfRecycler = inRangeOfRecycler == 1,
                    )
                }
            }

            val us = Army(
                matter = myMatter,
                tanks = cells.filter { it.owner == ME && it.units > 0 }.map { Tank(it.units, it.point) },
                recycles = cells.filter { it.owner == ME && it.recycler }.map { Recycle(it.point) },
            )
            val enemy = Army(
                matter = myMatter,
                tanks = cells.filter { it.owner == ENEMY && it.units > 0 }.map { Tank(it.units, it.point) },
                recycles = cells.filter { it.owner == ENEMY && it.recycler }.map { Recycle(it.point) },
            )
            val shouldBuild = countDownRecycles !=0

            val board = Board(
                cells.associateBy { it.point }
            )

            System.err.println(us.tanks.size)
            System.err.println(enemy.tanks.size)

            // Write an action using println()
            // To debug: System.err.println("Debug messages...");

            val bestTargets = us.tanks
                .map { ourTank -> ourTank to enemy.tanks.minByOrNull { enemyTank -> enemyTank.point.distanceTo(ourTank.point) } }
                .filter { it.second != null }
                .map { it.first to it.second!! }

            System.err.println(bestTargets);

            val build = if (shouldBuild) {
                countDownRecycles--
                BUILD(point = cells.filter { it.owner == ME && it.units == 0 }
                    .maxByOrNull { it.scrapAmount }!!.point)
            } else {
               null
            }
            val move = bestTargets.map {
                val ourAmount = it.first.amount
                val enemyAmount = it.second.amount
                if (it.first.point.distanceTo(it.second.point) == 1 && ourAmount > enemyAmount){
                    MOVE(ourAmount - enemyAmount, it.first.point, it.second.point).toString()
                }else {
                    MOVE(ourAmount, it.first.point, it.second.point).toString()
                }
            }
            val strongTank = us.tanks.maxByOrNull { it.amount }!!
            val spawn = SPAWN(1, strongTank.point)
            println((move + spawn + build).filterNotNull().joinToString(separator = ";"))
        }
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

data class Army(val matter: Int, val tanks: List<Tank>, val recycles: List<Recycle>)

data class Tank(val amount: Int, val point: Point)

data class Point(val x: Int, val y: Int) {
    fun distanceTo(point: Point): Int {
        return abs(x - point.x) + abs(y - point.y)
    }

    override fun toString(): String {
        return "$x $y"
    }
}

data class Recycle(val point: Point)

data class Board(val cells: Map<Point, Cell>)

data class Cell(
    val point: Point,
    val scrapAmount: Int,
    val owner: Owner,
    val units: Int,
    val recycler: Boolean,
    val canBuild: Boolean,
    val canSpawn: Boolean,
    val inRangeOfRecycler: Boolean
)

enum class Owner {
    ME,
    ENEMY,
    NEUTRAL
}

data class MOVE(val amount: Int, val from: Point, val to: Point) {
    override fun toString(): String = "MOVE $amount $from $to"
}

data class BUILD(val point: Point) {
    override fun toString(): String = "BUILD $point"
}

data class SPAWN(val number: Int, val point: Point) {
    override fun toString(): String = "SPAWN $number $point"
}