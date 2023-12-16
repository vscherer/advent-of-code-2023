import kotlin.math.max

private const val DAY = "16"
private const val SOLUTION_TEST_1 = 46
private const val SOLUTION_TEST_2 = 51

private lateinit var mirrors: CharGrid
private lateinit var beams: Grid<Energy>
private val queue = ArrayDeque<Pair<Int, Int>>()


data class Energy(
    var up: Boolean = false,
    var down: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false,
) {
    var changed: Boolean = false

    val isEnergized
        get() = up || down || left || right

    val anyVertical: Boolean
        get() = up || down

    val anyHorizontal: Boolean
        get() = left || right

    fun addUp() {
        changed = !up
        up = true
    }

    fun addDown() {
        changed = !down
        down = true
    }

    fun addLeft() {
        changed = !left
        left = true
    }

    fun addRight() {
        changed = !right
        right = true
    }

    fun add(e: Energy) {
        if (e.up) addUp()
        if (e.down) addDown()
        if (e.left) addLeft()
        if (e.right) addRight()
    }
}

private fun resetBeams() {
    beams = createGrid(mirrors.size, mirrors[0].size) { Energy() }
}

private fun update(location: Pair<Int, Int>) {
    val symbol = mirrors.get(location)
    val inEnergy = beams.get(location)
    inEnergy.changed = false

    val outEnergy = when (symbol) {
        '/' -> Energy(inEnergy.right, inEnergy.left, inEnergy.down, inEnergy.up)
        '\\' -> Energy(inEnergy.left, inEnergy.right, inEnergy.up, inEnergy.down)
        '-' -> Energy(left = inEnergy.left || inEnergy.anyVertical, right = inEnergy.right || inEnergy.anyVertical)
        '|' -> Energy(up = inEnergy.up || inEnergy.anyHorizontal, down = inEnergy.down || inEnergy.anyHorizontal)
        else -> inEnergy
    }

    if (outEnergy.up && location.first > 0) {
        val above = Pair(location.first - 1, location.second)
        val beamsAbove = beams.get(above)
        beamsAbove.addUp()
        if (beamsAbove.changed) queue.add(above)
    }

    if (outEnergy.down && location.first < beams.size - 1) {
        val below = Pair(location.first + 1, location.second)
        val beamsBelow = beams.get(below)
        beamsBelow.addDown()
        if (beamsBelow.changed) queue.add(below)
    }

    if (outEnergy.left && location.second > 0) {
        val left = Pair(location.first, location.second - 1)
        val beamsLeft = beams.get(left)
        beamsLeft.addLeft()
        if (beamsLeft.changed) queue.add(left)
    }

    if (outEnergy.right && location.second < beams[0].size - 1) {
        val right = Pair(location.first, location.second + 1)
        val beamsRight = beams.get(right)
        beamsRight.addRight()
        if (beamsRight.changed) queue.add(right)
    }
}

private fun Grid<Energy>.countEnergized() = this.flatMap { row -> row.map { it.isEnergized } }.count { it }

private fun updateBeamsUntilConstant() {
    while (queue.isNotEmpty()) {
        update(queue.removeFirst())
    }
}

private fun tryBeam(inputLocation: Pair<Int, Int>, energy: Energy): Int {
    resetBeams()
    beams.get(inputLocation).add(energy)
    queue.add(inputLocation)

    updateBeamsUntilConstant()

    return beams.countEnergized()
}

private fun tryAllPossibleBeams(): Int {
    val bestHorizontal = mirrors.indices.maxOf { row ->
        max(
            tryBeam(Pair(row, 0), Energy(right = true)),
            tryBeam(Pair(row, mirrors.numberOfColumns - 1), Energy(left = true)),
        )
    }

    val bestVertical = mirrors[0].indices.maxOf { column ->
        max(
            tryBeam(Pair(0, column), Energy(down = true)),
            tryBeam(Pair(mirrors.numberOfColumns - 1, column), Energy(up = true)),
        )
    }

    return max(bestHorizontal, bestVertical)
}

private fun part1(input: List<String>): Int {
    mirrors = input.toCharGrid()
    return tryBeam(Pair(0, 0), Energy(right = true))
}

private fun part2(input: List<String>): Int {
    mirrors = input.toCharGrid()
    return tryAllPossibleBeams()
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