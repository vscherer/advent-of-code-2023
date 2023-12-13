import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun String.findFirst(regex: String) = regex.toRegex().find(this)

fun String.findAll(regex: String) = regex.toRegex().findAll(this)

fun String.extractFirst(regex: String) = regex.toRegex().find(this)?.value

fun String.extractAll(regex: String) = regex.toRegex().findAll(this).map { it.value }.toList()

fun String.extractAllUnsignedInts(): List<Int> = extractAll("""\d+""").map(String::toInt)

fun String.extractAllSignedInts(): List<Int> = extractAll("""-?\d+""").map(String::toInt)

fun String.extractAllUnsignedLongs(): List<Long> = extractAll("""\d+""").map(String::toLong)

fun String.extractAllSignedLongs(): List<Long> = extractAll("""-?\d+""").map(String::toLong)

// Adapted from baeldung.com/kotlin/lcm
fun lcm(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

// Adapted from baeldung.com/kotlin/lcm
fun List<Int>.calculateLCM(): Long {
    var result = this[0].toLong()
    for (i in 1 until size) {
        result = lcm(result, this[i].toLong())
    }
    return result
}

fun List<String>.splitOnEmptyLine(): List<List<String>> {
    val result = mutableListOf<List<String>>()
    var subList = mutableListOf<String>()

    forEach { line ->
        if (line.isBlank()) {
            result.add(subList)
            subList = mutableListOf()
        } else {
            subList.add(line)
        }
    }

    result.add(subList)
    return result
}

fun List<Char>.asString() = joinToString("")