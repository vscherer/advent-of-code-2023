import utils.extractAllUnsignedInts
import utils.readInput
import kotlin.math.pow
import kotlin.time.measureTime

private const val DAY = "04"
private const val SOLUTION_TEST_1 = 13
private const val SOLUTION_TEST_2 = 30

private data class Card(val winningNumbers: Set<Int>, val myNumbers: Set<Int>)

private fun parseInput(line: String): Card {
    val (winning, mine) = line
        .substring(8) // Remove Card number
        .split(" | ")
        .map { it.extractAllUnsignedInts() }
        .map(List<Int>::toSet)

    return Card(winning, mine)
}

private fun Card.calculateHits() = myNumbers.intersect(winningNumbers).count()

private fun Card.calculatePoints(): Int {
    val hits = calculateHits()
    return 2.toDouble().pow(hits - 1).toInt()
}

private fun part1(input: List<String>): Int {
    return input
        .map(::parseInput)
        .sumOf(Card::calculatePoints)
}

private fun part2(input: List<String>): Int {
    val copiesPerCard = MutableList(input.size) { 1 }

    val hitsPerCard = input
        .map(::parseInput)
        .map(Card::calculateHits)

    hitsPerCard.forEachIndexed { index, hitsOfThisCard ->
        val copiesOfThisCard = copiesPerCard[index]

        if (hitsOfThisCard > 0) {
            for (i in index + 1..index + hitsOfThisCard) {
                copiesPerCard[i] += copiesOfThisCard
            }
        }
    }

    return copiesPerCard.sum()
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
    get() = readInput("Day${DAY}_test2")