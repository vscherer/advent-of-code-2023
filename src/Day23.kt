import utils.*
import utils.GridDirection.EAST
import utils.GridDirection.SOUTH
import kotlin.math.max

private const val DAY = "23"
private const val SOLUTION_TEST_1 = 94
private const val SOLUTION_TEST_2 = 0

private typealias Location = Pair<Int, Int>
private typealias Graph = MutableMap<Location, MutableList<Path>>

private data class Path(val to: Location, val length: Int)

private fun Graph.add(from: Location, path: Path) {
    if (this.contains(from)) {
        this[from]!!.add(path)
    } else {
        this[from] = mutableListOf(path)
    }
}

private fun followPath(grid: CharGrid, start: Location, direction: GridDirection): Path {
    var currentDirection = direction
    var currentLocation = start + direction.getStep() + direction.getStep() // Skip over the arrow
    var length = 2
    val end = Pair(grid.numberOfRows - 1, grid.numberOfColumns - 2)

    while (true) {
        if (currentLocation == end) {
            return Path(end, length)
        }

//        println("at $currentLocation, looking $currentDirection, at ${grid.get(currentLocation + currentDirection.getStep())}, length: $length")

        when (grid.get(currentLocation + currentDirection.getStep())) {
            '#' -> {
                for (direction in GridDirection.entries) {
                    if (direction != currentDirection
                        && direction != currentDirection.opposite
                        && grid.get(currentLocation + direction.getStep()) != '#'
                    ) {
                        currentDirection = direction
                        break
                    }
                }
            }

            'v' -> {
                val target = currentLocation + currentDirection.getStep() + SOUTH.getStep()
//                println("Found path from $start: ${Path(target, length + 2)}")
                return Path(target, length + 2)
            }

            '>' -> {
                val target = currentLocation + currentDirection.getStep() + EAST.getStep()
//                println("Found path from $start: ${Path(target, length + 2)}")
                return Path(target, length + 2)
            }

            else -> { // '.'
                length++
                currentLocation += currentDirection.getStep()
            }
        }
    }
}

private fun parseGraph(grid: CharGrid): Pair<Graph, Path> {
    println("Grid: ${grid.dimensions}")
    val graph = mutableMapOf<Location, MutableList<Path>>()

    val start = Pair(-1, 1)
    val end = Pair(grid.numberOfRows - 1, grid.numberOfColumns - 2)
    val startPath = followPath(grid, start, SOUTH)

    val queue = ArrayDeque<Location>()
    queue.add(startPath.to)

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()

        if (graph.contains(node)) continue // Already visited

        val paths = buildList {
            if (grid.get(node + SOUTH.getStep()) == 'v') {
                add(followPath(grid, node, SOUTH))
            }
            if (grid.get(node + EAST.getStep()) == '>') {
                add(followPath(grid, node, EAST))
            }
            // No ^ or < in my input
        }

        paths.forEach { path ->
            graph.add(node, path)
            if (path.to != end) {
                queue.add(path.to)
            }
        }
    }

    return Pair(graph, startPath)
}

private fun topologicalSort(graph: Graph, start: Location): List<Location> {
    val sorted = mutableSetOf(start)
    val toSort = graph.flatMap { it.value.map { it.to } }.toMutableSet()
    val allNodes = listOf(sorted, toSort).flatten().toSet()

    val inConnections = allNodes.associateWith { to ->
        graph.entries.filter { it.value.map { path -> path.to }.contains(to) }
            .map { it.key }
    }

    while (toSort.isNotEmpty()) {
//        println("Sorting... $sorted")
        val sizeBefore = sorted.size
        val canAdd = toSort.filter { to: Location ->
            sorted.containsAll(inConnections[to] ?: emptyList())
        }.toSet()

        sorted.addAll(canAdd)
        toSort.removeAll(canAdd)

        val sizeAfter = sorted.size
        check(sizeBefore != sizeAfter) { "Not sortable!" }
    }

    return sorted.toList()
}

private fun part1(input: List<String>): Int {
    val grid = input.toCharGrid()
    val end = Pair(grid.numberOfRows - 1, grid.numberOfColumns - 2)

    val (graph, startPath) = parseGraph(grid)

    val sortedNodes = topologicalSort(graph, startPath.to)
    println(sortedNodes)

    val longestPathTo = mutableMapOf(startPath.to to startPath.length)
    sortedNodes.forEach { from ->
        graph[from]?.forEach {
            longestPathTo[it.to] = max(longestPathTo[it.to] ?: 0, it.length + longestPathTo[from]!!)
        }
    }

    return longestPathTo[end]!! - 1
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