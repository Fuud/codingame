import java.util.Scanner
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args: Array<String>) {

    val input = Scanner(System.`in`)
    val depth = input.nextInt()
//    for (i in 0 until 3) {
//        for (j in 0 until 3) {
//            val value = input.nextInt()
//        }
//    }

//    var current = HashMap<Int, Int>()
    var current = Int2Int()
    val start =
        input.nextInt().shl(SHIFT_00)
            .or(input.nextInt().shl(SHIFT_01))
            .or(input.nextInt().shl(SHIFT_02))
            .or(input.nextInt().shl(SHIFT_10))
            .or(input.nextInt().shl(SHIFT_11))
            .or(input.nextInt().shl(SHIFT_12))
            .or(input.nextInt().shl(SHIFT_20))
            .or(input.nextInt().shl(SHIFT_21))
            .or(input.nextInt().shl(SHIFT_22))

    System.err.println(depth)
    System.err.println(start)
    val countUnique = false
    val unique = mutableSetOf<Int>()
    var overGames = 0
//    current[start] = 1
    current.addTo(start, 1)
    var result = 0
    var r00 = 0
    var r01 = 0
    var r02 = 0
    var r10 = 0
    var r11 = 0
    var r12 = 0
    var r20 = 0
    var r21 = 0
    var r22 = 0
    var total = 0L
    for (turn in 1..depth) {
//        val next = HashMap<Int, Int>()
//        for ((board, times) in current) {
        val next = Int2Int()
        current.int2IntEntrySet().fastForEach {
            val board = it.key
            val times = it.value
            if (isOver(board)) {
                r00 += times * get00(board)
                r01 += times * get01(board)
                r02 += times * get02(board)
                r10 += times * get10(board)
                r11 += times * get11(board)
                r12 += times * get12(board)
                r20 += times * get20(board)
                r21 += times * get21(board)
                r22 += times * get22(board)
                total += times
                if (countUnique) {
                    unique.add(board)
                    overGames++
                }
//                System.err.println("+++${board.toDecimal()} : $times")
            } else {
                move(next, times, board)
            }
        }
        System.err.println("$turn ========== $total:${next.size}")
//        next.forEach { System.err.println(it)
//            System.err.println("---")
//        }
        current = next
    }
    current.int2IntEntrySet().fastForEach {
        val board = it.key
        val times = it.value
        r00 += times * get00(board)
        r01 += times * get01(board)
        r02 += times * get02(board)
        r10 += times * get10(board)
        r11 += times * get11(board)
        r12 += times * get12(board)
        r20 += times * get20(board)
        r21 += times * get21(board)
        r22 += times * get22(board)
        total += times
    }
    System.err.println("unique=${unique.size} from $overGames")
    System.err.println(total)


    // Write an action using println()
    // To debug: System.err.println("Debug messages...");
    arrayOf(r00, r01, r02, r10, r11, r12, r20, r21, r22)
        .forEach {
            result = (result * 10 + it) % MODULE
            while (result < 0) result += MODULE
        }
    println(result)
}

const val SHIFT_00 = 24
const val SHIFT_01 = 21
const val SHIFT_02 = 18
const val SHIFT_10 = 15
const val SHIFT_11 = 12
const val SHIFT_12 = 9
const val SHIFT_20 = 6
const val SHIFT_21 = 3
const val SHIFT_22 = 0
const val MASK_00 = 7.shl(24)
const val MASK_01 = 7.shl(21)
const val MASK_02 = 7.shl(18)
const val MASK_10 = 7.shl(15)
const val MASK_11 = 7.shl(12)
const val MASK_12 = 7.shl(9)
const val MASK_20 = 7.shl(6)
const val MASK_21 = 7.shl(3)
const val MASK_22 = 7
const val ZERO_00 = MASK_00.inv()
const val ZERO_01 = MASK_01.inv()
const val ZERO_02 = MASK_02.inv()
const val ZERO_10 = MASK_10.inv()
const val ZERO_11 = MASK_11.inv()
const val ZERO_12 = MASK_12.inv()
const val ZERO_20 = MASK_20.inv()
const val ZERO_21 = MASK_21.inv()
const val ZERO_22 = MASK_22.inv()

const val ONE: Int = 1
const val ONE_00 = ONE.shl(SHIFT_00)
const val ONE_01 = ONE.shl(SHIFT_01)
const val ONE_02 = ONE.shl(SHIFT_02)
const val ONE_10 = ONE.shl(SHIFT_10)
const val ONE_11 = ONE.shl(SHIFT_11)
const val ONE_12 = ONE.shl(SHIFT_12)
const val ONE_20 = ONE.shl(SHIFT_20)
const val ONE_21 = ONE.shl(SHIFT_21)
const val ONE_22 = ONE.shl(SHIFT_22)

