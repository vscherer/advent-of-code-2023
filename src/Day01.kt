import utils.println
import utils.readInput

fun main() {

    // Only replace first char to allow overlap
    val spelledOutReplacements = mapOf(
        "one" to "1ne",
        "two" to "2wo",
        "three" to "3hree",
        "four" to "4our",
        "five" to "5ive",
        "six" to "6ix",
        "seven" to "7even",
        "eight" to "8ight",
        "nine" to "9ine",
    )

    fun String.filterDigits() = filter { it.isDigit() }

    fun String.replaceSpelledOutDigits(): String {
        return this.fold("") { processed, nextChar ->
            var result = "$processed$nextChar"
            for ((pattern, replacement) in spelledOutReplacements) {
                if (pattern in result) {
                    result = result.replace(pattern, replacement)
                }
            }
            result
        }
    }

    fun extractCalibrationValue(s: String): Int {
        val onlyDigits = s.filterDigits()
        return "${onlyDigits.first()}${onlyDigits.last()}".toInt()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { s -> extractCalibrationValue(s) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { s -> extractCalibrationValue(s.replaceSpelledOutDigits()) }
    }

    val testInput1 = readInput("Day01_test")
    check(part1(testInput1) == 142)

    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 299) // Added one testcase: "xyz1threeight" -> 18

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
