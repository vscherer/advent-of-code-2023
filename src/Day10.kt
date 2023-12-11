import Direction.*

private const val DAY = "10"
private const val SOLUTION_TEST_1 = 8
private const val SOLUTION_TEST_2 = 10

private typealias StringGrid = List<String>
private typealias MutableStringGrid = MutableList<String>

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

private fun StringGrid.get(coordinates: Pair<Int, Int>) = this[coordinates.first][coordinates.second]
private fun MutableStringGrid.set(symbol: Char, coordinates: Pair<Int, Int>) {
    val sb = StringBuilder(this[coordinates.first])
    sb.setCharAt(coordinates.second, symbol)
    this[coordinates.first] = sb.toString()
}

private fun StringGrid.padWith(paddingChar: Char): StringGrid {
    val paddedSides = this
        .map { row ->
            row
                .padStart(row.length + 1, paddingChar)
                .padEnd(row.length + 2, paddingChar)
        }
    val emptyRow = "$paddingChar".repeat(paddedSides[0].length)

    return buildList {
        add(emptyRow)
        addAll(paddedSides)
        add(emptyRow)
    }
}

private fun findStart(grid: StringGrid): Pair<Int, Int> {
    grid.forEachIndexed { index, row ->
        val startPosition = row.indexOf('S')
        if (startPosition != -1) return Pair(index, startPosition)
    }

    throw IllegalStateException("No Start in input")
}

private fun findStartDirection(grid: StringGrid, start: Pair<Int, Int>): Direction {
    return when {
        listOf('|', '7', 'F').contains(grid.get(start.moveOne(NORTH))) -> NORTH
        listOf('|', 'J', 'L').contains(grid.get(start.moveOne(SOUTH))) -> SOUTH
        listOf('-', '7', 'J').contains(grid.get(start.moveOne(EAST))) -> EAST
        else -> WEST
    }
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

private fun countPath(grid: StringGrid, start: Pair<Int, Int>): Int {
    var currentDirection = findStartDirection(grid, start)
    var currentPosition = start.moveOne(currentDirection)
    var steps = 1

    while (grid.get(currentPosition) != 'S') {
        currentDirection = findNextDirection(grid.get(currentPosition), currentDirection)
        currentPosition = currentPosition.moveOne(currentDirection)
        steps++
    }

    return steps
}


private fun findStartSymbol(`in`: Direction, out: Direction): Char {
    return when (`in`) {
        NORTH -> {
            when (out) {
                NORTH -> '|'
                EAST -> 'F'
                WEST -> '7'
                else -> throw IllegalStateException()
            }
        }

        SOUTH -> {
            when (out) {
                SOUTH -> '|'
                EAST -> 'L'
                WEST -> 'J'
                else -> throw IllegalStateException()
            }
        }

        EAST -> {
            when (out) {
                NORTH -> 'J'
                SOUTH -> '7'
                EAST -> '-'
                else -> throw IllegalStateException()
            }
        }

        else -> { // WEST
            when (out) {
                NORTH -> 'L'
                SOUTH -> 'F'
                WEST -> '-'
                else -> throw IllegalStateException()
            }
        }
    }
}

private fun paintPath(grid: StringGrid, start: Pair<Int, Int>): StringGrid {
    val gridCopy = MutableList(grid.size) { ".".repeat(grid[0].length) }

    val firstDirection = findStartDirection(grid, start)
    var currentDirection = firstDirection
    var currentPosition = start.moveOne(currentDirection)

    while (grid.get(currentPosition) != 'S') {
        val currentSymbol = grid.get(currentPosition)
        gridCopy.set(currentSymbol, currentPosition)

        currentDirection = findNextDirection(grid.get(currentPosition), currentDirection)
        currentPosition = currentPosition.moveOne(currentDirection)
    }

    gridCopy.set(findStartSymbol(currentDirection, firstDirection), start)

    return gridCopy
}

private fun StringGrid.zoomByThree(): StringGrid {
    return this.flatMap { row ->
        val rowAbove = row
            .replace('|', 'X')
            .replace('L', 'X')
            .replace('J', 'X')
            .map { if (it != 'X') ',' else it }
            .joinToString("") { ",$it," }

        val rowBelow = row
            .replace('|', 'X')
            .replace('F', 'X')
            .replace('7', 'X')
            .map { if (it != 'X') ',' else it }
            .joinToString("") { ",$it," }

        val theRow = row
            .replace("L", ",XX")
            .replace("J", "XX,")
            .replace("F", ",XX")
            .replace("7", "XX,")
            .replace("|", ",X,")
            .replace("-", "XXX")
            .replace(".", ",,,")

        check(rowAbove.length == rowBelow.length && theRow.length == rowAbove.length)
        listOf(rowAbove, theRow, rowBelow)
    }
}

private fun StringGrid.shrinkByThree(): StringGrid {
    return this
        .chunked(3)
        .map { it[1] }
        .map { row ->
            row
                .toCharArray()
                .toList()
                .chunked(3)
                .map { it[1] }
                .joinToString("")
        }
}

private fun dilute(grid: StringGrid): StringGrid {
    return grid.mapIndexed { rowIndex, row ->
        row
            .replace("O,", "OO")
            .replace(",O", "OO")
            .mapIndexed { colIndex, symbol ->
                if (symbol == ',') {
                    if (
                        (rowIndex > 0 && grid.get(Pair(rowIndex - 1, colIndex)) == 'O')
                        || (rowIndex < grid.size - 1 && grid.get(Pair(rowIndex + 1, colIndex)) == 'O')
                    ) 'O' else symbol
                } else {
                    symbol
                }
            }
            .joinToString("")
    }
}

private fun part1(input: List<String>): Int {
    val paddedGrid = input.padWith('.')
    paddedGrid.forEach(::println)
    val start = findStart(paddedGrid)
    println("Start: $start")
    return countPath(paddedGrid, start) / 2
}

private fun part2(input: List<String>): Int {
    val paddedGrid = input.padWith('.')
    println("\nInput grid:")
    paddedGrid.forEach(::println)

    val start = findStart(paddedGrid)
    println("Start: $start")

    val loopOnlyGrid = paintPath(paddedGrid, start)
    println("\nOnly loop grid:")
    loopOnlyGrid.forEach(::println)

    val zoomedGrid = loopOnlyGrid.zoomByThree()
    println("\nZoomed grid:")
    zoomedGrid.forEach(::println)

    var currentGrid = zoomedGrid.padWith('O').padWith('O').padWith('O')
    var dilutedGrid = dilute(currentGrid)

    while (dilutedGrid != currentGrid) {
        currentGrid = dilutedGrid
        dilutedGrid = dilute(currentGrid)
    }
    println("\nMarked grid:")
    currentGrid.forEach(::println)

    val shrunkGrid = currentGrid.shrinkByThree()
    println("\nShrunk grid:")
    shrunkGrid.forEach(::println)

    return shrunkGrid.joinToString("").count { it == ',' }
}

fun main() {
    println("\n PART 1 TEST\n")
    testPart1()
    println("\n PART 1 \n")
    runPart1()

    println("\n PART 2 TEST\n")
    testPart2()
    println("\n PART 2 \n")
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