fun get00(v: Int): Int = v.and(MASK_00).shr(SHIFT_00)
fun get01(v: Int): Int = v.and(MASK_01).shr(SHIFT_01)
fun get02(v: Int): Int = v.and(MASK_02).shr(SHIFT_02)
fun get10(v: Int): Int = v.and(MASK_10).shr(SHIFT_10)
fun get11(v: Int): Int = v.and(MASK_11).shr(SHIFT_11)
fun get12(v: Int): Int = v.and(MASK_12).shr(SHIFT_12)
fun get20(v: Int): Int = v.and(MASK_20).shr(SHIFT_20)
fun get21(v: Int): Int = v.and(MASK_21).shr(SHIFT_21)
fun get22(v: Int): Int = v.and(MASK_22).shr(SHIFT_22)
fun raw00(v: Int): Int = v.and(MASK_00)
fun raw01(v: Int): Int = v.and(MASK_01)
fun raw02(v: Int): Int = v.and(MASK_02)
fun raw10(v: Int): Int = v.and(MASK_10)
fun raw11(v: Int): Int = v.and(MASK_11)
fun raw12(v: Int): Int = v.and(MASK_12)
fun raw20(v: Int): Int = v.and(MASK_20)
fun raw21(v: Int): Int = v.and(MASK_21)

fun raw22(v: Int): Int = v.and(MASK_22)
const val ZERO: Int = 0
const val SIX: Int = 6
const val MODULE = 1.shl(30)

fun isOver(v: Int): Boolean =
    raw00(v) != ZERO && raw01(v) != ZERO && raw02(v) != ZERO &&
        raw10(v) != ZERO && raw11(v) != ZERO && raw12(v) != ZERO &&
        raw20(v) != ZERO && raw21(v) != ZERO && raw22(v) != ZERO


fun toDecimal(v: Int) =
    100000000 * get00(v) +
        10000000 * get01(v) +
        1000000 * get02(v) +
        100000 * get10(v) +
        10000 * get11(v) +
        1000 * get12(v) +
        100 * get20(v) +
        10 * get21(v) +
        get22(v)


//fun move(set: HashMap<Int, Int>, times: Int, v: Int) {
fun move(set: Int2Int, times: Int, v: Int) {
    move00(set, times, v)
    move01(set, times, v)
    move02(set, times, v)
    move10(set, times, v)
    move11(set, times, v)
    move12(set, times, v)
    move20(set, times, v)
    move21(set, times, v)
    move22(set, times, v)
}


private fun move22(set: Int2Int, times: Int, v: Int) {
    if (raw22(v) == ZERO) {
        move2(v, set, times, SHIFT_22, MASK_21, SHIFT_21, ZERO_21, MASK_12, SHIFT_12, ZERO_12, ONE_22)
    }
}


private fun move21(set: Int2Int, times: Int, v: Int) {
    if (raw21(v) == ZERO) {
        val captured0211 = capture2(v, set, times, SHIFT_21, MASK_20, SHIFT_20, ZERO_20, MASK_11, SHIFT_11, ZERO_11)
        val captured2211 = capture2(v, set, times, SHIFT_21, MASK_22, SHIFT_22, ZERO_22, MASK_11, SHIFT_11, ZERO_11)
        val captured2220 = capture2(v, set, times, SHIFT_21, MASK_22, SHIFT_22, ZERO_22, MASK_20, SHIFT_20, ZERO_20)
        if (captured0211 && captured2220 && captured2211) {
            val g20 = get20(v)
            val g11 = get11(v)
            val g22 = get22(v)
            val s222011 = g22 + g20 + g11
            if (s222011 <= SIX) {
                set.addTo(v.and(ZERO_22).and(ZERO_20).and(ZERO_11).or(s222011.shl(SHIFT_21)), times)
            }

        } else if (!(captured0211 || captured2220 || captured2211)) {
            set.addTo(v.or(ONE_21), times)
        }
    }
}

private fun move20(set: Int2Int, times: Int, v: Int) {
    if (raw20(v) == ZERO) {
        move2(v, set, times, SHIFT_20, MASK_21, SHIFT_21, ZERO_21, MASK_10, SHIFT_10, ZERO_10, ONE_20)
    }
}

