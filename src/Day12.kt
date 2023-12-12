private const val DAY = "12"
private const val SOLUTION_TEST_1 = 21
private const val SOLUTION_TEST_2 = 525152L

private fun parse(line: String): Pair<String, List<Int>> {
    val parts = line.split(" ")
    return Pair(parts[0], parts[1].extractAllUnsignedInts())
}

private fun fits(arrangement: String, pattern: String): Boolean {
    check(arrangement.length == pattern.length) { "Unequal lengths: ${arrangement.length}, ${pattern.length}" }
    val fits = arrangement.zip(pattern).all { (c1, c2) -> c1 == c2 || c2 == '?' }
    return fits
}

fun countArrangements(pattern: String, groups: List<Int>): Int {
    val length = pattern.length
    val minNecessaryLength = groups.sumOf { it + 1 } - 1 // 1 gap for each group past the first

    if (minNecessaryLength > length) return 0

    val startPositions = 0..length - minNecessaryLength
    val nextGroup = groups[0]
    val remainingGroups = groups.drop(1)

    var count = 0
    for (start in startPositions) {
        var arrangement = ".".repeat(start) + "#".repeat(nextGroup)

        arrangement += if (remainingGroups.isNotEmpty()) {
            "."
        } else {
            ".".repeat(pattern.length - arrangement.length)
        }

        if (fits(arrangement, pattern.substring(0, arrangement.length))) {
            if (remainingGroups.isNotEmpty()) {
                count += countArrangements(pattern.substring(startIndex = arrangement.length), remainingGroups)
            } else {
                count++
            }
        }
    }
    return count
}

private fun part1(input: List<String>): Int {
    return input
        .map(::parse)
        .sumOf { (pattern, groups) ->
            countArrangements(pattern, groups)
        }
}

private fun part2(input: List<String>): Long {
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