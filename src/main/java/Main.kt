import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Scanner
import kotlin.math.sqrt

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

const val BOARD_WIDTH = 17630
const val BOARD_HEIGHT = 9000
const val SPELL_MANA_COST = 10

const val THREAT_FOR_OUR_BASE = 1
const val THREAT_FOR_ENEMY_BASE = 2
const val NO_THREAT = 0

val enemyBase = Point(
    BOARD_WIDTH,
    BOARD_HEIGHT
)

const val FOG_RADIUS = 2200

var topLeftBase: Boolean = true

var tick: Int = 0

data class Point(val x: Int, val y: Int)

class Hero(
    val entityId: Int,
    point: Point,
    val idx: Int,
    val health: Int,
    val shieldLife: Int,
    val isControlled: Boolean
) {
    val point: Point = if (topLeftBase) {
        point
    } else {
        Point(BOARD_WIDTH - point.x, BOARD_HEIGHT - point.y)
    }

    fun distance(hero: Hero): Distance {
        return point.distance(hero.point)
    }

    fun distance(spider: Spider): Distance {
        return point.distance(spider.point)
    }

    fun distance(point: Point): Distance {
        return point.distance(this.point)
    }
}

class Spider(
    val entityId: Int,
    point: Point,
    val health: Int,
    vx: Int,
    vy: Int,
    val nearBase: Int,
    val threatFor: Int,
    val shieldLife: Int,
    val isControlled: Boolean
) {
    val point: Point = if (topLeftBase) {
        point
    } else {
        Point(BOARD_WIDTH - point.x, BOARD_HEIGHT - point.y)
    }

    val vx: Int = if (topLeftBase) {
        vx
    } else {
        -vx
    }
    val vy: Int = if (topLeftBase) {
        vy
    } else {
        -vy
    }

    fun distance(hero: Hero): Distance {
        return hero.point.distance(this.point)
    }

    fun distance(point: Point): Distance {
        return point.distance(this.point)
    }

    fun distance(spider: Spider): Distance = distance(spider.point)

    var movesToBase: Int? = null
}

var mana: Int = 0

val heroes = mutableListOf<Hero>()

val VASYA
    get() = heroes[0]

val PETYA
    get() = heroes[1]

val KOLYA
    get() = heroes[2]

fun main(args: Array<String>) {
    performGame()
}

