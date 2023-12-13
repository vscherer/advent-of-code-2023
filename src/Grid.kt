typealias Grid<T> = List<List<T>>
typealias CharGrid = Grid<Char>

/**
 * From: https://stackoverflow.com/a/76533918
 */
fun <T> Grid<T>.transpose(): Grid<T> {
    return (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] } }
}

fun <T> Grid<T>.print() = map { it.joinToString("") }.forEach(::println)