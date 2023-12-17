import utils.extractAllSignedLongs
import utils.readInput

private const val DAY = "09"
private const val SOLUTION_TEST_1 = 114L
private const val SOLUTION_TEST_2 = 2L

private fun parseInput(input: List<String>) = input.map { it.extractAllSignedLongs() }

private fun buildRows(sequence: List<Long>): List<List<Long>> {
    val rows = mutableListOf(sequence)

    while (rows.last().sum() != 0L) {
        rows.add(rows.last().zipWithNext { a, b -> b - a })
    }

    return rows
}

private fun calculateNext(sequence: List<Long>): Long {
    return buildRows(sequence).sumOf { it.last() }
}

private fun calculatePrevious(sequence: List<Long>): Long {
    return buildRows(sequence)
        .dropLast(1)
        .reversed()
        .fold(0) { acc, row ->
            row[0] - acc
        }
}

private fun part1(input: List<String>): Long {
    val sequences = parseInput(input)
    return sequences.sumOf { calculateNext(it) }
}

private fun part2(input: List<String>): Long {
    val sequences = parseInput(input)
    return sequences.sumOf { calculatePrevious(it) }
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