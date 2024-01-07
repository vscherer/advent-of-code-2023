import utils.*
import kotlin.math.max
import kotlin.time.measureTime

private const val DAY = "21"
private const val SOLUTION_TEST_1 = 16

private const val BLOCKED_TILE = -1
private const val EMPTY_TILE = -2

private fun parseInput(input: List<String>): Pair<Grid<Int>, Pair<Int, Int>> {
    var start = Pair(0, 0)
    val grid = input.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char ->
            when (char) {
                '.' -> EMPTY_TILE
                '#' -> BLOCKED_TILE
                else -> {
                    start = Pair(rowIndex, colIndex)
                    EMPTY_TILE
                } // Start
            }
        }
    }
    return Pair(grid, start)
}

private fun Pair<Int, Int>.connect4(): List<Pair<Int, Int>> {
    return listOf(
        this + GridDirection.NORTH.getStep(),
        this + GridDirection.SOUTH.getStep(),
        this + GridDirection.EAST.getStep(),
        this + GridDirection.WEST.getStep(),
    )
}

private fun spread(grid: MutableGrid<Int>, position: Pair<Int, Int>): Boolean {
    val stepsToHere = grid.get(position)
    var anyChange = false
    position.connect4()
        .filter { grid.contains(it) } // No out of bounds
        .forEach {
            if (grid.get(it) == EMPTY_TILE) {
                grid.set(it, stepsToHere + 1)
                anyChange = true
            }
        }

    return anyChange
}

private fun step(grid: MutableGrid<Int>, steps: Int): Boolean {
    var anyChange = false
    grid.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, value ->
            if (value == steps) {
                anyChange = spread(grid, Pair(rowIndex, colIndex)) || anyChange
            }
        }
    }
    return anyChange
}

private fun fill(grid: MutableGrid<Int>, startPosition: Pair<Int, Int>) {
    grid.set(startPosition, 0)
    spread(grid, startPosition)

    var anyChange = true
    var steps = 1
    while (anyChange) {
        anyChange = step(grid, steps)
        steps++
    }
}

private fun Grid<Int>.printGarden() {
    this.forEach { row ->
        row.forEach { entry ->
            when {
                entry == -1 -> print("## ")
                entry < 10 -> print("0$entry ")
                else -> print("$entry ")
            }
        }
        println("")
    }
    println("")
}

private fun bruteForce(grid: MutableGrid<Int>, numberOfSteps: Int): Long {
    val stepsMod2 = numberOfSteps % 2
    val center = Pair(grid.size / 2, grid.size / 2)
    fill(grid, center)
    return grid.count { it in 0..numberOfSteps && it % 2 == stepsMod2 }.toLong()
}

private fun part1(input: List<String>, numberOfSteps: Int): Int {
    val (grid, startPosition) = parseInput(input)
    println("Grid: ${grid.dimensions}, start: $startPosition")
    val garden = grid.toMutableGrid()
    return bruteForce(garden, numberOfSteps).toInt()
}

/**
 * Test input does not fulfill conditions on actual input mentioned below.
 * Generating new test data using part 1.
 */
private fun part2TestData(input: List<String>, numberOfSteps: Int): Long {
    val (grid, _) = parseInput(input)
    val bigGrid = grid.repeat(3, 3).toMutableGrid()
    return bruteForce(bigGrid, numberOfSteps)
}

/**
 * Input observations:
 * A. S is dead center of the input tile, which is a square.
 * B. Positions in the + directions from S are empty.
 * C. The input tile has an empty border.
 * D. There is a visible empty pattern connecting the centers of all edges diagonally.
 *
 * Conclusions:
 * 1. (A, B) tiles in the + directions are always reached first in the middle of the edge facing the start.
 * 2. (A, B, C) All other tiles are always reached first in the corner closest to the start.
 * 3. (C) All other border tiles are then reached in minimal time within the tile itself. No other path can be faster.
 * (D) seems irrelevant, because going diagonal in a grid is not faster than going one direction first, then the other.
 *
 * DISCLAIMER: The code below assumes A-C hold and some other things, so it should not be treated as a general solution!
 *
 * LATE NIGHT UPDATE: Don't even try to read this. It only works on my specific input and I will not clean this up.
 */

