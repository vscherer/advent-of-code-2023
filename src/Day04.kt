import kotlin.math.pow

private const val DAY = "04"
private const val SOLUTION_TEST_1 = 13
private const val SOLUTION_TEST_2 = 0

private data class Card(val winningNumbers: Set<Int>, val myNumbers: Set<Int>)

private fun parseInput(line: String): Card {
    val (winning, mine) = line
        .substring(8) // Remove Card number
        .split(" | ")
        .map { it.extractAllInts() }
        .map(List<Int>::toSet)

    return Card(winning, mine)
}

private fun calculatePoints(card: Card): Int {
    val hits = card.myNumbers.intersect(card.winningNumbers).count()
    return 2.toDouble().pow(hits - 1).toInt()
}

private fun part1(input: List<String>): Int {
    return input
        .map { parseInput(it) }
        .sumOf { calculatePoints(it) }
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
}

private fun testPart2() {
    val result = part2(testInput2)
    check(result == SOLUTION_TEST_2) { "Failed test 2 -> Is: $result, should be: $SOLUTION_TEST_2" }
}

private val mainInput: List<String>
    get() = readInput("Day$DAY")

private val testInput1: List<String>
    get() = readInput("Day${DAY}_test")

private val testInput2: List<String>
    get() = readInput("Day${DAY}_test2")