package dev.gleroy.ivanachess.core

/**
 * Position.
 *
 * @param col Column index.
 * @param row Row index.
 * @throws IllegalArgumentException If column index or row index is not in valid range.
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
        check(col, "col")
        check(row, "row")
    }

    override fun toString() = "$colLetter$row"

    /**
     * Check if index is in range.
     *
     * @param index Index to check.
     * @param propertyName Property name.
     * @throws IllegalArgumentException If index is not in range.
     */
    @Throws(IllegalArgumentException::class)
    private fun check(index: Int, propertyName: String) {
        if (index < Min || index > Max) {
            throw IllegalArgumentException("$propertyName must be between $Min and $Max")
        }
    }
}
