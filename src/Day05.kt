private const val DAY = "05"
private const val SOLUTION_TEST_1 = 35
private const val SOLUTION_TEST_2 = 0

private typealias Seed = Long

private lateinit var seeds: List<Seed>

// Line "5 10 5" is equal to map entry "[5-9], +5"
private data class MapEntry(val inputRange: LongRange, val conversion: Long)

private typealias ConversionMap = MutableList<MapEntry>

/**
 * 0 to soil
 * 1 to fertilizer
 * 2 to water
 * 3 to light
 * 4 to temperature
 * 5 to humidity
 * 6 to location
 */
private lateinit var maps: List<ConversionMap>

private fun Seed.convertWithAllMaps() = maps.fold(this) { currentValue, nextMap ->
    currentValue.convertWith(nextMap)
}

private fun Seed.convertWith(map: ConversionMap): Long {
    val conversion = map.find { this in it.inputRange }?.conversion ?: 0
    return this + conversion
}

private fun parseInput(input: List<String>) {
    parseSeeds(input[0])
    parseMaps(input.subList(3, input.size))
}

private fun parseSeeds(input: String) {
    seeds = input.extractAllLongs()
}

private fun parseMaps(input: List<String>) {
    maps = List(7) { mutableListOf() }

    var step = 0
    for (line in input) {
        when {
            line.isBlank() -> continue
            !line[0].isDigit() -> step++
            else -> parseMapEntry(step, line)
        }
    }

    input[0].split("")
}

private fun parseMapEntry(step: Int, line: String) {
    val numbers = line.extractAllLongs()
    val range = LongRange(numbers[1], numbers[1] + numbers[2])
    val conversion = numbers[0] - numbers[1]

    maps[step].add(MapEntry(range, conversion))
}

private fun part1(input: List<String>): Long {
    parseInput(input)

    return seeds.minOf(Seed::convertWithAllMaps)
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
    check(result == SOLUTION_TEST_1.toLong()) { "Failed test 1 -> Is: $result, should be: $SOLUTION_TEST_1" }
}

private fun testPart2() {
    val result = part2(testInput2)
    check(result == SOLUTION_TEST_2.toLong()) { "Failed test 2 -> Is: $result, should be: $SOLUTION_TEST_2" }
}

private val mainInput: List<String>
    get() = readInput("Day$DAY")

private val testInput1: List<String>
    get() = readInput("Day${DAY}_test")

private val testInput2: List<String>
    get() = readInput("Day${DAY}_test2")