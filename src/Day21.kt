import utils.*

private const val DAY = "21"
private const val SOLUTION_TEST_1 = 16
private const val SOLUTION_TEST_2 = 16733044L

private fun parseInput(input: List<String>): Pair<Grid<Int>, Pair<Int, Int>> {
    var start = Pair(0, 0)
    val grid = input.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char ->
            when (char) {
                '.' -> 0
                '#' -> -1
                else -> {
                    start = Pair(rowIndex, colIndex)
                    0
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
            if (grid.get(it) == 0) {
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
            print(if (entry == -1) "#" else entry.toString())
        }
        println("")
    }
    println("")
}

private fun part1(input: List<String>, numberOfSteps: Int): Int {
    val (grid, startPosition) = parseInput(input)
    println("Grid: ${grid.dimensions}, start: $startPosition")
    val garden = grid.toMutableGrid()

    fill(garden, startPosition)

    return garden.count { it in 1..numberOfSteps && it % 2 == 0 }
}

private fun part2(input: List<String>, numberOfSteps: Int): Long {
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

private fun runPart1() = println(part1(mainInput, 64))

private fun runPart2() = println(part2(mainInput, 26501365))

private fun testPart1() {
    val result = part1(testInput1, 6)
    check(result == SOLUTION_TEST_1) { "Failed test 1 -> Is: $result, should be: $SOLUTION_TEST_1" }
    println("Test 1 successful!")
}

private fun testPart2() {
    val result = part2(testInput2, 5000)
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