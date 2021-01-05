package dev.gleroy.ivanachess.core

/**
 * Position.
 *
 * @param col Column number.
 * @param row Row number.
 * @throws IllegalArgumentException If column number or row number is not in range.
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
     * Check if number is in range.
     *
     * @param nb Number to check.
     * @param propertyName Property name.
     * @throws IllegalArgumentException If number is not in range.
     */
    @Throws(IllegalArgumentException::class)
    private fun check(nb: Int, propertyName: String) {
        if (nb < Min || nb > Max) {
            throw IllegalArgumentException("$propertyName must be between $Min and $Max")
        }
    }
}
