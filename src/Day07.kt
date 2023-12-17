import utils.readInput

private const val DAY = "07"
private const val SOLUTION_TEST_1 = 6440
private const val SOLUTION_TEST_2 = 5905

private data class Hand(val cards: String, val bid: Int) {
    companion object {
        val HandComparator = Comparator<Hand> { h1, h2 ->
            if (h1.type == h2.type) {
                val cardPairs = (h1.cards.toCharArray() zip h2.cards.toCharArray())
                cardPairs.map { (c1, c2) ->
                    valueOf(c1) - valueOf(c2)
                }.first {
                    it != 0
                }
            } else {
                h1.type.value - h2.type.value
            }
        }
    }
}

private enum class Type(val value: Int) {
    FIVE(50),
    FOUR(40),
    FULL_HOUSE(32),
    THREE(30),
    TWO_PAIRS(22),
    TWO(20),
    ONE(10),
}

private var hasJokers: Boolean = false

private val Hand.type: Type
    get() {
        val nrOfJokers = if (hasJokers) this.cards.count { it == 'J' } else 0
        val cards = this.cards.filter { !hasJokers || it != 'J' }

        var counts = cards
            .groupingBy { it }
            .eachCount()
            .values
            .sortedDescending()
            .toMutableList()

        if (counts.size > 0) {
            counts[0] += nrOfJokers
        } else { // Only jokers
            counts = mutableListOf(nrOfJokers)
        }

        val max = counts.max()
        return when (max) {
            5 -> Type.FIVE
            4 -> Type.FOUR
            3 -> if (counts.contains(2)) Type.FULL_HOUSE else Type.THREE
            2 -> if (counts.count { it == 2 } == 2) Type.TWO_PAIRS else Type.TWO
            else -> Type.ONE
        }
    }

private fun valueOf(card: Char): Int = when (card) {
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'J' -> if (hasJokers) 1 else 11
    'T' -> 10
    else -> card.digitToInt()
}


private fun parseInput(input: List<String>): List<Hand> {
    return input.map { line ->
        val parts = line.split(" ")
        Hand(parts.first(), parts.last().toInt())
    }
}

private fun List<Hand>.computeWinnings(): Int {
    return sortedWith(comparator = Hand.HandComparator)
        .foldIndexed(0) { index, sum, hand ->
            sum + hand.bid * (index + 1)
        }
}

private fun part1(input: List<String>): Int {
    return parseInput(input).computeWinnings()
}

private fun part2(input: List<String>): Int {
    hasJokers = true
    return parseInput(input).computeWinnings()
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
    get() = try {
        readInput("Day${DAY}_test2")
    } catch (_: Exception) {
        testInput1
    }