fun performGame(echo: Boolean = true){
    val input = if (echo){
        Scanner(TeeInputStream(System.`in`, System.err))
    }else {
        Scanner(System.`in`)
    }
    val baseX = input.nextInt() // The corner of the map representing your base
    val baseY = input.nextInt()

    topLeftBase = baseX == 0

    val startPoints = listOf(
        Point(3000, 7600),
        Point(8000, 800)
    )


    val base = Point(0, 0)
    var myBaseHealth = 0;
    var enemyMana = 0;

    var enemyHealth = 0;
    val heroesPerPlayer = input.nextInt() // Always 3
    val heroX = mutableMapOf<Int, Int>()
    val heroY = mutableMapOf<Int, Int>()
    val heroId2Spider = mutableMapOf<Int, Spider>()
    // game loop
    while (true) {
        tick++
        log("tick: $tick")
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
        heroes.clear()
        val enemies = mutableListOf<Hero>()

        for (i in 0 until entityCount) {
            val id = input.nextInt() // Unique identifier
            val type = input.nextInt() // 0=monster, 1=your hero, 2=opponent hero
            val x = input.nextInt() // Position of this entity
            val y = input.nextInt()
            val shieldLife = input.nextInt() // Ignore for this league; Count down until shield spell fades
            val isControlled =
                input.nextInt() == 1 // Ignore for this league; Equals 1 when this entity is under a control spell
            val health = input.nextInt() // Remaining health of this monster
            val vx = input.nextInt() // Trajectory of this monster
            val vy = input.nextInt()
            val nearBase = input.nextInt() // 0=monster with no target yet, 1=monster targeting a base
            val threatFor =
                input.nextInt() // Given this monster's trajectory, is it a threat to 1=your base, 2=your opponent's base, 0=neither

            when (type) {
                1 -> heroes.add(Hero(id, Point(x, y), heroes.size, health, shieldLife, isControlled))
                2 -> enemies.add(Hero(id, Point(x, y), enemies.size, health, shieldLife, isControlled))
                0 -> spiders.add(Spider(id, Point(x, y), health, vx, vy, nearBase, threatFor, shieldLife, isControlled))
            }
        }

        spiders.forEach { spider ->
            if (spider.threatFor == THREAT_FOR_OUR_BASE) {
                if (spider.point.distance(base) > d(5000)) {
                    var steps = 0
                    var currentPoint = spider.point
                    while (currentPoint.distance(base) > d(5000)) {
                        steps++
                        currentPoint = Point(currentPoint.x + spider.vx, currentPoint.y + spider.vy)
                    }
                    spider.movesToBase = steps + 12 // todo!
                } else {
                    spider.movesToBase =
                        kotlin.math.ceil((spider.point.distance(base).toDouble() - 300) / 400).toInt()
                }
            } else if (spider.threatFor == THREAT_FOR_ENEMY_BASE) {
                spider.movesToBase = kotlin.math.ceil(Int.MAX_VALUE - spider.distance(base).toDouble()).toInt()
            } else {
                spider.movesToBase = kotlin.math.ceil(Int.MAX_VALUE / 2 - spider.distance(base).toDouble()).toInt()
            }
        }

        val minSpiders = spiders.filter { it.movesToBase != null }.sortedBy { it.movesToBase }.take(2).toMutableList()

        val hero2Spider = mutableMapOf<Hero, Spider>()
        minSpiders.forEach { spider ->
            val hero = heroes.filterNot { hero2Spider.containsKey(it) }
                .minByOrNull { it.point.distance(spider.point) }!!
            hero2Spider[hero] = spider
        }

        val heroActions = mutableMapOf<Hero, String>()


        with(KOLYA) {
            heroActions[this] = processKolya(spiders, enemies)
        }


        val sortedHeroes = listOf(VASYA, PETYA).sortedBy { it.point.distance(base) }
        val minSpider = minSpiders.firstOrNull()
        val nearSpiderDistance = minSpider?.distance(base) ?: Distance.MAX_VALUE
        val threatSpider = nearSpiderDistance < d(3500)
        val nearEnemy = enemies.minByOrNull { it.point.distance(base) }
        val threatEnemy = (nearEnemy?.point?.distance(base) ?: Distance.MAX_VALUE) < d(6280)

        var wasShield = false

        heroActions.putAll(sortedHeroes.map { hero ->
            if (hero.isControlled) {
                return@map hero to controlledWait
            }
            if (threatEnemy && nearEnemy!!.shieldLife == 0 && mana >= 10 && nearSpiderDistance < d(4500) &&
                Wind.inRange(nearEnemy, minSpider!!)
            ) {
                if (Wind.inRange(hero, nearEnemy.point) || Wind.inRange(hero, minSpider.point)) {
                    return@map hero to Wind.cast(BOARD_WIDTH, BOARD_HEIGHT)
                }
            }
            if (threatEnemy && nearEnemy!!.shieldLife == 0 && mana >= 40 && nearSpiderDistance < d(4500)) {
                if (Wind.inRange(hero, nearEnemy.point)) {
                    return@map hero to Wind.cast(BOARD_WIDTH, BOARD_HEIGHT)
                }
            }
            if (!wasShield && !threatSpider && hero.shieldLife == 0 && mana >= 40 &&
                (enemies.minOfOrNull { it.distance(hero) } ?: Distance.MAX_VALUE) < d(4000) &&
                hero.point.distance(base) < d(4000)
            ) {
                wasShield = true
                return@map hero to Shield.cast(hero.entityId)
            }

            val heroSpider = hero2Spider[hero]
            // In the first league: MOVE <x> <y> | WAIT; In later leagues: | SPELL <spellParams>;
            if (heroSpider != null) {
                val toBaseDistance = heroSpider.distance(base)
                val near = Wind.inRange(hero, heroSpider.point)
                val wind = toBaseDistance < d(5000) && mana >= 10
                val willReachBase = heroSpider.movesToBase?.let { it * 2 < heroSpider.health + 4 } ?: false
                if (near && wind && heroSpider.shieldLife == 0 && willReachBase) {
                    return@map hero to Wind.cast(BOARD_WIDTH, BOARD_HEIGHT)
                } else {
                    val startPoint = startPoints[hero.idx]
                    if (heroSpider.point.distance(base) < startPoint.distance(base) * 2) {
                        val removeSpider = heroSpider.movesToBase?.let { it * 2 > heroSpider.health } ?: false
                        if (removeSpider) {
                            minSpiders.remove(heroSpider)
                        }
                        val otherSpider = spiders.filter { it.distance(heroSpider) < d(1500) && it != heroSpider }
                            .maxByOrNull { it.distance(base) }
                        if (otherSpider != null) {
                            return@map hero to Move.to(
                                (heroSpider.point.x + otherSpider.point.x) / 2,
                                (heroSpider.point.y + otherSpider.point.y) / 2,
                                "kill ${heroSpider.entityId} and ${otherSpider.entityId}"
                            )
                        } else {
                            return@map hero to Move.to(heroSpider, "kill ${heroSpider.entityId}")
                        }
                    } else {
                        return@map hero to moveToStartPoint(hero, startPoints)
                    }
                }
            } else {
                return@map hero to moveToStartPoint(hero, startPoints)
            }
        }.toMap())

        heroes.forEach { hero ->
            println(heroActions[hero])
        }

    }
}

