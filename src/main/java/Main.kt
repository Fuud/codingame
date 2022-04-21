import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

data class Point(val x: Int, val y: Int)
data class Hero(val entityId: Int, val point: Point, val idx: Int, val health: Int, val shieldLife: Int, val isControlled: Int) {
    fun distance(hero: Hero): Int {
        return point.distance(hero.point)
    }
}

data class Base(val point: Point)
data class Spider(
    val entityId: Int,
    val point: Point,
    val health: Int,
    val vx: Int,
    val vy: Int,
    val nearBase: Int,
    val threatFor: Int,
    val shieldLife: Int,
    val isControlled: Int
) {
    fun distance(hero: Hero): Int {
        return hero.point.distance(this.point)
    }

    fun distance(point: Point): Int {
        return point.distance(this.point)
    }

    var movesToBase: Int = Int.MAX_VALUE
}

var mana: Int = 0

class Game {

}

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val baseX = input.nextInt() // The corner of the map representing your base
    val baseY = input.nextInt()

    val startPoints = listOf(
        Point(5900, 2000),
        Point(7600, 7600),
        Point(2000, 5900),
    ).map {
        if (baseX < 3000) {
            it
        } else {
            Point(baseX - it.x, baseY - it.y)
        }
    }


    val base = Point(baseX, baseY)
    var myBaseHealth = 0;
    var enemyMana = 0;

    var enemyHealth = 0;
    val heroesPerPlayer = input.nextInt() // Always 3
    val heroX = mutableMapOf<Int, Int>()
    val heroY = mutableMapOf<Int, Int>()
    val heroId2Spider = mutableMapOf<Int, Spider>()
    // game loop
    while (true) {
        for (i in 0 until 2) {
            val health = input.nextInt() // Your base health
            if (i == 0) {
                mana = input.nextInt()
                myBaseHealth = health;
            } else {
                enemyMana = input.nextInt()
                enemyHealth = health
            }
        }
        val entityCount = input.nextInt() // Amount of heros and monsters you can see

        val spiders = mutableListOf<Spider>()
        val heroes = mutableListOf<Hero>()
        val enemies = mutableListOf<Hero>()

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
                heroes.add(Hero(id, Point(x, y), heroes.size, health, shieldLife, isControlled))
            } else if (type == 2) {
                enemies.add(Hero(id, Point(x, y), enemies.size, health, shieldLife, isControlled))
            } else if (type == 0) {
                spiders.add(Spider(id, Point(x, y), health, vx, vy, nearBase, threatFor, shieldLife, isControlled))
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

        val sortedHeros = heroes.sortedBy { it.point.distance(base) }
        val heroActions = mutableMapOf<Hero, String>()

        sortedHeros.forEach { hero ->
            if (hero.shieldLife <= 1 && (enemies.minOfOrNull { it.distance(hero) } ?: Int.MAX_VALUE) < 3000 * 3000 ){
                heroActions[hero] =
                    ("SPELL SHIELD ${hero.entityId}")
                return@forEach
            }

            val minSpider = heroSpider[hero]
            // In the first league: MOVE <x> <y> | WAIT; In later leagues: | SPELL <spellParams>;
            if (minSpider != null) {
                val distance = minSpider.distance(hero)
                val toBaseDistance = minSpider.distance(base)
                val near = distance < 1280 * 1280 && mana >= 10
                val wind = toBaseDistance < 5000 * 5000 || mana >= 1200
                if (near && wind && minSpider.shieldLife == 0) {
                    mana -= 10
                    heroActions[hero] =
                        ("SPELL WIND ${(minSpider.point.x - baseX) * 100} ${(minSpider.point.y - baseY) * 100}")
                } else {
                    val startPoint = startPoints[hero.idx]
                    if (minSpider.point.distance(base) < startPoint.distance(base) * 2) {
                        heroActions[hero] =
                            ("MOVE ${minSpider.point.x + minSpider.vx} ${minSpider.point.y + minSpider.vy}")
                    } else {

                        heroActions[hero] = ("MOVE ${startPoint.x} ${startPoint.y}")
                    }
                }
                minSpiders.remove(minSpider)
            } else {
                val startPoint = startPoints[hero.idx]
                heroActions[hero] = ("MOVE ${startPoint.x} ${startPoint.y}")

            }
        }

        heroes.forEach { hero ->
            println(heroActions[hero])
        }

    }
}

fun Spider.targetPoint() = Point(this.point.x + this.vx, this.point.y + this.vy)
fun Point.distance(other: Point) = (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y)
fun log(s: String) = System.err.println(s)