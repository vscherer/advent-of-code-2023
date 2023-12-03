private const val DAY = "03"
private const val SOLUTION_TEST_1 = 4361
private const val SOLUTION_TEST_2 = 467835

data class PartNumber(val value: Int, val line: Int, val range: IntRange)

private lateinit var symbolPositions: MutableList<List<Int>>
private lateinit var numbers: MutableList<PartNumber>
private lateinit var potentialGearPositions: MutableList<List<Int>>

private fun parseInput(input: List<String>) {
    symbolPositions = mutableListOf()
    numbers = mutableListOf()
    potentialGearPositions = mutableListOf()

    input.forEachIndexed { lineNumber, text ->
        symbolPositions.add(
            text
                .findAll("""[^\d.\n]""")
                .map { it.range.first }
                .toList()
        )

        numbers.addAll(
            text
                .findAll("""\d+""")
                .map { PartNumber(it.value.toInt(), lineNumber, it.range) }
        )

        potentialGearPositions.add(
            text
                .findAll("""\*""")
                .map { it.range.first }
                .toList()
        )
    }
}

private fun IntRange.extendedByOne() = IntRange(first - 1, last + 1)

private fun PartNumber.isAdjacentToAnySymbol(): Boolean {
    val searchRange = range.extendedByOne()

    val hasSymbolAbove = if (line > 0) {
        symbolPositions[line - 1].any { it in searchRange }
    } else false
    val hasSymbolOnLine = symbolPositions[line].any { it in searchRange }
    val hasSymbolBelow = if (line < symbolPositions.size - 1) {
        symbolPositions[line + 1].any { it in searchRange }
    } else false

    return hasSymbolAbove || hasSymbolOnLine || hasSymbolBelow
}

private fun PartNumber.isAdjacentToGear(gearPosition: Pair<Int, Int>): Boolean {
    return (gearPosition.first in line - 1..line + 1)
            && (gearPosition.second in range.extendedByOne())
}

private fun part1(input: List<String>): Int {
    parseInput(input)

    return numbers
        .filter { it.isAdjacentToAnySymbol() }
        .sumOf { it.value }
}

private fun part2(input: List<String>): Int {
    parseInput(input)

    val potentialGearPositionsFlattened = potentialGearPositions
        .flatMapIndexed { line, gearsOnLine -> gearsOnLine.map { Pair(line, it) } }

    val adjacentNumbersPerPotentialGear = potentialGearPositionsFlattened
        .map { gearPosition ->
            numbers.filter { it.isAdjacentToGear(gearPosition) }
        }

    val actualGears = adjacentNumbersPerPotentialGear.filter { it.size == 2 }

    return actualGears.sumOf { it.first().value * it.last().value }
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

private fun runPart2() = println(part2(mainInput))

private fun testPart1() {
    val result = part1(testInput1)
    check(result == SOLUTION_TEST_1) { "Failed test 1 -> Is: $result, should be: $SOLUTION_TEST_1" }
}

private fun testPart2() {
    val result = part2(testInput2)
    check(result == SOLUTION_TEST_2) { "Failed test 2 -> Is: $result, should be: $SOLUTION_TEST_2" }
}

private val mainInput: List<String>
    get() = readInput("Day$DAY")

private val testInput1: List<String>
    get() = readInput("Day${DAY}_test")

private val testInput2: List<String>
    get() = readInput("Day${DAY}_test2")