private fun move12(set: Int2Int, times: Int, v: Int) {
    if (raw12(v) == ZERO) {
        val captured0211 = capture2(v, set, times, SHIFT_12, MASK_02, SHIFT_02, ZERO_02, MASK_11, SHIFT_11, ZERO_11)
        val captured2211 = capture2(v, set, times, SHIFT_12, MASK_22, SHIFT_22, ZERO_22, MASK_11, SHIFT_11, ZERO_11)
        val captured2202 = capture2(v, set, times, SHIFT_12, MASK_22, SHIFT_22, ZERO_22, MASK_02, SHIFT_02, ZERO_02)
        if (captured0211 && captured2202 && captured2211) {
            val g02 = get02(v)
            val g11 = get11(v)
            val g22 = get22(v)
            val s002011 = g22 + g02 + g11
            if (s002011 <= SIX) {
                set.addTo(v.and(ZERO_22).and(ZERO_02).and(ZERO_11).or(s002011.shl(SHIFT_12)), times)
            }
        } else if (!(captured0211 || captured2202 || captured2211)) {
            set.addTo(v.or(ONE_12), times)
        }
    }
}

private fun move11(set: Int2Int, times: Int, v: Int) {
    if (raw11(v) == ZERO) {
        val captured0112 = capture2(v, set, times, SHIFT_11, MASK_01, SHIFT_01, ZERO_01, MASK_12, SHIFT_12, ZERO_12)
        val captured1221 = capture2(v, set, times, SHIFT_11, MASK_21, SHIFT_21, ZERO_21, MASK_12, SHIFT_12, ZERO_12)
        val captured2110 = capture2(v, set, times, SHIFT_11, MASK_10, SHIFT_10, ZERO_10, MASK_21, SHIFT_21, ZERO_21)
        val captured1001 = capture2(v, set, times, SHIFT_11, MASK_01, SHIFT_01, ZERO_01, MASK_10, SHIFT_10, ZERO_10)
        val captured0121 = capture2(v, set, times, SHIFT_11, MASK_01, SHIFT_01, ZERO_01, MASK_21, SHIFT_21, ZERO_21)
        val captured1012 = capture2(v, set, times, SHIFT_11, MASK_10, SHIFT_10, ZERO_10, MASK_12, SHIFT_12, ZERO_12)
        if (captured0112 && captured1221 && captured0121) {
            val g01 = get01(v)
            val g12 = get12(v)
            val g21 = get21(v)
            val s011221 = g21 + g01 + g12
            if (s011221 <= SIX) {
                set.addTo(v.and(ZERO_21).and(ZERO_01).and(ZERO_12).or(s011221.shl(SHIFT_11)), times)
            }
        }
        if (captured2110 && captured1221 && captured1012) {
            val g10 = get10(v)
            val g12 = get12(v)
            val g21 = get21(v)
            val s101221 = g21 + g10 + g12
            if (s101221 <= SIX) {
                set.addTo(v.and(ZERO_21).and(ZERO_10).and(ZERO_12).or(s101221.shl(SHIFT_11)), times)
            }
        }
        if (captured2110 && captured1001 && captured0121) {
            val g10 = get10(v)
            val g01 = get01(v)
            val g21 = get21(v)
            val s100121 = g21 + g10 + g01
            if (s100121 <= SIX) {
                set.addTo(v.and(ZERO_01).and(ZERO_10).and(ZERO_21).or(s100121.shl(SHIFT_11)), times)
            }
        }
        if (captured1012 && captured1001 && captured0112) {
            val g10 = get10(v)
            val g01 = get01(v)
            val g12 = get12(v)
            val s100112 = g12 + g10 + g01
            if (s100112 <= SIX) {
                set.addTo(v.and(ZERO_01).and(ZERO_10).and(ZERO_12).or(s100112.shl(SHIFT_11)), times)
            }
        }
        if (captured0121 && captured0112 && captured1012 && captured1221 && captured2110 && captured1001) {
            val s12102101 = get12(v) + get10(v) + get21(v) + get01(v)
            if (s12102101 <= SIX) {
                set.addTo(v.and(ZERO_01).and(ZERO_10).and(ZERO_12).and(ZERO_21).or(s12102101.shl(SHIFT_11)), times)
            }
        }

        if (!(captured0121 || captured0112 || captured1012 || captured1221 || captured2110 || captured1001)) {
            set.addTo(v.or(ONE_11), times)
        }
    }
}

