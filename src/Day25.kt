import utils.extractAll
import utils.readInput
import kotlin.time.measureTime

private const val DAY = "25"
private const val SOLUTION_TEST_1 = 54
private const val SOLUTION_TEST_2 = 0

typealias Edge = Pair<String, String>

typealias KragerNode = Set<String>
typealias KragerEdge = Pair<KragerNode, KragerNode>

private fun parseGraph(input: List<String>): List<Edge> {
    return buildList {
        input.forEach { line ->
            val nodes = line.extractAll("""[a-z]+""")
            nodes.drop(1).forEach { target ->
                add(Edge(nodes[0], target))
            }
        }
    }
}

private fun MutableList<KragerEdge>.removeEdge(e: KragerEdge) = removeAll {
    it == e || it == (KragerEdge(e.second, e.first))
}

private fun contract(nodes: Set<String>, edges: List<Edge>): Triple<Int, Int, Int> { // Returns: mincut, size1, size2
    val newNodes: MutableSet<KragerNode> = nodes.map { setOf(it) }.toMutableSet()
    val newEdges: MutableList<KragerEdge> = edges.map { Pair(setOf(it.first), setOf(it.second)) }.toMutableList()

    while (newNodes.size > 2) {
        val randomEdge = newEdges.random()
        newEdges.removeEdge(randomEdge)
        newNodes.remove(randomEdge.first)
        newNodes.remove(randomEdge.second)

        val combinedNode = listOf(randomEdge.first, randomEdge.second).flatten().toSet()
        newNodes.add(combinedNode)
        val affectedEdges = newEdges.filter { it.first in randomEdge.toList() || it.second in randomEdge.toList() }
        newEdges.removeAll(affectedEdges)
        val newTargets = affectedEdges.mapNotNull {
            when {
                it.first in randomEdge.toList() -> it.second
                it.second in randomEdge.toList() -> it.first
                else -> null
            }
        }
        val updatedEdges = newTargets.map { KragerEdge(combinedNode, it) }
        newEdges.addAll(updatedEdges)
    }

    val group1 = newNodes.first()
    val group2 = newNodes.last()
    return Triple(newEdges.size, group1.size, group2.size)
}

// Pretty inefficient, but it worked
fun karger(graph: List<Edge>): Int {
    val allNodes = graph.flatMap { it.toList() }.toSet()

    var attempt = Triple(0, 0, 0)
    while (attempt.first != 3) {
        attempt = contract(allNodes, graph)
    }

    return attempt.second * attempt.third
}

private fun part1(input: List<String>): Int {
    return karger(parseGraph(input))
}

private fun part2(input: List<String>): Int {
    return 0
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

    println("No part 2")
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