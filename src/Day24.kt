import utils.extractAllSignedLongs
import utils.readInput
import kotlin.math.sign

private const val DAY = "24"
private const val SOLUTION_TEST_1 = 2
private const val SOLUTION_TEST_2 = 47

private typealias Vec3 = Triple<Double, Double, Double>
private typealias Vec2 = Pair<Double, Double>

private data class HailStone(val pos: Vec3, val vel: Vec3) {
    val x = pos.first
    val y = pos.second
    val z = pos.third

    val u = vel.first
    val v = vel.second
    val w = vel.third
}

private val validRangePart1Test = 7.0..27.0
private val validRangePart1 = 200000000000000.0..400000000000000.0

private fun List<Long>.toVec3(): Vec3 = Triple(this[0].toDouble(), this[1].toDouble(), this[2].toDouble())

private fun parseHailStone(line: String): HailStone {
    val ints = line.extractAllSignedLongs()
    return HailStone(ints.subList(0, 3).toVec3(), ints.subList(3, 6).toVec3())
}

private fun isInFuture(h: HailStone, c: Pair<Double, Double>): Boolean {
    val futureSignX = sign(h.u)
    val futureSignY = sign(h.v)

    val isInFutureX = when (futureSignX) {
        1.0 -> c.first >= h.x
        -1.0 -> c.first <= h.x
        else -> c.first == h.x
    }

    val isInFutureY = when (futureSignY) {
        1.0 -> c.second >= h.y
        -1.0 -> c.second <= h.y
        else -> c.second == h.y
    }

    return isInFutureX && isInFutureY
}

private fun calculateCrossing(h1: HailStone, h2: HailStone): Pair<Double, Double>? {
    val stones = listOf(h1, h2)

    val m = stones.map { it.v / it.u }

    if (m.first() == m.last()) {
        return null // parallel
    }

    val q = stones.mapIndexed { index, h -> h.y - m[index] * h.x }

    val xCross = (q.last() - q.first()) / (m.first() - m.last())
    val yCross = m.first() * xCross + q.first()

    val potentialCrossing = Pair(xCross, yCross)

    return if (isInFuture(h1, potentialCrossing) && isInFuture(h2, potentialCrossing)) {
        potentialCrossing
    } else {
        null
    }
}

private fun isInArea(crossing: Pair<Double, Double>, area: ClosedFloatingPointRange<Double>) =
    crossing.first in area && crossing.second in area


private fun part1(input: List<String>, area: ClosedFloatingPointRange<Double>): Int {
    val hailStones = input.map(::parseHailStone)

    var totalPotentialCollisions = 0
    hailStones.forEachIndexed { index, h1 ->
        hailStones.subList(index + 1, hailStones.size).forEach { h2 ->
            val crossing = calculateCrossing(h1, h2)
            if (crossing != null && isInArea(crossing, area)) {
                totalPotentialCollisions++
            }
        }
    }

    return totalPotentialCollisions
}

private fun part2(input: List<String>): Int {
    return 0
}

fun main() {
    testPart1()
    runPart1()

//    testPart2()
//    runPart2()
}

/**
 * //////////////////////// AoC setup code \\\\\\\\\\\\\\\\\\\\\\\\\\\
 */

private fun runPart1() = println(part1(mainInput, validRangePart1))

private fun runPart2() = println(part2(mainInput))

private fun testPart1() {
    val result = part1(testInput1, validRangePart1Test)
    check(result == SOLUTION_TEST_1) { "Failed test 1 -> Is: $result, should be: $SOLUTION_TEST_1" }
    println("Test 1 successful!")
}

private fun testPart2() {
    val result = part2(testInput2)
    check(result == SOLUTION_TEST_2) { "Failed test 2 -> Is: $result, should be: $SOLUTION_TEST_2" }
    println("Test 2 successful!")
}

private val mainInput: List<String>
    get() = readInput("Day$DAY")

private val testInput1: List<String>
    get() = readInput("Day${DAY}_test")

private val testInput2: List<String>
    get() = try {
        readInput("Day${DAY}_test2")
    } catch (_: Exception) {
        println("Using test input from part 1 to test part 2")
        testInput1
    }