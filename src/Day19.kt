import utils.*

private const val DAY = "19"
private const val SOLUTION_TEST_1 = 19114
private const val SOLUTION_TEST_2 = 167409079868000L

private data class Criterion(val attribute: Char, val isLowerBound: Boolean, val limit: Int, val target: String)
private data class WorkFlow(val label: String, val criteria: List<Criterion>, val default: String)
private data class Part(val x: Int, val m: Int, val a: Int, val s: Int)

private val Part.sumOfRatings
    get() = x + m + a + s

private fun String.toCriterion(): Criterion {
    return Criterion(
        attribute = this.first(),
        isLowerBound = this.contains(">"),
        limit = this.extractAllUnsignedInts().first(),
        target = this.substring(this.indexOf(':') + 1)
    )
}

private fun parseWorkflows(input: List<String>): Map<String, WorkFlow> {
    return input.associate {
        val label = it.extractFirst("""[a-z]+""")!!
        val criteria = it.extractAll("""[xmas][><]\d+:[a-zAR]+""").map { it.toCriterion() }
        val default = it.extractFirst("""[a-zAR]+}""")!!.dropLast(1)

        label to WorkFlow(label, criteria, default)
    }
}

private fun parseParts(input: List<String>): List<Part> {
    return input.map { line ->
        line.extractAllUnsignedInts()
            .let { Part(it[0], it[1], it[2], it[3]) }
    }
}

private fun Criterion.process(part: Part): String? {
    val partValue = when (attribute) {
        'x' -> part.x
        'm' -> part.m
        'a' -> part.a
        's' -> part.s
        else -> throw IllegalStateException("Wrong criterion: $this")
    }

    val match = if (isLowerBound) {
        partValue > limit
    } else {
        partValue < limit
    }

    return if (match) target else null
}

private fun WorkFlow.process(part: Part): String {
    criteria.forEach {
        val next = it.process(part)
        if (next != null) return next
    }
    return default
}

private fun checkForAcceptance(part: Part, workFlows: Map<String, WorkFlow>): Boolean {
    var currentState = "in"

    while (true) {
        when (currentState) {
            "A" -> return true
            "R" -> return false
            else -> currentState = workFlows[currentState]!!.process(part)
        }
    }
}

private fun part1(input: List<String>): Int {
    val inputs = input.splitOnEmptyLine()
    val workFlows = parseWorkflows(inputs[0])
    val parts = parseParts(inputs[1])

    return parts
        .filter { checkForAcceptance(it, workFlows) }
        .sumOf { it.sumOfRatings }
}

private fun part2(input: List<String>): Long {
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