private fun move10(set: Int2Int, times: Int, v: Int) {
    if (raw10(v) == ZERO) {
        val captured2011 = capture2(v, set, times, SHIFT_10, MASK_20, SHIFT_20, ZERO_20, MASK_11, SHIFT_11, ZERO_11)
        val captured0011 = capture2(v, set, times, SHIFT_10, MASK_00, SHIFT_00, ZERO_00, MASK_11, SHIFT_11, ZERO_11)
        val captured0020 = capture2(v, set, times, SHIFT_10, MASK_00, SHIFT_00, ZERO_00, MASK_20, SHIFT_20, ZERO_20)
        if (captured2011 && captured0020 && captured0011) {
            val g20 = get20(v)
            val g11 = get11(v)
            val g00 = get00(v)
            val s002011 = g00 + g20 + g11
            if (s002011 <= SIX) {
                set.addTo(v.and(ZERO_00).and(ZERO_20).and(ZERO_11).or(s002011.shl(SHIFT_10)), times)
            }

        } else if (!(captured2011 || captured0020 || captured0011)) {
            set.addTo(v.or(ONE_10), times)
        }
    }

}


fun toString(v: Int): String {
    return "${get00(v)} ${get01(v)} ${get02(v)}\n${get10(v)} ${get11(v)} ${get12(v)}\n${get20(v)} ${get21(v)} ${get22(v)}"
}

private fun move00(set: Int2Int, times: Int, v: Int) {
    if (raw00(v) == ZERO) {
        move2(v, set, times, SHIFT_00, MASK_01, SHIFT_01, ZERO_01, MASK_10, SHIFT_10, ZERO_10, ONE_00)
    }
}

private fun move02(set: Int2Int, times: Int, v: Int) {
    if (raw02(v) == ZERO) {
        move2(v, set, times, SHIFT_02, MASK_01, SHIFT_01, ZERO_01, MASK_12, SHIFT_12, ZERO_12, ONE_02)
    }
}

private fun move01(set: Int2Int, times: Int, v: Int) {
    if (raw01(v) == ZERO) {
        val captured0211 = capture2(v, set, times, SHIFT_01, MASK_02, SHIFT_02, ZERO_02, MASK_11, SHIFT_11, ZERO_11)
        val captured0011 = capture2(v, set, times, SHIFT_01, MASK_00, SHIFT_00, ZERO_00, MASK_11, SHIFT_11, ZERO_11)
        val captured0002 = capture2(v, set, times, SHIFT_01, MASK_00, SHIFT_00, ZERO_00, MASK_02, SHIFT_02, ZERO_02)
        if (captured0211 && captured0011 && captured0002) {
            val g02 = get02(v)
            val g11 = get11(v)
            val g00 = get00(v)
            val s000211 = g00 + g02 + g11
            if (s000211 <= SIX) {
                set.addTo(v.and(ZERO_00).and(ZERO_02).and(ZERO_11).or(s000211.shl(SHIFT_01)), times)
            }
        } else if (!(captured0211 || captured0011 || captured0002)) {
            set.addTo(v.or(ONE_01), times)
        }
    }
}

fun move2(
    v: Int,
    set: Int2Int,
    times: Int,
    shift: Int,
    mask0: Int,
    shift0: Int,
    zero0: Int,
    mask1: Int,
    shift1: Int,
    zero1: Int,
    one: Int,
) {
    if (!capture2(v, set, times, shift, mask0, shift0, zero0, mask1, shift1, zero1)) {
        set.addTo(v.or(one), times)
    }
}

inline private fun capture2(
    v: Int,
    set: Int2Int,
    times: Int,
    shift: Int,
    mask0: Int,
    shift0: Int,
    zero0: Int,
    mask1: Int,
    shift1: Int,
    zero1: Int
): Boolean {
    val r0 = v.and(mask0)
    val r1 = v.and(mask1)
    val s = r0.shr(shift0) + r1.shr(shift1)
    if (s <= SIX && r0 > ZERO && r1 > ZERO) {
        set.addTo(v.and(zero0).and(zero1).or(s.shl(shift)), times)
        return true
    }
    return false
}

private fun add(previous: Int?, times: Int): Int {
    return previous?.let {
        it + times
    } ?: times
}

class Int2Int @JvmOverloads constructor(expected: Int = DEFAULT_INITIAL_SIZE, f: Float = DEFAULT_LOAD_FACTOR) {

    var key: IntArray


    var value: IntArray


    var mask: Int


    var containsNullKey: Boolean = false
    var n: Int
    var maxFill: Int

