import kotlin.math.abs

private const val DAY = "11"
private const val SOLUTION_TEST_1 = 374
private const val SOLUTION_TEST_2 = 0

private typealias CharGrid = List<List<Char>>

private fun CharGrid.doubleEmptyRows(): CharGrid {
    return this.flatMap { row ->
        if (row.none { it != '.' }) listOf(row, row) else listOf(row)
    }
}

private fun CharGrid.expandEmptySpace(): CharGrid {
    return this.doubleEmptyRows()
        .transpose()
        .doubleEmptyRows()
        .transpose()
}

private fun CharGrid.getAllGalaxyLocations(): List<Pair<Int, Int>> {
    val galaxies = mutableListOf<Pair<Int, Int>>()
    this.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, char ->
            if (char == '#') {
                galaxies.add(Pair(rowIndex, colIndex))
            }
        }
    }
    return galaxies
}

private fun List<Pair<Int, Int>>.computeSumOfShortestPaths(): Int {
    return this.sumOf { g1 ->
        this.sumOf { g2 ->
            abs(g2.first - g1.first) + abs(g2.second - g1.second)
        }
    } / 2 // Counted each distance twice
}

private fun part1(input: List<String>): Int {
    val grid: CharGrid = input.map { it.toList() }

    return grid
        .expandEmptySpace()
        .getAllGalaxyLocations()
        .computeSumOfShortestPaths()
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