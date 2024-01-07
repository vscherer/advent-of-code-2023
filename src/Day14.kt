import utils.*
import kotlin.time.measureTime

private const val DAY = "14"
private const val SOLUTION_TEST_1 = 136
private const val SOLUTION_TEST_2 = 64

private fun MutableCharGrid.tiltGrid() = forEach { it.tilt() }

/**
 * Move all round rocks as far toward the start as possible, in-place
 */
private fun MutableList<Char>.tilt() {
    var nextFreeSpace = 0

    this.forEachIndexed { index, symbol ->
        when (symbol) {
            '#' -> nextFreeSpace = index + 1
            'O' -> {
                this.swap(index, nextFreeSpace)
                nextFreeSpace++
            }

            else -> {} // '.' case, do nothing
        }
    }
}

private fun CharGrid.calculateLoad() = sumOf(::calculateRowLoad)

/**
 * Calculate load toward the start
 */
private fun calculateRowLoad(list: List<Char>): Int {
    var load = 0
    list.forEachIndexed { index, symbol ->
        if (symbol == 'O') {
            load += list.size - index
        }
    }
    return load
}

private fun MutableCharGrid.spinnedXTimes(cycles: Int): CharGrid {
    val afterNCycles = mutableListOf(copy())
    var loopLength = 0
    var preLoop = 0

    for (i in 1..cycles) {
        spinOnce()
        val lastSeenAt = afterNCycles.indexOfFirst { it == copy() }
        if (lastSeenAt != -1) {
            loopLength = i - lastSeenAt
            preLoop = lastSeenAt
            break
        } else {
            afterNCycles.add(copy())
        }
    }

    val targetPosition = ((cycles - preLoop) % loopLength) + preLoop
    return afterNCycles[targetPosition]
}

/**
 * Don't ask
 */
private fun MutableCharGrid.spinOnce() {
    tiltGrid() // NORTH
    transpose()
    tiltGrid() // WEST
    reverse()
    transpose()
    tiltGrid() // SOUTH
    reverse()
    transpose()
    tiltGrid() // EAST
    reverse()
    transpose()
    reverse()
}

private fun part1(input: List<String>): Int {
    with(input.toMutableCharGrid()) {
        transpose() // Orient north toward start
        tiltGrid()
        return calculateLoad()
    }
}

private fun part2(input: List<String>): Int {
    val grid = input.toMutableCharGrid().apply {
        transpose() // Orient north toward start
    }

    return grid
        .spinnedXTimes(1000000000)
        .calculateLoad()
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