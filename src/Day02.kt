fun main() {

    fun String.extractGameNumber(): Int {
        val pattern = """\d+""".toRegex()
        return pattern.find(this)?.value?.toInt() ?: throw Exception("Incorrectly formatted line: $this")
    }

    fun String.extractSamples(): List<CubeSample> {
        val pattern = """\d* ([rgb])""".toRegex()
        val matches = pattern.findAll(this)
        return matches.map { match ->
            CubeSample(
                color = match.value.last(),
                quantity = match.value.dropLast(2).toInt()
            )
        }.toList()
    }

    fun isValidGame(line: String): Boolean {
        return line
            .extractSamples()
            .none {
                it.quantity > AVAILABLE_CUBES_FOR_PART_ONE[it.color]!!
            }
    }

    fun calculatePower(line: String): Int {
        return line
            .extractSamples()
            .groupBy(
                keySelector = { it.color },
                valueTransform = { it.quantity })
            .values
            .map { it.max() }
            .reduce(Int::times)
    }

    fun part1(input: List<String>): Int {
        return input
            .filter { isValidGame(it) }
            .sumOf { it.extractGameNumber() }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { calculatePower(it) }
    }

    val testInput1 = readInput("Day02_test")
    check(part1(testInput1) == 8)

    val testInput2 = readInput("Day02_test2")
    check(part2(testInput2) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

data class CubeSample(val color: Char, val quantity: Int)

val AVAILABLE_CUBES_FOR_PART_ONE = mapOf(
    'r' to 12,
    'g' to 13,
    'b' to 14,
)