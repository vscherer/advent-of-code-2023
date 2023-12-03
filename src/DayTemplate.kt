private const val DAY = "00"
private const val SOLUTION_TEST_1 = 0
private const val SOLUTION_TEST_2 = 0

private fun part1(input: List<String>): Int {
    return 0
}

private fun part2(input: List<String>): Int {
    return 0
}

fun main() {
    testPart1()
//    runPart1()

//    testPart2()
//    runPart2()
}

private fun runPart1() = part1(mainInput)

private fun runPart2() = part2(mainInput)

private fun testPart1() = check(part1(testInput1) == SOLUTION_TEST_1)

private fun testPart2() = check(part2(testInput2) == SOLUTION_TEST_2)

private val mainInput: List<String>
    get() = readInput("Day$DAY")

private val testInput1: List<String>
    get() = readInput("Day${DAY}_test")

private val testInput2: List<String>
    get() = readInput("Day${DAY}_test2")