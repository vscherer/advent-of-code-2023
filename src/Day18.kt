import utils.GridDirection
import utils.plus
import utils.readInput
import utils.times
import kotlin.math.max
import kotlin.math.min

private const val DAY = "18"
private const val SOLUTION_TEST_1 = 62
private const val SOLUTION_TEST_2 = 0

private data class DigInstruction(val direction: GridDirection, val distance: Int /*, val color: Color*/)

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
            DigInstruction(it[0].toDirection(), it[1].toInt() /*, it[2].toColor()*/)
        }
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

private fun calculateArea(corners: List<Pair<Int, Int>>): Int {
    var sortedCorners = corners
        .pushedToPositive()
        .sortedWith(compareBy({ it.first }, { it.second }))

    var totalArea = 0
    val currentRangesInside = mutableListOf<IntRange>()
    while (sortedCorners.isNotEmpty()) {
        val row = sortedCorners.first().first
        println("On row: $row")
        println("Current ranges: $currentRangesInside")
        var areaOfThisLine = currentRangesInside.sumOf { it.areaInsideInclusive() }

        val rangesOnThisRow = sortedCorners.filter { it.first == row }
            .chunked(2)
            .map { (start, end) -> start.second..end.second }

        println("Ranges on here: $rangesOnThisRow")

        rangesOnThisRow.forEach { range ->
            println("Checking $range against $currentRangesInside")
            val matchingRanges =
                currentRangesInside.filter { it.containsInclusive(range.first) || it.containsInclusive(range.last) }
            println("Range $range matches $matchingRanges")

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
                        if (hitRange.containsInclusive(newRange.first) && hitRange.containsInclusive(newRange.last)) {
                            println("smaller by ${range.areaInside() + 1}")
                        } else {
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
        println("Area of this line: $areaOfThisLine")


        if (sortedCorners.isNotEmpty()) {
            println("Area after this line ${currentRangesInside.sumOf { it.areaInsideInclusive() }} for ${(sortedCorners.first().first - row - 1)} lines is ${currentRangesInside.sumOf { it.areaInsideInclusive() } * (sortedCorners.first().first - row - 1)}")
            totalArea += currentRangesInside.sumOf { it.areaInsideInclusive() } * (sortedCorners.first().first - row - 1)

        }
        println("Area now: $totalArea")

    }
    return totalArea
}

private fun part1(input: List<String>): Int {
    val corners = parseInput(input).getCorners()
//    println("$corners")
    return calculateArea(corners)
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