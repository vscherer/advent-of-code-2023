import utils.*
import kotlin.math.min
import kotlin.time.measureTime

private const val DAY = "17"
private const val SOLUTION_TEST_1 = 102
private const val SOLUTION_TEST_2 = 94

private data class QueueEntry(val location: Pair<Int, Int>, val comingFrom: GridDirection)

private lateinit var stableCrucibleRange: IntRange
private lateinit var grid: Grid<Int>
private val queue: MutableMap<QueueEntry, Int> = mutableMapOf()
private val done: MutableMap<QueueEntry, Boolean> = mutableMapOf()

/**
 * Turns this map into a (slow!) queue, sorted by value
 */
private fun MutableMap<QueueEntry, Int>.poll(): Pair<QueueEntry, Int> {
    val next = this.toList().minByOrNull { it.second }!!
    this.remove(next.first)
    return next
}

private fun addToQueueIfNotDoneAlready(entry: QueueEntry, heatLoss: Int) {
    if (done[entry] != true) {
        val previousBest = queue[entry] ?: Int.MAX_VALUE
        queue[entry] = min(previousBest, heatLoss)
    }
}

private fun spread(location: Pair<Int, Int>, direction: GridDirection, currentHeatLoss: Int) {
    var newHeatLoss = 0
    for (distance in 1..stableCrucibleRange.last) {
        val targetLocation = location + (direction.getStep() * distance)
        if (!grid.contains(targetLocation)) break

        newHeatLoss += grid.get(targetLocation)

        if (distance in stableCrucibleRange) {
            addToQueueIfNotDoneAlready(
                QueueEntry(targetLocation, comingFrom = direction.opposite),
                currentHeatLoss + newHeatLoss
            )
        }
    }
}

private fun addAllOptionsFrom(entry: QueueEntry, heatLoss: Int) {
    val possibleNextDirections = GridDirection.entries.filter {
        it != entry.comingFrom && it != entry.comingFrom.opposite
    }

    possibleNextDirections.forEach { direction ->
        spread(entry.location, direction, heatLoss)
    }
}

private fun customDijkstra(): Int {
    queue.clear()
    done.clear()

    val start = Pair(0, 0)
    val end = Pair(grid.numberOfRows - 1, grid.numberOfColumns - 1)

    queue[QueueEntry(start, GridDirection.NORTH)] = 0
    queue[QueueEntry(start, GridDirection.EAST)] = 0

    val shortestPaths = mutableListOf<Int>()
    while (queue.isNotEmpty()) {
        val nextEntry = queue.poll()
        done[nextEntry.first] = true

        if (nextEntry.first.location == end) {
            shortestPaths.add(nextEntry.second)
        } else {
            addAllOptionsFrom(nextEntry.first, nextEntry.second)
        }
    }

    return shortestPaths.min()
}

private fun part1(input: List<String>): Int {
    grid = input.map { row -> row.map { it.toString().toInt() } }
    stableCrucibleRange = 1..3

    return customDijkstra()
}

private fun part2(input: List<String>): Int {
    grid = input.map { row -> row.map { it.toString().toInt() } }
    stableCrucibleRange = 4..10

    return customDijkstra()
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