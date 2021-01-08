package dev.gleroy.ivanachess.core

/**
 * String board serializer.
 */
class StringBoardSerializer : BoardSerializer {
    override fun serialize(board: Board): ByteArray {
        val builder = StringBuilder()
        (Position.Max downTo 1).forEach { builder.row(it, board) }
        builder.border()
        return builder.toString().toByteArray()
    }

    /**
     * Append border line to builder.
     */
    private fun StringBuilder.border() {
        (1..Position.Max).forEach { _ -> append("+---") }
        dropLast(2)
        appendLine("+")
    }

    /**
     * Append chess row to builder.
     *
     * @param row Row index.
     * @param board Board.
     */
    private fun StringBuilder.row(row: Int, board: Board) {
        border()
        (1..Position.Max).forEach { append("| ${board.pieceAt(it, row)?.symbol ?: ' '} ") }
        appendLine("|")
    }
}