private fun part2(input: List<String>, numberOfSteps: Long): Long {
    val (immutableGrid, center) = parseInput(input)
    val grid = immutableGrid.toMutableGrid()

    val length = grid.size // X
    val startToEdge = (grid.size - 1) / 2 // x

    var total = 0L

    // Start tile
    val fromCenter = grid.copy().toMutableGrid()
    total += bruteForce(fromCenter, numberOfSteps.toInt())

    // Straight direction tiles (up, right, down, left)
    val straightGrids = List(4) { grid.copy().toMutableGrid() }

    val bottomEntry = Pair(length - 1, center.second)
    val leftEntry = Pair(center.first, 0)
    val topEntry = Pair(0, center.second)
    val rightEntry = Pair(center.first, length - 1)
    val straightStarts = listOf(bottomEntry, leftEntry, topEntry, rightEntry)

    straightGrids.zip(straightStarts).forEach { (grid, start) -> fill(grid, start) }
    val reachableStraightEven = straightGrids.map { grid ->
        grid.count { it >= 0 && it % 2 == 0 }
    }
    val reachableStraightOdd = straightGrids.map { grid ->
        grid.count { it >= 0 && it % 2 == 1 }
    }
    val straightNeededToFill = straightGrids.map { grid ->
        grid.fold(0) { acc, value -> max(acc, value) }
    }

    val stepsAtFirstStraightTile = numberOfSteps - startToEdge - 1
    val stepsLeftLastStraightTile = stepsAtFirstStraightTile % length
    val reachableLastTile = straightGrids.map { grid ->
        grid.count { it in 0..stepsLeftLastStraightTile && it.toLong() % 2 == stepsLeftLastStraightTile % 2 }
    }

    // Last straight tiles
    total += reachableLastTile.sum()

    // Full straight tiles
    val fullTilesStraight = stepsAtFirstStraightTile / length
    val oddTilesStraight = fullTilesStraight / 2 + (fullTilesStraight % 2)
    val evenTilesStraight = fullTilesStraight / 2
    val fullStraightTotal = reachableStraightOdd.sum() * oddTilesStraight +
            reachableStraightEven.sum() * evenTilesStraight
    total += fullStraightTotal


    // Diagonal direction tiles (up-right, right-down, down-left, left-up)
    val diagonalGrids = List(4) { grid.copy().toMutableGrid() }

    val bottomLeftEntry = Pair(length - 1, 0)
    val leftTopEntry = Pair(0, 0)
    val topRightEntry = Pair(0, length - 1)
    val rightBottomEntry = Pair(length - 1, length - 1)
    val diagonalStarts = listOf(bottomLeftEntry, leftTopEntry, topRightEntry, rightBottomEntry)

    diagonalGrids.zip(diagonalStarts).forEach { (grid, start) -> fill(grid, start) }
    val reachableDiagonalOdd = diagonalGrids.map { grid ->
        grid.count { it >= 0 && it % 2 == 1 }
    }
    val reachableDiagonalEven = diagonalGrids.map { grid ->
        grid.count { it >= 0 && it % 2 == 0 }
    }
    val diagonalNeededToFill = diagonalGrids.map { grid ->
        grid.fold(0) { acc, value -> max(acc, value) }
    }

    // Tiles at (1,1), (2,2), (1,3), (3,1), (3,3), etc.
    val stepsAtFirstDiagonalTile = numberOfSteps - (length + 1)
    val stepsLeftLastDiagonalTile = stepsAtFirstDiagonalTile % (2 * length)
    val reachableLastDiagonalTile = diagonalGrids.map { grid ->
        grid.count { it in 0..stepsLeftLastDiagonalTile && it % 2 == stepsLeftLastDiagonalTile.toInt() % 2 }
    }

    // Tiles at (2,1), (1,2), (2,3), (3,2) etc.
    val stepsAtFirstDiagonal2Tile = numberOfSteps - (2 * length + 1)
    val stepsLeftLastDiagonal2Tile = stepsAtFirstDiagonal2Tile % (2 * length)
    val reachableLastDiagonal2Tile = diagonalGrids.map { grid ->
        grid.count { it in 0..stepsLeftLastDiagonal2Tile && it % 2 == stepsLeftLastDiagonal2Tile.toInt() % 2 }
    }

    // Last diagonal tiles
    total += reachableLastDiagonalTile.sum() * fullTilesStraight
    total += reachableLastDiagonal2Tile.sum() * (fullTilesStraight + 1)

    // total + completeDiagonal * 4
    val fullTilesDiagonal = List(fullTilesStraight.toInt()) { it.toLong() / 2 }.sum()
    val fullTilesDiagonal2 = List(fullTilesStraight.toInt() + 1) { it.toLong() / 2 }.sum()

    val fulldiagonalTotal = reachableDiagonalOdd.sum() * fullTilesDiagonal
    total += fulldiagonalTotal

    val fulldiagonal2Total = reachableDiagonalEven.sum() * fullTilesDiagonal2
    total += fulldiagonal2Total

    return total
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
    testPart2(11)
    println("Running Part 2...")
    val part2Time = measureTime {
        runPart2()
    }
    println("Part 2 time: $part2Time")
}

/**
 * //////////////////////// AoC setup code \\\\\\\\\\\\\\\\\\\\\\\\\\\
 */

private fun runPart1() = println(part1(mainInput, 64))

private fun runPart2() = println(part2(mainInput, 26501365L))

private fun testPart1() {
    val result = part1(testInput1, 6)
    check(result == SOLUTION_TEST_1) { "Failed test 1 -> Is: $result, should be: $SOLUTION_TEST_1" }
    println("Test 1 successful!")
}

private fun testPart2(steps: Int) {
    val total = part2TestData(testInput2, steps)
    println("Brute force total is: $total")
    val result = part2(testInput2, steps.toLong())
    check(result == total) { "Failed test 2 -> Is: $result, should be: $total" }
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