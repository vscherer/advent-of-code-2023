private const val DAY = "15"
private const val SOLUTION_TEST_1 = 1320
private const val SOLUTION_TEST_2 = 145

private data class Lens(val label: String, val focalLength: Int)

private val boxes = List<MutableList<Lens>>(256) { mutableListOf() }

private fun hash(s: String) = s.fold(0) { acc, c -> (acc + c.code) * 17 % 256 }

private fun removeLens(label: String, boxNumber: Int) {
    boxes[boxNumber].removeIf { it.label == label }
}

private fun addLens(newLens: Lens, boxNumber: Int) {
    val index = boxes[boxNumber].indexOfFirst { it.label == newLens.label }
    if (index != -1) {
        boxes[boxNumber][index] = newLens
    } else {
        boxes[boxNumber].add(newLens)
    }
}

private fun placeLens(s: String) {
    val label = s.extractFirst("""[a-z]+""")!!
    val operation = s.extractFirst("""[-=]""")!!
    val focalLength = s.extractAllUnsignedInts().singleOrNull()

    val targetBox = hash(label)

    when (operation) {
        "-" -> removeLens(label, targetBox)
        else -> addLens(Lens(label, focalLength!!), targetBox)
    }
}

private fun part1(input: List<String>): Int {
    return input[0].split(",").sumOf(::hash)
}

private fun part2(input: List<String>): Int {
    boxes.forEach { it.clear() }

    input[0]
        .split(",")
        .forEach(::placeLens)

    return boxes
        .flatMapIndexed { boxIndex: Int, lenses: MutableList<Lens> ->
            lenses.mapIndexed { slotIndex, lens -> (1 + boxIndex) * (slotIndex + 1) * lens.focalLength }
        }.sum()
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