private fun moveToStartPoint(hero: Hero, startPoints: List<Point>) =
    Move.to(startPoints[hero.idx], "Есть идея")


val isDebut: Boolean
    get() = tick < 30

private fun processKolya(spiders: List<Spider>, enemies: List<Hero>): String {
    return if (isDebut) {
        KolyaEarlyGame.processKolyaEarlyGame(spiders, enemies)
    } else {
        KolyaLateGame.processKolyaLateGame(spiders, enemies)
    }
}

object KolyaEarlyGame {
    var initialMarch = true

    fun processKolyaEarlyGame(spiders: List<Spider>, enemies: List<Hero>): String {
        val initialPoint = Point(9000, 8000)
        if (initialMarch) {
            if (KOLYA.distance(initialPoint) > d(800)) {
                return Move.to(initialPoint, "MARCH! URA!!!")
            }
            initialMarch = false
        }
        val spiderToAttack =
            spiders.filter { it.distance(KOLYA) < d(FOG_RADIUS * 2) }.minByOrNull { it.distance(KOLYA) }
        if (spiderToAttack != null) {
            return Move.to(spiderToAttack, "Mana must flow!")
        } else {
            val patrolPoints = listOf(
                initialPoint,
                Point(initialPoint.x - 1000, initialPoint.y)
            )
            return Move.to(patrolPoints.max { it.distance(KOLYA) })
        }
    }
}

object KolyaLateGame {
    var initialMarch = true

    var kolyaDirection: Int = 0

    var letsCheck: Point? = null

    fun processKolyaLateGame(spiders: List<Spider>, enemies: List<Hero>): String {
        val initialPoints = listOf(
            Point(15000, 6300),
            Point(13050, 1800),
            Point(10000, 8400),
            Point(15000, 6300),
        )
        if (KOLYA.isControlled) {
            return controlledWait
        }

        if (initialMarch) {
            if (KOLYA.distance(initialPoints[1]) > d(800)) {
                return Move.to(initialPoints[1], "Сарынь на кичку")
            }
            initialMarch = false
        }

        if (mana > SPELL_MANA_COST) {
            val shieldSpider = spiders.filter {
                it.health > 10 &&
                        Shield.inRange(KOLYA, it) &&
                        it.distance(
                            enemyBase
                        ) < d(5000) &&
                        it.shieldLife == 0
            }.minByOrNull { it.distance(enemyBase) }

            if (shieldSpider != null) {
                return Shield.cast(shieldSpider)
            }

            val windySpiders = spiders.filter { it.health > 10 && Wind.inRange(KOLYA, it) && it.shieldLife == 0 }

            if (windySpiders.size > 1) {
                letsCheck = enemyBase
                return Wind.cast(BOARD_WIDTH, BOARD_HEIGHT)
            }

            val spiderToControl =
                spiders.filter {
                    Control.inRange(it, KOLYA.point) &&
                            it.shieldLife == 0 &&
                            it.threatFor != THREAT_FOR_ENEMY_BASE &&
                            it.health > 10
                }.maxByOrNull { it.health }
            if (spiderToControl != null) {
                val targetPoints = listOf(
                    Point(BOARD_WIDTH - 410, BOARD_HEIGHT - 5000 + 410),
                    Point(BOARD_WIDTH - 5000 + 410, BOARD_HEIGHT - 410),
                )
                return Control.cast(
                    spiderToControl.entityId,
                    targetPoints.min { spiderToControl.distance(it) },
                    "Всё для фронта!"
                )
            }
            letsCheck?.let {
                letsCheck = null
                return Move.to(it, "Just curious")
            }
            if (mana > SPELL_MANA_COST * 4) {
                val spiderToMoveToAndControl = spiders.filter {
                    KOLYA.distance(it) < d(FOG_RADIUS) &&
                            it.shieldLife < 2 &&
                            it.threatFor != THREAT_FOR_ENEMY_BASE &&
                            it.health > 10
                }.maxByOrNull { it.health }

                if (spiderToMoveToAndControl != null) {
                    return Move.to(
                        Point(
                            spiderToMoveToAndControl.point.x + spiderToMoveToAndControl.vx,
                            spiderToMoveToAndControl.point.y + spiderToMoveToAndControl.vy
                        ),
                        "Plan cntr ${spiderToMoveToAndControl.entityId}"
                    )
                }
            }
        }

        val spiderToAttack =
            spiders.filter {
                it.distance(
                    enemyBase
                ) <= d(7000) && it.threatFor != THREAT_FOR_ENEMY_BASE
            }
                .minByOrNull { it.distance(KOLYA) }

        if (spiderToAttack != null) {
            return Move.to(spiderToAttack.point, "KILL")
        } else {
            val targetPoint = initialPoints[kolyaDirection]
            if (KOLYA.point.distance(targetPoint) < d(700)) {
                kolyaDirection = (kolyaDirection + 1).mod(initialPoints.size)
            }
            return Move.to(targetPoint, "POST")

        }
    }
}

