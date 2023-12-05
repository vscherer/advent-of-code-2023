private const val DAY = "05"
private const val SOLUTION_TEST_1 = 35
private const val SOLUTION_TEST_2 = 46

private typealias Seed = Long
private typealias SeedRange = LongRange

private lateinit var seeds: List<Seed> // Part 1
private lateinit var seedRanges: List<SeedRange> // Part 2

// Line "50 98 2" is equal to map entry "[98-99], -48"
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

/**
 * //////////////////////// Input parsing \\\\\\\\\\\\\\\\\\\\\\\\\\\
 */

private fun parseSingleSeeds(input: String) {
    seeds = input.extractAllLongs()
}

private fun parseSeedRanges(input: String) {
    seedRanges = input
        .extractAllLongs()
        .chunked(2)
        .map {
            val start = it.first()
            val end = start + it.last() - 1
            LongRange(start, end)
        }
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
    val range = LongRange(numbers[1], numbers[1] + numbers[2] - 1)
    val conversion = numbers[0] - numbers[1]

    maps[step].add(MapEntry(range, conversion))
}

/**
 * //////////////////////// Range operations \\\\\\\\\\\\\\\\\\\\\\\\\\\
 */

private fun SeedRange.shiftBy(value: Long): SeedRange {
    return SeedRange(first() + value, last() + value)
}

private fun SeedRange.intersect(other: SeedRange): SeedRange {
    return SeedRange(maxOf(first(), other.first), minOf(last(), other.last()))
}

private fun SeedRange.minus(other: SeedRange): List<SeedRange> {
    val leftOver = mutableListOf<SeedRange>()
    if (first < other.first) {
        leftOver.add(SeedRange(first(), other.first() - 1))
    }

    if (last > other.last) {
        leftOver.add(SeedRange(other.last + 1, last))
    }

    return leftOver
}

/**
 * //////////////////////// Seed conversions \\\\\\\\\\\\\\\\\\\\\\\\\\\
 */

private fun Seed.convertWith(map: ConversionMap): Long {
    val conversion = map.find { this in it.inputRange }?.conversion ?: 0
    return this + conversion
}

private fun Seed.convertWithAllMaps() = maps.fold(this) { currentValue, nextMap ->
    currentValue.convertWith(nextMap)
}

private fun List<SeedRange>.convertWith(mapEntry: MapEntry): Pair<List<SeedRange>, List<SeedRange>> {
    val matches = mutableListOf<SeedRange>()
    val noMatch = mutableListOf<SeedRange>()

    forEach { range ->
        val intersection = range.intersect(mapEntry.inputRange)

        if (intersection == LongRange.EMPTY) {
            noMatch.add(range)
        } else {
            matches.add(intersection.shiftBy(mapEntry.conversion))
            noMatch.addAll(range.minus(intersection))
        }
    }

    return Pair(matches, noMatch)
}

private fun List<SeedRange>.convertWith(map: ConversionMap): List<SeedRange> {
    val convertedRanges = mutableListOf<SeedRange>()

    val unprocessed = map.fold(this) { ranges, mapEntry ->
        val (converted, leftToProcess) = ranges.convertWith(mapEntry)
        convertedRanges.addAll(converted)
        leftToProcess
    }

    convertedRanges.addAll(unprocessed)

    // Sanity checks
    convertedRanges.sortBy { it.first }
    convertedRanges.subList(0, convertedRanges.size - 1).forEachIndexed() { index, range ->
        check(range.last < convertedRanges[index + 1].first)
    }

    return convertedRanges
}

private fun convertAll(): List<SeedRange> {
    return maps.fold(seedRanges) { ranges, map ->
        ranges.convertWith(map)
    }
}

private fun part1(input: List<String>): Long {
    parseSingleSeeds(input[0])
    parseMaps(input.subList(3, input.size))

    return seeds.minOf(Seed::convertWithAllMaps)
}

private fun part2(input: List<String>): Long {
    parseSeedRanges(input[0])
    parseMaps(input.subList(3, input.size))

    return convertAll().minOf { it.first }
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
    get() = try {
        readInput("Day${DAY}_test2")
    } catch (_: Exception) {
        testInput1
    }