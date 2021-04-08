package dev.gleroy.ivanachess.game

/**
 * Position.
 *
 * @param col Column index.
 * @param row Row index.
 * @throws IllegalArgumentException If column index or row index is out of range.
 */
data class Position(
    val col: Int,
    val row: Int
) {
    companion object {
        /**
         * Minimal value.
         */
        const val Min = 1

        /**
         * Maximal value.
         */
        const val Max = 8

        /**
         * Coordinates regex.
         */
        internal val CoordinatesRegex = Regex("^([A-H])([1-8])$")

        /**
         * Get all positions.
         *
         * @return All positions.
         */
        fun all() = (Min..Max).flatMap { col -> (Min..Max).map { Position(col, it) } }.toSet()

        /**
         * Instantiate position from coordinates string (like A8).
         *
         * @param coordinates Coordinates.
         * @return Position.
         * @throws IllegalArgumentException If coordinates string is invalid.
         */
        @Throws(IllegalArgumentException::class)
        fun fromCoordinates(coordinates: String): Position {
            val matcher = CoordinatesRegex.matchEntire(coordinates)
                ?: throw IllegalArgumentException("coordinates must match ${CoordinatesRegex.pattern}")
            val col = matcher.groups[1]!!.value[0] - 'A' + 1
            val row = matcher.groups[2]!!.value.toInt()
            return Position(col, row)
        }
    }

    init {
        checkIndex(col, "col")
        checkIndex(row, "row")
    }

    /**
     * Get position relative to this one.
     *
     * @param colOffset Column offset.
     * @param rowOffset Row offset.
     * @return Position or null if it out of range.
     */
    @Throws(IllegalArgumentException::class)
    fun relativePosition(colOffset: Int = 0, rowOffset: Int = 0): Position? {
        val col = col + colOffset
        val row = row + rowOffset
        return if (col < Min || col > Max || row < Min || row > Max) {
            null
        } else {
            Position(col, row)
        }
    }

    override fun toString() = "${'A' + (col - 1)}$row"

    /**
     * Check if index is in range.
     *
     * @param index Index to check.
     * @param propertyName Property name.
     * @throws IllegalArgumentException If index is out of range.
     */
    @Throws(IllegalArgumentException::class)
    private fun checkIndex(index: Int, propertyName: String) {
        if (index < Min || index > Max) {
            throw IllegalArgumentException("$propertyName must be between $Min and $Max")
        }
    }
}