    var size: Int = 0
    var defRetValue: Int = 0
    val f: Float
    val minN: Int
    var entries: MapEntrySet? = null

    fun addTo(k: Int, incr: Int): Int {
        var pos: Int
        if (((k) == (0))) {
            if (containsNullKey) {
                return addToValue(n, incr)
            }
            pos = n
            containsNullKey = true
        } else {
            var curr: Int
            val key = this.key
            // The starting point.
            if ((key[((mix((k))) and mask).also { pos = it }].also { curr = it }) != (0)) {
                if (((curr) == (k))) {
                    return addToValue(pos, incr)
                }
                while ((key[((pos + 1) and mask).also { pos = it }].also { curr = it }) != (0)) {
                    if (((curr) == (k))) {
                        return addToValue(pos, incr)
                    }
                }
            }
        }
        key[pos] = k
        value[pos] = defRetValue + incr
        if (size++ >= maxFill) {
            rehash(arraySize(size + 1, f))
        }
        return defRetValue
    }

    private fun addToValue(pos: Int, incr: Int): Int {
        val oldValue = value[pos]
        value[pos] = oldValue + incr
        return oldValue
    }


    private fun realSize(): Int {
        return if (containsNullKey) size - 1 else size
    }

    protected fun rehash(newN: Int) {
        val key = this.key
        val value = this.value
        val mask = newN - 1 // Note that this is used by the hashing macro
        val newKey = IntArray(newN + 1)
        val newValue = IntArray(newN + 1)
        var i = n
        var pos: Int
        var j = realSize()
        while (j-- != 0) {
            while (((key[--i]) == (0)));
            if ((newKey[((mix((key[i]))) and mask).also { pos = it }]) != (0)) {
                while ((newKey[((pos + 1) and mask).also { pos = it }]) != (0));
            }
            newKey[pos] = key[i]
            newValue[pos] = value[i]
        }
        newValue[newN] = value[n]
        n = newN
        this.mask = mask
        maxFill = maxFill(n, f)
        this.key = newKey
        this.value = newValue
    }

    fun int2IntEntrySet(): MapEntrySet {
        if (entries == null) entries = MapEntrySet()
        return entries!!
    }

    inner class MapEntrySet {
        fun fastForEach(consumer: Consumer<BasicEntry>) {
            val entry = BasicEntry()
            if (containsNullKey) {
                entry.key = key[n]
                entry.value = value[n]
                consumer.accept(entry)
            }
            var pos = n
            while (pos-- != 0) {
                if ((key[pos]) != (0)) {
                    entry.key = key[pos]
                    entry.value = value[pos]
                    consumer.accept(entry)
                }
            }
        }
    }

    class BasicEntry {
        var key: Int = 0
        var value: Int = 0
    }


    /**
     * Creates a new hash map with initial expected
     * [Hash.DEFAULT_INITIAL_SIZE] entries and
     * [Hash.DEFAULT_LOAD_FACTOR] as load factor.
     */
    init {
        require(!(f <= 0 || f > 1)) { "Load factor must be greater than 0 and smaller than or equal to 1" }
        require(expected >= 0) { "The expected number of elements must be nonnegative" }
        this.f = f
        n = arraySize(expected, f)
        minN = n
        mask = n - 1
        maxFill = maxFill(n, f)
        key = IntArray(n + 1)
        value = IntArray(n + 1)
    }

    companion object {

        const val DEFAULT_INITIAL_SIZE: Int = 16


        const val DEFAULT_LOAD_FACTOR: Float = .75f
        private const val INT_PHI = -0x61c88647

        fun mix(x: Int): Int {
            val h = x * INT_PHI
            return h xor (h ushr 16)
        }

        fun nextPowerOfTwo(x: Long): Long {
            var x = x
            if (x == 0L) return 1
            x--
            x = x or (x shr 1)
            x = x or (x shr 2)
            x = x or (x shr 4)
            x = x or (x shr 8)
            x = x or (x shr 16)
            return (x or (x shr 32)) + 1
        }

        fun arraySize(expected: Int, f: Float): Int {
            val s: Long = max(2, nextPowerOfTwo(ceil((expected / f).toDouble()).toLong()))
            require(s <= (1 shl 30)) { "Too large (" + expected + " expected elements with load factor " + f + ")" }
            return s.toInt()
        }

        fun maxFill(n: Int, f: Float): Int {
            /* We must guarantee that there is always at least
		 * one free entry (even with pathological load factors). */
            return min(ceil((n * f).toDouble()).toInt(), n - 1)
        }
    }
}