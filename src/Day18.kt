import utils.GridDirection
import utils.plus
import utils.readInput
import utils.times
import kotlin.math.max
import kotlin.math.min

private const val DAY = "18"
private const val SOLUTION_TEST_1 = 62
private const val SOLUTION_TEST_2 = 952408144115L

private data class DigInstruction(val direction: GridDirection, val distance: Int)

private fun String.toDirection(): GridDirection {
    return when (this[0]) {
        'U' -> GridDirection.NORTH
        'D' -> GridDirection.SOUTH
        'R' -> GridDirection.EAST
        else -> GridDirection.WEST
    }
}

private fun parseInput(input: List<String>): List<DigInstruction> {
    return input
        .map { it.split(" ") }
        .map {
            DigInstruction(it[0].toDirection(), it[1].toInt())
        }
}

private fun Char.toDirection2(): GridDirection {
    return when (this) {
        '3' -> GridDirection.NORTH
        '1' -> GridDirection.SOUTH
        '0' -> GridDirection.EAST
        else -> GridDirection.WEST
    }
}

private fun parseInput2(input: List<String>): List<DigInstruction> {
    return input
        .map { it.split(" ").last() }
        .map { it.drop(1).dropLast(1) } // drop ( )
        .map { DigInstruction(it.last().toDirection2(), Integer.valueOf(it.substring(1, 6), 16)) }
}

private fun List<DigInstruction>.getCorners(): List<Pair<Int, Int>> {
    val corners = mutableListOf<Pair<Int, Int>>()

    this.fold(Pair(-5, -5)) { acc, instruction ->
        corners.add(acc)
        acc + instruction.direction.getStep() * instruction.distance
    }

    return corners
}

private fun List<Pair<Int, Int>>.pushedToPositive(): List<Pair<Int, Int>> {
    val minY = this.minOf { it.first }
    val minX = this.minOf { it.second }

    return this.map { Pair(it.first - minY, it.second - minX) }
}

private fun IntRange.minus(other: IntRange): List<IntRange> {
    val leftOver = mutableListOf<IntRange>()
    if (first < other.first) {
        leftOver.add(first()..other.first())
    }

    if (last > other.last) {
        leftOver.add(other.last..last)
    }

    return leftOver
}

private fun updateRangeWithNewRange(existingRange: IntRange, newRange: IntRange): List<IntRange> {
    return if (existingRange.containsInclusive(newRange.first) && existingRange.containsInclusive(newRange.last)) {
        existingRange.minus(newRange)
    } else {
        listOf(min(existingRange.first, newRange.first)..max(existingRange.last, newRange.last))
    }
}

private fun IntRange.areaInside() = last - first - 1

private fun IntRange.areaInsideInclusive() = last - first + 1

private fun IntRange.containsInclusive(x: Int) = x in first..last + 1

/**
 * Too late in the evening to clean this up, so here's the gist:
 * Perform scanline algorithm across the entire lagoon.
 *
 * To calculate the area, we keep track of the ranges currently on our scanline and update that
 * whenever we hit an interesting row (any row that contains at least one corner):
 * - Update the ranges
 * - Increase the total area by the area on this row
 * - Increase the total area by the area of the next row, times the distance to the next corner/row
 */
private fun calculateArea(corners: List<Pair<Int, Int>>): Long {
    var sortedCorners = corners
        .pushedToPositive()
        .sortedWith(compareBy({ it.first }, { it.second }))

    println("Corners:")
    sortedCorners.forEach(::println)

    var totalArea = 0L
    val currentRangesInside = mutableListOf<IntRange>()
    while (sortedCorners.isNotEmpty()) {
        val row = sortedCorners.first().first
        println("On row: $row")
        var areaOfThisLine = currentRangesInside.sumOf { it.areaInsideInclusive() }.toLong()

        val rangesOnThisRow = sortedCorners.filter { it.first == row }
            .chunked(2)
            .map { (start, end) -> start.second..end.second }


        rangesOnThisRow.forEach { range ->
            val matchingRanges = currentRangesInside
                .filter { it.containsInclusive(range.first) || it.containsInclusive(range.last) }

            when (matchingRanges.size) {
                0 -> {
                    currentRangesInside.add(range)
                    areaOfThisLine += range.areaInsideInclusive()
                }

                1 -> {
                    val hitRange = matchingRanges.single()
                    currentRangesInside.remove(hitRange)
                    val rangesToAdd = updateRangeWithNewRange(hitRange, range)
                    currentRangesInside.addAll(rangesToAdd)

                    if (rangesToAdd.size == 1) {
                        val newRange = rangesToAdd.single()
                        if (!(hitRange.containsInclusive(newRange.first) && hitRange.containsInclusive(newRange.last))) {
                            areaOfThisLine += range.areaInside() + 1
                        }
                    }
                }

                2 -> {
                    currentRangesInside.removeAll(matchingRanges)
                    val newlyJoinedRanges = matchingRanges.union(listOf(range))
                    currentRangesInside.add(
                        newlyJoinedRanges.minOf { it.first }..newlyJoinedRanges.maxOf { it.last }
                    )
                    areaOfThisLine += range.areaInside()
                }
            }
        }

        sortedCorners = sortedCorners.drop(rangesOnThisRow.size * 2)
        totalArea += areaOfThisLine

        if (sortedCorners.isNotEmpty()) {
            val areaOnLineAfterThisLine = currentRangesInside
                .sumOf { it.areaInsideInclusive().toLong() }

            totalArea += areaOnLineAfterThisLine * (sortedCorners.first().first - row - 1)
        }
    }

    return totalArea
}

private fun part1(input: List<String>): Int {
    val corners = parseInput(input).getCorners()
    return calculateArea(corners).toInt()
}

private fun part2(input: List<String>): Long {
    val corners = parseInput2(input).getCorners()

    return calculateArea(corners)
}

fun main() {
    testPart1()
    runPart1()

    testPart2()
    runPart2()
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