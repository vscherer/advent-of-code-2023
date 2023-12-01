fun main() {
    fun extractCalibrationValue(s: String) : Int {
        val digits = s.filter { it.isDigit() }
        return "${digits.first()}${digits.last()}".toInt()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { s -> extractCalibrationValue(s) }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")

//    println(part1(testInput))
    check(part1(testInput) == 142)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
