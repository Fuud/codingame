import Owner.ENEMY
import Owner.ME
import Owner.NEUTRAL
import Tune.spawnPart
import java.util.*
import kotlin.math.abs

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args: Array<String>) {
    try {
        var turn = 0
        val input = Scanner(System.`in`)
        val width = input.nextInt()
        val height = input.nextInt()

        var builds = 0
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
                            else -> NEUTRAL
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
            val shouldBuild = builds < Tune.maxBuildCount && turn > Tune.buildsFromTurn

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
                .sortedByDescending { it.first.point.distanceTo(it.second.point) }

            System.err.println(bestTargets)

            val researchersPart = bestTargets.size / Tune.researchersPart
            val (attackers, researchers) = if (researchersPart > 0) {
                val attackers = bestTargets.toMutableList()
                val researchers = bestTargets.subList(0, researchersPart).map { researcher ->
                    val point = board.nearResearchCell(researcher.first.point)
                    if (point != null) {
                        attackers.remove(researcher)
                        researcher.first to point
                    } else {
                        researcher.first to null
                    }
                }.filter {
                    it.second != null
                }
                attackers to researchers
            } else {
                bestTargets to emptyList()
            }

            val attackMoves = attackers.map {
                val ourAmount = it.first.amount
                val enemyAmount = it.second.amount
                if (it.first.point.distanceTo(it.second.point) == 1 && ourAmount > enemyAmount) {
                    MOVE(ourAmount - enemyAmount, it.first.point, it.second.point).toString()
                } else {
                    MOVE(ourAmount, it.first.point, it.second.point).toString()
                }
            }

            val researchMoves = researchers.map { MOVE(it.first.amount, it.first.point, it.second!!).toString() }

            val spawn = mutableListOf<SPAWN>()
            val closeAttackers = attackers.filter { it.first.point.distanceTo(it.second.point) == 1 }.toMutableList()
            val researchCells = cells
                .filter { it.canSpawn && board.nearResearchCell(it.point) != null }
                .toList().toMutableList()
            val buildCell = cells
                .filter { it.canBuild && board.nearEnemyCell(it.point) != null }
                .toList().toMutableList()

            closeAttackers.shuffle()
            researchCells.shuffle()
            buildCell.shuffle()

            var matterCounter = 0
            val size = minOf(myMatter / spawnPart, closeAttackers.size)
            var i = 0
            while (matterCounter in 0..size) {
                if (closeAttackers.isNotEmpty()) {
                    val point = closeAttackers[i % closeAttackers.size].first.point
                    spawn.add(SPAWN(1, point))
                    matterCounter = matterCounter + 10
                    i++
                }else {
                    break;
                }
            }

            i = 0
            while (matterCounter in 0..myMatter) {
                if (researchCells.isNotEmpty()) {
                    val point = researchCells[i % researchCells.size].point
                    spawn.add(SPAWN(1, point))
                    matterCounter = matterCounter + 10
                    i++
                }else {
                    break;
                }
            }

            i = 0
            val build = if (shouldBuild && i < buildCell.size) {
                builds++
                BUILD(point = buildCell[i].point)
            } else {
                null
            }


            val command = (attackMoves + researchMoves + spawn + build).filterNotNull().joinToString(separator = ";")
            if (command.isEmpty()) {
                println("WAIT;")
            } else {
                println(command)
            }
            turn++
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

data class Board(val cells: Map<Point, Cell>) {
    fun nearResearchCell(point: Point): Point? {
        val neighbors = listOf(-1 to 0, 1 to 0, 0 to 1, 0 to -1)
            .map { Point(point.x + it.first, point.y + it.second) }
        return neighbors.firstOrNull { cells[it]?.owner == ENEMY && ((cells[it]?.scrapAmount ?: 0) > 0) }
            ?: neighbors.firstOrNull { cells[it]?.owner == NEUTRAL && ((cells[it]?.scrapAmount ?: 0) > 0) }
    }

    fun nearEnemyCell(point: Point): Point? {
        val neighbors = listOf(-1 to 0, 1 to 0, 0 to 1, 0 to -1)
            .map { Point(point.x + it.first, point.y + it.second) }
        return neighbors.firstOrNull {
            cells[it]?.owner == ENEMY
                    && ((cells[it]?.scrapAmount ?: 0) > 0) && (cells[it]?.units ?: 0) > 0
        }
    }
}

data class Cell(
    val point: Point,
    val scrapAmount: Int,
    val owner: Owner,
    val units: Int,
    val recycler: Boolean,
    val canBuild: Boolean,
    val canSpawn: Boolean,
    val inRangeOfRecycler: Boolean,
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

object Tune {
    const val maxBuildCount: Int = 10
    const val buildsFromTurn: Int = 5
    const val researchersPart: Int = 1
    const val spawnPart: Int = 3
}