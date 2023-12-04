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

fun String.extractAllInts(): List<Int> = extractAll("""\d+""").map(String::toInt)