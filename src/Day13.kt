import kotlin.math.min

private const val DAY = "13"
private const val SOLUTION_TEST_1 = 405
private const val SOLUTION_TEST_2 = 400

private fun findHorizontalMirrorLine(grid: CharGrid, numberOfSmudges: Int): Int? {
    for (index in 0..grid.size - 2) {
        val before = grid.take(index + 1)
        val after = grid.takeLast(grid.size - (index + 1))

        val length = min(before.size, after.size)
        val beforeCut = before.takeLast(length).flatten()
        val afterCut = after.take(length).reversed().flatten()

        val differences = beforeCut.zip(afterCut).count { (c1, c2) -> c1 != c2 }
        if (differences == numberOfSmudges) return index
    }

    return null
}

private fun findMirrorLine(grid: CharGrid, numberOfSmudges: Int): Pair<Int, Int> {
    findHorizontalMirrorLine(grid, numberOfSmudges)?.let {
        return Pair(it + 1, 0)
    }

    findHorizontalMirrorLine(grid.transposed(), numberOfSmudges)?.let {
        return Pair(0, it + 1)
    }

    throw IllegalStateException("No mirror line for grid:\n${grid.forEach(::println)}")
}

private fun part1(input: List<String>): Int {
    return input
        .splitOnEmptyLine()
        .map { it.toCharGrid() }
        .map { findMirrorLine(it, 0) }
        .sumOf { (row, col) ->
            if (row != 0) row * 100 else col
        }
}

private fun part2(input: List<String>): Int {
    return input
        .splitOnEmptyLine()
        .map { it.toCharGrid() }
        .map { findMirrorLine(it, 1) }
        .sumOf { (row, col) ->
            if (row != 0) row * 100 else col
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