const val controlledWait = "WAIT Покорен будь судьбе"

private fun <T, R : Comparable<R>> Iterable<T>.min(function: (T) -> R): T {
    return this.minByOrNull(function)!!
}

private fun <T, R : Comparable<R>> Iterable<T>.max(function: (T) -> R): T {
    return this.maxByOrNull(function)!!
}

fun Spider.targetPoint() = Point(this.point.x + this.vx, this.point.y + this.vy)
fun Point.distance(other: Point) =
    Distance((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y))

fun Point.distance(other: Hero) = this.distance(other.point)
fun Point.distance(other: Spider) = this.distance(other.point)

var logStream = System.err
fun log(s: Any?) {
    logStream.println("\n# $s\n")
    System.err.flush()
    System.out.flush()
}
fun d(d: Int) = Distance(d * d)

@JvmInline
value class Distance(val d: Int) : Comparable<Distance> {
    override fun compareTo(other: Distance): Int {
        return d - other.d
    }

    fun toDouble(): Double = sqrt(d.toDouble())
    operator fun times(i: Int): Distance {
        return Distance(d * i * i)
    }

    companion object {
        val MAX_VALUE = Distance(Integer.MAX_VALUE)
    }

}

class Wind {
    companion object {
        private val range = d(1280)

        val shift = 1200
        fun cast(towards: Point, comment: String = "") = cast(towards.x, towards.y, comment)
        fun inRange(hero: Hero, point: Point) = hero.point.distance(point) <= range
        fun inRange(hero: Hero, spider: Spider) = inRange(hero, spider.point)
        fun cast(x: Int, y: Int, comment: String = ""): String {
            mana -= SPELL_MANA_COST
            return if (topLeftBase) {
                "SPELL WIND $x $y $comment"
            } else {
                "SPELL WIND ${BOARD_WIDTH - x} ${BOARD_HEIGHT - y} $comment"
            }
        }
    }
}

class Control {
    companion object {
        private val range = d(1280)
        fun inRange(hero: Hero, point: Point) = hero.point.distance(point) <= range
        fun inRange(spider: Spider, point: Point) = spider.point.distance(point) <= range
        fun cast(entityId: Int, towards: Point, comment: String = ""): String {
            mana -= SPELL_MANA_COST
            return if (topLeftBase) {
                "SPELL CONTROL $entityId ${towards.x} ${towards.y} $comment"
            } else {
                "SPELL CONTROL $entityId ${BOARD_WIDTH - towards.x} ${BOARD_HEIGHT - towards.y} $comment"
            }
        }
    }
}

class Shield {
    companion object {
        private val range = d(2200)
        fun inRange(hero: Hero, point: Point) = hero.point.distance(point) <= range
        fun inRange(hero: Hero, spider: Spider) = hero.point.distance(spider) <= range
        fun inRange(hero: Hero, target: Hero) = hero.point.distance(target) <= range
        fun cast(entityId: Int, comment: String = ""): String {
            mana -= SPELL_MANA_COST
            return "SPELL SHIELD $entityId $comment"
        }

        fun cast(spider: Spider, comment: String = ""): String = cast(spider.entityId, comment)
    }
}

class Move {
    companion object {
        private val range = d(800)
        fun to(towards: Point, comment: String = "") = to(towards.x, towards.y, comment)
        fun to(x: Int, y: Int, comment: String = "") = if (topLeftBase) {
            "MOVE $x $y $comment"
        } else {
            "MOVE ${BOARD_WIDTH - x} ${BOARD_HEIGHT - y} $comment"
        }

        fun to(spider: Spider, comment: String = "") = to(spider.point, comment)
    }
}

fun List<Int>.mean() = this.sum() / this.size
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