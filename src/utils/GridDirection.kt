package utils

enum class GridDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    val opposite
        get() = when (this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }

    fun getStep(): Pair<Int, Int> {
        return when (this) {
            NORTH -> Pair(-1, 0)
            EAST -> Pair(0, 1)
            SOUTH -> Pair(1, 0)
            WEST -> Pair(0, -1)
        }
    }
}