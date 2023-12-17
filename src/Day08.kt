import utils.calculateLCM
import utils.extractAll
import utils.readInput

private const val DAY = "08"
private const val SOLUTION_TEST_1 = 2
private const val SOLUTION_TEST_1b = 6
private const val SOLUTION_TEST_2 = 6L

private data class Node(val label: String, val left: String, val right: String) {
    fun next(direction: Char) = if (direction == 'L') left else right
}

private val graph = mutableMapOf<String, Node>()
private var path = ""
private val starts = mutableListOf<String>()

private fun parseGraph(input: List<String>) {
    starts.clear()
    graph.clear()

    input.drop(2)
        .map { it.extractAll("""\w+""") }
        .forEach { (from, left, right) ->
            graph[from] = Node(from, left, right)
            if (from.endsWith('A')) {
                starts.add(from)
            }
        }
}

private fun takeStep(from: Node, steps: Int): String {
    val direction = path[steps % path.length]
    return from.next(direction)
}

private fun followPath(start: String): Int {
    var steps = 0
    var currentNode = graph[start]!!

    while (!currentNode.label.endsWith('Z')) {
        currentNode = graph[takeStep(currentNode, steps)]!!
        steps++
    }

    return steps
}

private fun followPathParallel(): Long {
    val distancesToFirstFinish = starts.map { followPath(it) }

    /**
     * Input is special case where this is correct.
     * A general solution would require finding the cycle for each ghost
     * and then lining them up.
     */
    return distancesToFirstFinish.calculateLCM()
}

private fun part1(input: List<String>): Int {
    parseGraph(input)
    path = input[0]

    return followPath("AAA")
}

private fun part2(input: List<String>): Long {
    parseGraph(input)
    path = input[0]

    return followPathParallel()
}

fun main() {
    testPart1()
    testPart1b()
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

private fun testPart1b() {
    val result = part1(testInput1b)
    check(result == SOLUTION_TEST_1b) { "Failed test 1_2 -> Is: $result, should be: $SOLUTION_TEST_1b" }
    println("Test 1b successful!")
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

private val testInput1b: List<String>
    get() = readInput("Day${DAY}_testb")

private val testInput2: List<String>
    get() = try {
        readInput("Day${DAY}_test2")
    } catch (_: Exception) {
        println("Using test input from part 1 to test part 2")
        testInput1
    }