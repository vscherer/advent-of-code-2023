import utils.CharGrid
import utils.readInput
import utils.transposed
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val DAY = "11"
private const val SOLUTION_TEST_1 = 374L
private const val SOLUTION_TEST_2 = 8410L

private fun CharGrid.doubleEmptyRows(): CharGrid {
    return this.flatMap { row ->
        if (row.none { it != '.' }) listOf(row, row) else listOf(row)
    }
}

private fun CharGrid.findEmptyRowIndices(): List<Int> {
    return this.mapIndexedNotNull { index, row -> if (row.none { it != '.' }) index else null }
}

private fun CharGrid.expandEmptySpace(): CharGrid {
    return this.doubleEmptyRows()
        .transposed()
        .doubleEmptyRows()
        .transposed()
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

private fun shortestPath(a: Pair<Int, Int>, b: Pair<Int, Int>): Int {
    return abs(a.first - b.first) + abs(a.second - b.second)
}

private fun List<Pair<Int, Int>>.computeSumOfShortestPaths(
    emptyRows: List<Int>,
    emptyCols: List<Int>,
    multiplier: Int
): Long {
    return this.sumOf { g1 ->
        this.sumOf { g2 ->
            var shortestPath = shortestPath(g1, g2).toLong()

            val numberOfEmptyRowsBetween = emptyRows.count {
                it in min(g1.first, g2.first)..max(g1.first, g2.first)
            }

            val numberOfEmptyColsBetween = emptyCols.count {
                it in min(g1.second, g2.second)..max(g1.second, g2.second)
            }

            val emptySpace = numberOfEmptyRowsBetween + numberOfEmptyColsBetween

            shortestPath = shortestPath - emptySpace + emptySpace * multiplier

            shortestPath
        }
    } / 2 // Counted each distance twice
}

private fun part1(input: List<String>): Long {
    val grid: CharGrid = input.map { it.toList() }
    val emptySpaceMultiplier = 2

    val emptyRowIndices = grid.findEmptyRowIndices()
    val emptyColIndices = grid.transposed().findEmptyRowIndices()

    return grid
        .getAllGalaxyLocations()
        .computeSumOfShortestPaths(emptyRowIndices, emptyColIndices, emptySpaceMultiplier)
}

private fun part2(input: List<String>, emptySpaceMultiplier: Int): Long {
    val grid: CharGrid = input.map { it.toList() }

    val emptyRowIndices = grid.findEmptyRowIndices()
    val emptyColIndices = grid.transposed().findEmptyRowIndices()

    return grid
        .getAllGalaxyLocations()
        .computeSumOfShortestPaths(emptyRowIndices, emptyColIndices, emptySpaceMultiplier)
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

private fun runPart2() = println(part2(mainInput, 1000000))

private fun testPart1() {
    val result = part1(testInput1)
    check(result == SOLUTION_TEST_1) { "Failed test 1 -> Is: $result, should be: $SOLUTION_TEST_1" }
    println("Test 1 successful!")
}

private fun testPart2() {
    val result = part2(testInput2, 100)
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