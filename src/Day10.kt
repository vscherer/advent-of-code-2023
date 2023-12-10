import Direction.*

private const val DAY = "10"
private const val SOLUTION_TEST_1 = 8
private const val SOLUTION_TEST_2 = 0

private typealias Grid = List<String>

private enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST,
}

private fun Pair<Int, Int>.moveOne(dir: Direction): Pair<Int, Int> {
    return when (dir) {
        NORTH -> Pair(first - 1, second)
        EAST -> Pair(first, second + 1)
        SOUTH -> Pair(first + 1, second)
        WEST -> Pair(first, second - 1)
    }
}

private fun Grid.get(coordinates: Pair<Int, Int>) = this[coordinates.first][coordinates.second]

private fun Grid.pad(): Grid {
    val paddedSides = this
        .map { row ->
            row
                .padStart(row.length + 1, '.')
                .padEnd(row.length + 2, '.')
        }
    val emptyRow = ".".repeat(paddedSides[0].length)

    return buildList {
        add(emptyRow)
        addAll(paddedSides)
        add(emptyRow)
    }
}

private fun findStart(grid: Grid): Pair<Int, Int> {
    grid.forEachIndexed { index, row ->
        val startPosition = row.indexOf('S')
        if (startPosition != -1) return Pair(index, startPosition)
    }

    throw IllegalStateException("No Start in input")
}

/**
 * Assumes previous connection is valid
 */
private fun findNextDirection(symbol: Char, direction: Direction): Direction {
    return when (symbol) {
        '|' -> direction
        '-' -> direction
        'F' -> if (direction == NORTH) EAST else SOUTH
        '7' -> if (direction == NORTH) WEST else SOUTH
        'J' -> if (direction == SOUTH) WEST else NORTH
        'L' -> if (direction == SOUTH) EAST else NORTH
        else -> throw IllegalStateException("Unknown Symbol")
    }
}

private fun followPath(grid: Grid, start: Pair<Int, Int>): Int {
    grid.forEach(::println)
    var currentDirection = when {
        listOf('|', '7', 'F').contains(grid.get(start.moveOne(NORTH))) -> NORTH
        listOf('|', 'J', 'L').contains(grid.get(start.moveOne(SOUTH))) -> SOUTH
        listOf('-', '7', 'J').contains(grid.get(start.moveOne(EAST))) -> EAST
        else -> WEST
    }
    var currentPosition = start.moveOne(currentDirection)
    println("Moving $currentDirection to $currentPosition")
    var steps = 1

    while (grid.get(currentPosition) != 'S') {
        currentDirection = findNextDirection(grid.get(currentPosition), currentDirection)
        currentPosition = currentPosition.moveOne(currentDirection)
        steps++
        println("Moving $currentDirection to $currentPosition")
    }

    return steps
}

private fun part1(input: List<String>): Int {
    val paddedGrid = input.pad()
    val start = findStart(paddedGrid)
    println("Start: $start")
    return followPath(paddedGrid, start) / 2
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