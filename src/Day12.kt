import utils.extractAllUnsignedInts
import utils.readInput

private const val DAY = "12"
private const val SOLUTION_TEST_1 = 21
private const val SOLUTION_TEST_2 = 525152L

private val cache = mutableMapOf<Pair<String, Int>, Long>()

private fun parse(line: String): Pair<String, List<Int>> {
    val parts = line.split(" ")
    return Pair(parts[0], parts[1].extractAllUnsignedInts())
}

private fun fits(arrangement: String, pattern: String): Boolean {
    if (arrangement.length != pattern.length) return false

    return arrangement.zip(pattern).all { (c1, c2) -> c1 == c2 || c2 == '?' }
}

fun countArrangements(pattern: String, groups: List<Int>): Long {

    // Base cases
    if (pattern.isEmpty()) {
        return if (groups.isEmpty()) 1 else 0
    }

    if (groups.isEmpty()) {
        return if (pattern.last() == '#') 0 else countArrangements(pattern.dropLast(1), groups)
    }

    val minLengthRequired = groups.reduce { a, b -> a + b + 1 }
    if (minLengthRequired > pattern.length) {
        return 0
    }

    // Recursive case with cache
    return cache.getOrPut(Pair(pattern, groups.size)) {
        when (pattern.last()) {
            '.' -> countArrangements(pattern.dropLast(1), groups)
            '#' -> {
                var patternForLastGroup = "#".repeat(groups.last())

                if (groups.count() > 1) {
                    patternForLastGroup = ".$patternForLastGroup" // Add the '.' separating the last group from the rest
                }

                val size = patternForLastGroup.length

                if (fits(patternForLastGroup, pattern.takeLast(size))) {
                    countArrangements(pattern.dropLast(size), groups.dropLast(1))
                } else {
                    0
                }
            }

            else -> { // Symbol is '?'
                (countArrangements(pattern.dropLast(1) + ".", groups)
                        + countArrangements(pattern.dropLast(1) + "#", groups))
            }
        }
    }
}

private fun part1(input: List<String>): Int {
    return input
        .map(::parse)
        .sumOf { (pattern, groups) ->
            cache.clear()
            countArrangements(pattern, groups)
        }
        .toInt()
}

private fun part2(input: List<String>): Long {
    return input
        .map(::parse)
        .map { (pattern, groups) ->
            Pair(
                List(5) { pattern }.joinToString("?"),
                List(5) { groups }.flatten(),
            )
        }
        .sumOf { (pattern, groups) ->
            cache.clear()
            countArrangements(pattern, groups)
        }
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