package dev.gleroy.ivanachess.core

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
    }

    /**
     * Column letter.
     */
    val colLetter = 'A' + (col - 1)

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

    override fun toString() = "$colLetter$row"

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
