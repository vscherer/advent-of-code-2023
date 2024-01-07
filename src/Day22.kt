import utils.extractAllUnsignedInts
import utils.readInput
import kotlin.math.abs
import kotlin.math.min
import kotlin.time.measureTime

private const val DAY = "22"
private const val SOLUTION_TEST_1 = 5
private const val SOLUTION_TEST_2 = 7

private data class Brick(val index: Int, val start: Pos3D, val end: Pos3D) // x, y, z
private typealias Pos3D = Triple<Int, Int, Int>
private typealias Pos2D = Pair<Int, Int>

private val Pos3D.x
    get() = first
private val Pos3D.y
    get() = second
private val Pos3D.z
    get() = third
private val Pos3D.pos2D
    get() = Pair(x, y)
private val Pos2D.x
    get() = first
private val Pos2D.y
    get() = second


private val currentHighest: MutableMap<Pos2D, Pair<Int, Brick>> = mutableMapOf() // height, top brick
private val bricksBelow: MutableMap<Brick, Set<Brick>> = mutableMapOf()

private fun List<Int>.toCoordinates(): Pos3D = Triple(this[0], this[1], this[2])

private fun parseBricks(input: List<String>): List<Brick> {
    return input.mapIndexed { index, line ->
        val ints = line.extractAllUnsignedInts()
        Brick(index, ints.subList(0, 3).toCoordinates(), ints.subList(3, 6).toCoordinates())
    }
}

private fun Brick.getXYArea(): List<Pos2D> {
    val start = this.start.pos2D
    val end = this.end.pos2D

    return if (start == end) {
        listOf(start) // Vertical brick
    } else if (start.x != end.x) {
        List(abs(start.x - end.x) + 1) { Pair(it + min(start.x, end.x), start.y) }
    } else {
        List(abs(start.y - end.y) + 1) { Pair(start.x, it + min(start.y, end.y)) }
    }
}

private fun place(brick: Brick) {
    val xyArea = brick.getXYArea()

    val maxHeightBelow = xyArea.maxOf { currentHighest[it]?.first ?: 0 }

    bricksBelow[brick] = xyArea
        .mapNotNull { currentHighest[it] }
        .filter { it.first == maxHeightBelow }
        .map { it.second }
        .toSet()

    val heightOfNewBrick = abs(brick.start.z - brick.end.z) + 1
    val newZ = maxHeightBelow + heightOfNewBrick
    xyArea.forEach { currentHighest[it] = Pair(newZ, brick) }
}

private fun calculateFallingBricks(disintegratedBrick: Brick, bricks: List<Brick>): Int {
    val removed = mutableListOf(disintegratedBrick)

    var changed = true
    while (changed) {
        changed = false
        for (brick in bricks.minus(removed.toSet())) {
            val supports = bricksBelow[brick] ?: emptySet()
            if (supports.isNotEmpty() && supports.all { removed.contains(it) }) {
                removed.add(brick)
                changed = true
            }
        }
    }

    return removed.size - 1
}

private fun part1(input: List<String>): Int {
    currentHighest.clear()
    bricksBelow.clear()

    val bricks = parseBricks(input).sortedBy { min(it.start.z, it.end.z) }

    bricks.forEach(::place)

    return bricks.map { calculateFallingBricks(it, bricks) }.count { it == 0 }
}

private fun part2(input: List<String>): Int {
    currentHighest.clear()
    bricksBelow.clear()

    val bricks = parseBricks(input).sortedBy { min(it.start.z, it.end.z) }

    bricks.forEach(::place)

    return bricks.sumOf { calculateFallingBricks(it, bricks) }
}

fun main() {
    println("Day $DAY")

    println("Testing Part 1...")
    testPart1()
    println("Running Part 1...")
    val part1Time = measureTime {
        runPart1()
    }
    println("Part 1 time: $part1Time")

    println("Testing Part 2...")
    testPart2()
    println("Running Part 2...")
    val part2Time = measureTime {
        runPart2()
    }
    println("Part 2 time: $part2Time")
}

/**
 * //////////////////////// AoC setup code \\\\\\\\\\\\\\\\\\\\\\\\\\\
 */

private fun runPart1() = println(part1(mainInput))

private fun runPart2() = println(part2(mainInput))

private fun testPart1() {
    val result = part1(testInput1)
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