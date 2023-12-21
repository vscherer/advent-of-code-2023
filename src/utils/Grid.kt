package utils

typealias Grid<T> = List<List<T>>
typealias MutableGrid<T> = MutableList<MutableList<T>>
typealias CharGrid = Grid<Char>
typealias MutableCharGrid = MutableGrid<Char>


// Constructors

fun <T> createGrid(rows: Int, columns: Int, initialValue: T): Grid<T> = List(rows) { List(columns) { initialValue } }

fun <T> createGrid(rows: Int, columns: Int, initialValueGenerator: () -> T): Grid<T> =
    List(rows) { List(columns) { initialValueGenerator() } }

fun <T> Grid<T>.copy(): Grid<T> = this.map { row -> row.map { it } }


// Dimensions

val <T> Grid<T>.dimensions
    get() = Pair(this.numberOfRows, this.numberOfColumns)

val <T> Grid<T>.numberOfRows
    get() = this.size

val <T> Grid<T>.numberOfColumns
    get() = this[0].size

fun <T> Grid<T>.contains(location: Pair<Int, Int>) =
    location.first in 0..<numberOfRows && location.second in 0..<numberOfColumns


// Access

fun <T> Grid<T>.get(coordinates: Pair<Int, Int>) = this[coordinates.first][coordinates.second]

fun <T> MutableGrid<T>.set(coordinates: Pair<Int, Int>, value: T) {
    this[coordinates.first][coordinates.second] = value
}

fun <T> Grid<T>.count(predicate: (T) -> Boolean) = sumOf { row -> row.count(predicate) }

fun <T, R> Grid<T>.fold(initial: R, operation: (R, T) -> R) =
    fold(initial) { acc, row: List<T> -> row.fold(acc, operation) }


// Casting

fun <T> Grid<T>.toMutableGrid(): MutableGrid<T> = this.map { it.toMutableList() }.toMutableList()

fun List<String>.toCharGrid(): CharGrid = map { it.toList() }
fun List<String>.toMutableCharGrid(): MutableCharGrid = map { it.toList() }.toMutableGrid()


// Matrix operations

/**
 * From: https://stackoverflow.com/a/76533918
 */
fun <T> Grid<T>.transposed(): Grid<T> {
    return (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] } }
}

fun <T> MutableGrid<T>.transpose() {
    val transposed = transposed().toMutableGrid()
    this.clear()
    this.addAll(transposed)
}

fun <T> Grid<T>.repeat(vertical: Int, horizontal: Int): Grid<T> {
    val wider = this.map { row ->
        List(horizontal) { row }.flatten()
    }
    return List(vertical) { wider }.flatten()
}

// Misc

fun <T> Grid<T>.print() = map { it.joinToString("") }.forEach(::println)
