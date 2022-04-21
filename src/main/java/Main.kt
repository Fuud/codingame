import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

data class Point(val x: Int, val y: Int)
data class Hero(val point: Point, val id: Int, val health: Int)
data class Base(val point: Point)
data class Spider(val point: Point, val health: Int, val vx: Int, val vy: Int, val nearBase: Int, val threatFor: Int) {
    var movesToBase: Int = Int.MAX_VALUE
}

class Game {

}

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val baseX = input.nextInt() // The corner of the map representing your base
    val baseY = input.nextInt()
    val base = Point(baseX, baseY)
    var myBaseHealth = 0;

    var enemyHealth = 0;
    val heroesPerPlayer = input.nextInt() // Always 3
    val heroX = mutableMapOf<Int, Int>()
    val heroY = mutableMapOf<Int, Int>()
    val heroId2Spider = mutableMapOf<Int, Spider>()
    // game loop
    while (true) {
        for (i in 0 until 2) {
            val health = input.nextInt() // Your base health
            val mana = input.nextInt() // Ignore in the first league; Spend ten mana to cast a spell
            if (i == 0) {
                myBaseHealth = health;
            } else {
                enemyHealth = health
            }
        }
        val entityCount = input.nextInt() // Amount of heros and monsters you can see

        val spiders = mutableListOf<Spider>()
        val heroes = mutableListOf<Hero>()

        for (i in 0 until entityCount) {
            val id = input.nextInt() // Unique identifier
            val type = input.nextInt() // 0=monster, 1=your hero, 2=opponent hero
            val x = input.nextInt() // Position of this entity
            val y = input.nextInt()
            val shieldLife = input.nextInt() // Ignore for this league; Count down until shield spell fades
            val isControlled =
                input.nextInt() // Ignore for this league; Equals 1 when this entity is under a control spell
            val health = input.nextInt() // Remaining health of this monster
            val vx = input.nextInt() // Trajectory of this monster
            val vy = input.nextInt()
            val nearBase = input.nextInt() // 0=monster with no target yet, 1=monster targeting a base
            val threatFor =
                input.nextInt() // Given this monster's trajectory, is it a threat to 1=your base, 2=your opponent's base, 0=neither

            if (type == 1) {
                log("hero $id shift: x=${x - (heroX[id] ?: 0)} y=${y - (heroY[id] ?: 0)}")
                heroX[id] = x
                heroY[id] = y
                heroes.add(Hero(Point(x, y), id, health))
            } else if (type == 0) {
                spiders.add(Spider(Point(x, y), health, vx, vy, nearBase, threatFor))
            }
        }

        spiders.forEach { spider ->
            if (spider.threatFor == 1) {
                if (spider.point.distance(base) > 5000) {
                    var steps = 0
                    var currentPoint = spider.point
                    while (currentPoint.distance(base) > 5000) {
                        steps++
                        currentPoint = Point(currentPoint.x + spider.vx, currentPoint.y + spider.vy)
                    }
                    spider.movesToBase = steps + 12 // todo!
                } else {
                    spider.movesToBase = (spider.point.distance(base) - 300) / 400
                }
            }
        }

        val minSpiders = spiders.sortedBy { it.movesToBase }.take(3).toMutableList()

        val heroSpider = mutableMapOf<Hero, Spider>()
        minSpiders.forEach { spider ->
            val hero = heroes.filterNot { heroSpider.containsKey(it) }
                .minByOrNull { it.point.distance(spider.point) }!!
            heroSpider[hero] = spider;
        }

        for (i in 0 until heroesPerPlayer) {
            val minSpider = heroSpider[heroes[i]]
            // In the first league: MOVE <x> <y> | WAIT; In later leagues: | SPELL <spellParams>;
            if (minSpider != null) {
                println("MOVE ${minSpider.point.x} ${minSpider.point.y}")
                minSpiders.remove(minSpider)
            } else {
                if (baseX < 3000) {
                    when (i) {
                        0 -> println("MOVE 4900 1000")
                        1 -> println("MOVE 3600 3600")
                        else -> println("MOVE 1000 4900")
                    }
                } else {
                    when (i) {
                        0 -> println("MOVE ${baseX - 4900} ${baseY - 1000}")
                        1 -> println("MOVE ${baseX - 3600} ${baseX - 3600}")
                        else -> println("MOVE ${baseX - 1000} ${baseX - 4900}")
                    }
                }

            }

        }
    }
}

fun Spider.targetPoint() = Point(this.point.x + this.vx, this.point.y + this.vy)
fun Point.distance(other: Point) = (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y)
fun log(s: String) = System.err.println(s)