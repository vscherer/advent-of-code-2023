import utils.*
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime

private const val DAY = "19"
private const val SOLUTION_TEST_1 = 19114
private const val SOLUTION_TEST_2 = 167409079868000L

private enum class Attribute { X, M, A, S }

private data class Criterion(val attribute: Attribute, val acceptedRange: IntRange, val target: String)
private data class WorkFlow(val label: String, val criteria: List<Criterion>, val default: String)
private typealias Part = Map<Attribute, Int>
private typealias PartRange = Map<Attribute, IntRange>

private fun Char.toAttribute(): Attribute {
    return when (this) {
        'x' -> Attribute.X
        'm' -> Attribute.M
        'a' -> Attribute.A
        else -> Attribute.S
    }
}

private val Part.sumOfRatings
    get() = values.sum()

private val PartRange.productOfRatingRanges
    get() = values.map { it.total() }.reduce(Long::times)

private fun IntRange.total(): Long {
    return if (first > 0) last.toLong() - first + 1 else last.toLong()
}

private fun IntRange.unionWith(other: IntRange): IntRange? {
    val newRange = max(this.first, other.first)..min(this.last, other.last)
    return if (newRange.first <= newRange.last) newRange else null
}

private fun IntRange.minus(other: IntRange): List<IntRange> {
    val leftOver = mutableListOf<IntRange>()
    if (first < other.first) {
        leftOver.add(first()..<other.first())
    }

    if (last > other.last) {
        leftOver.add(other.last + 1..last)
    }

    return leftOver
}

private fun parseWorkflows(input: List<String>): Map<String, WorkFlow> {
    return input.associate {
        val label = it.extractFirst("""[a-z]+""")!!
        val criteria = it.extractAll("""[xmas][><]\d+:[a-zAR]+""").map { it.toCriterion() }
        val default = it.extractFirst("""[a-zAR]+}""")!!.dropLast(1)

        label to WorkFlow(label, criteria, default)
    }
}

private fun String.toCriterion(): Criterion {
    val limit = this.extractAllUnsignedInts().first()
    val range = if (this.contains(">")) {
        limit + 1..4000
    } else {
        0..<limit
    }

    return Criterion(
        attribute = this.first().toAttribute(),
        acceptedRange = range,
        target = this.substring(this.indexOf(':') + 1)
    )
}

private fun parseParts(input: List<String>): List<Part> {
    return input.map { line ->
        Attribute.entries
            .zip(line.extractAllUnsignedInts())
            .toMap()
    }
}

private fun Criterion.process(part: Part): String? {
    val partValue = part[attribute]!!
    val match = acceptedRange.contains(partValue)

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

private fun PartRange.splitOn(criterion: Criterion): Pair<PartRange?, PartRange?> {
    val attribute = criterion.attribute
    val currentRange = this[attribute]!!

    val accepted = currentRange.unionWith(criterion.acceptedRange)
    val rejected = currentRange.minus(criterion.acceptedRange).singleOrNull()

    val acceptedPartRange = if (accepted != null) {
        this.toMutableMap()
            .apply { set(attribute, accepted) }
            .toMap()
    } else {
        null
    }

    val rejectedPartRange = if (rejected != null) {
        this.toMutableMap()
            .apply { set(attribute, rejected) }
            .toMap()
    } else {
        null
    }

    return Pair(acceptedPartRange, rejectedPartRange)
}

private fun PartRange.processWith(workFlow: WorkFlow): List<Pair<PartRange, String>> {
    val processedRanges = mutableListOf<Pair<PartRange, String>>()
    val leftOver = workFlow.criteria.fold<Criterion, PartRange?>(this) { range, criterion ->
        if (range != null) {
            val (accepted, rejected) = range.splitOn(criterion)
            if (accepted != null) {
                processedRanges.add(accepted to criterion.target)
            }
            rejected
        } else {
            null
        }
    }
    if (leftOver != null) processedRanges.add(leftOver to workFlow.default)

    return processedRanges
}

private fun findAllPossibleRanges(workFlows: Map<String, WorkFlow>): List<PartRange> {
    val fullRange: PartRange = Attribute.entries.associateWith { 0..4000 }

    val queue = ArrayDeque<Pair<PartRange, String>>()
    queue.add(Pair(fullRange, "in"))

    val acceptedRanges = mutableListOf<PartRange>()
    while (queue.isNotEmpty()) {
        val (nextPartRange, target) = queue.removeFirst()
        when (target) {
            "A" -> acceptedRanges.add(nextPartRange)
            "R" -> {} // rejected, nothing more to do
            else -> queue.addAll(
                nextPartRange.processWith(workFlows[target]!!)
            )
        }
    }

    return acceptedRanges
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
    val inputs = input.splitOnEmptyLine()
    val workFlows = parseWorkflows(inputs[0])
    return findAllPossibleRanges(workFlows).sumOf { it.productOfRatingRanges }
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
    testPart2()
    println("Running Part 2...")
    val part2Time = measureTime {
        runPart2()
    }
    println("Part 2 time: $part2Time")
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