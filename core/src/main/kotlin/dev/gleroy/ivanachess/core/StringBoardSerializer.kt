package dev.gleroy.ivanachess.core

/**
 * String board serializer.
 */
class StringBoardSerializer : BoardSerializer {
    override fun serialize(board: Board): ByteArray {
        val builder = StringBuilder()
        (Position.Max downTo 1).forEach { builder.row(it, board) }
        builder.border()
        builder.columnLetters()
        return builder.toString().toByteArray()
    }

    /**
     * Append border line to builder.
     */
    private fun StringBuilder.border() {
        append("  ")
        (1..Position.Max).forEach { _ -> append("+---") }
        dropLast(2)
        appendLine("+")
    }

    /**
     * Append column letters line to builder.
     */
    private fun StringBuilder.columnLetters() {
        append(" ")
        ('A'..'H').forEach { append("   $it") }
        appendLine()
    }

    /**
     * Append chess row to builder.
     *
     * @param row Row index.
     * @param board Board.
     */
    private fun StringBuilder.row(row: Int, board: Board) {
        border()
        append("$row ")
        (1..Position.Max).forEach { append("| ${board.pieceAt(it, row)?.symbol ?: ' '} ") }
        appendLine("|")
    }
}
