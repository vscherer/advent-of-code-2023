import utils.calculateLCM
import utils.extractAll
import utils.readInput

private const val DAY = "20"
private const val SOLUTION_TEST_1 = 11687500L
private const val SOLUTION_TEST_2 = 0L // no test

private data class Pulse(val isHigh: Boolean, val from: String, val to: String)

private abstract class Module(
    val label: String,
    val outputs: List<String>,
) {
    abstract fun receive(pulse: Pulse): List<Pulse>
}

private class Broadcast(
    label: String,
    outputs: List<String>,
) : Module(label, outputs) {

    override fun receive(pulse: Pulse): List<Pulse> {
        return outputs.map { Pulse(pulse.isHigh, label, it) }
    }
}

private class FlipFlop(
    label: String,
    outputs: List<String>,
) : Module(label, outputs) {

    var state: Boolean = false

    override fun receive(pulse: Pulse): List<Pulse> {
        return if (pulse.isHigh) {
            emptyList()
        } else {
            state = !state
            outputs.map { Pulse(state, label, it) }
        }
    }
}

private class Conjunction(
    label: String,
    outputs: List<String>,
) : Module(label, outputs) {

    var lastInputs = mutableMapOf<String, Boolean>()

    override fun receive(pulse: Pulse): List<Pulse> {
        lastInputs[pulse.from] = pulse.isHigh
        val allHigh = (!lastInputs.values.isEmpty()) && lastInputs.values.all { it }
        return outputs.map { target -> Pulse(!allHigh, label, target) }
    }
}

private fun parseModule(line: String): Module {
    val labels = line.extractAll("""[a-z]+""")

    return when (line.first()) {
        '%' -> FlipFlop(labels.first(), labels.drop(1))
        '&' -> Conjunction(labels.first(), labels.drop(1))
        else -> Broadcast(labels.first(), labels.drop(1))
    }
}

private fun setConjunctionInputs(modules: Map<String, Module>) {
    modules.values.forEach { fromModule ->
        fromModule.outputs.forEach {
            val toModule = modules[it]
            if (toModule is Conjunction) {
                toModule.lastInputs[fromModule.label] = false
            }
        }
    }
}

private fun pressButton(modules: Map<String, Module>): Pair<Int, Int> {
    var countLowPulses = 0
    var countHighPulses = 0

    val queue = ArrayDeque<Pulse>()
    queue.add(Pulse(false, "button", "broadcaster"))

    while (queue.isNotEmpty()) {
        val pulse = queue.removeFirst()
        if (pulse.isHigh) countHighPulses++ else countLowPulses++
        modules[pulse.to]?.let {
            queue.addAll(it.receive(pulse))
        }
    }

    return Pair(countLowPulses, countHighPulses)
}

private fun pressButton1000Times(modules: Map<String, Module>): Pair<Long, Long> {
    var totalLowPulses = 0L
    var totalHighPulses = 0L

    for (i in 1..1000) {
        val (low, high) = pressButton(modules)
        totalLowPulses += low
        totalHighPulses += high
    }

    return Pair(totalLowPulses, totalHighPulses)
}

private fun findCycles(interestingModules: List<String>, modules: Map<String, Module>): List<Int> {
    var numberOfButtonPresses = 0
    val interestingModuleCycles = mutableMapOf<String, Int>()
    val queue = ArrayDeque<Pulse>()

    while (numberOfButtonPresses < 10000) { // Enough for my input, increase if any module didn't hit
        queue.add(Pulse(false, "button", "broadcaster"))
        numberOfButtonPresses++

        while (queue.isNotEmpty()) {
            val pulse = queue.removeFirst()
            val module = pulse.from

            if (interestingModules.contains(module) && pulse.isHigh) {
                if (!interestingModuleCycles.contains(module)) {
                    println("Received high pulse from $module after $numberOfButtonPresses presses")
                    interestingModuleCycles[module] = numberOfButtonPresses
                }
            }

            modules[pulse.to]?.let {
                queue.addAll(it.receive(pulse))
            }
        }
    }

    return interestingModuleCycles.values.toList()
}

private fun part1(input: List<String>): Long {
    val modules = input.map(::parseModule).associateBy(Module::label)
    setConjunctionInputs(modules)

    val (totalLowPulses, totalHighPulses) = pressButton1000Times(modules)

    return totalLowPulses * totalHighPulses
}

private fun part2(input: List<String>): Long {
    val modules = input.map(::parseModule).associateBy(Module::label)
    setConjunctionInputs(modules)

    // This is specific to my input where 'rx' gets input only from one conjunction module 'kz'
    val mainConjunctionModule = modules.values.single { it.outputs.contains("rx") }

    // We want to find out when mainConjunctionModule turns on, so we find the cycles for all of its inputs
    val interestingModules = modules.values.filter { it.outputs.contains(mainConjunctionModule.label) }.map { it.label }
    println("Interesting modules: $interestingModules")

    val cycles = findCycles(interestingModules, modules)

    // mainConjunctionModule turns on when all of its inputs do, which is when their cycles align at the least common multiple
    return cycles.calculateLCM()
}

fun main() {
    testPart1()
    runPart1()

    // no test 2
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