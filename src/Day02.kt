import java.lang.Integer.max

fun main() {

    val availableCubes = mapOf(
        'r' to 12,
        'g' to 13,
        'b' to 14,
    )

    fun String.getGameNumber(): Int {
        val pattern = """\d+""".toRegex()
        return pattern.find(this)?.value?.toInt()?: throw Exception("Incorrectly formatted line: $this")
    }

    fun getSamples(line: String) : List<Pair<Int, Char>> {
        println("Checking: $line")
        val pattern = """\d* ([rgb])""".toRegex()
        val matches = pattern.findAll(line)
        return matches.map { match ->
            match.value.dropLast(2).toInt() to match.value.last()
        }.toList()
    }

    fun isValidGame(samples: List<Pair<Int, Char>>): Boolean {
        samples.forEach { (quantity, color) ->
            if (quantity > availableCubes[color]!!) {
                println("Nope: $quantity $color too high")
                return false
            }
        }
        println("possible")
        return true
    }

    fun calculatePower(samples: List<Pair<Int, Char>>): Int {
        val necessaryDice = samples.groupingBy{ it.second }.fold (0) { accumulator, element ->
            max(accumulator, element.first)
        }
        println("Max: $necessaryDice")
        return necessaryDice.values.reduce { a, b -> a*b }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val samples = getSamples(line)
            if (isValidGame(samples)) {
                println("adding: ${line.getGameNumber()}")
                line.getGameNumber()
            } else {
                0
            }
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {line ->
            val samples = getSamples(line)
            calculatePower(samples)
        }
    }

    val testInput1 = readInput("Day02_test")
    check(part1(testInput1) == 8)

    val testInput2 = readInput("Day02_test2")
    check(part2(testInput2) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
