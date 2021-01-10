package dev.gleroy.ivanachess.core

/**
 * String board deserializer.
 */
class StringBoardDeserializer : BoardDeserializer {
    private companion object {
        /**
         * Line regex.
         */
        private val LineRegex = Regex("^(\\| [♟♜♞♝♚♛♙♖♘♗♔♕ ] ){8}\\|$")
    }

    override fun deserialize(bytes: ByteArray): Board {
        val boardStr = String(bytes)
        val lines = boardStr.lines()
        if (lines.size != Position.Max * 2 + 2) {
            throw IllegalArgumentException("'$boardStr' is not a valid board")
        }
        return Board(
            pieceByPosition = (0 until Position.Max)
                .flatMapIndexed { row, i ->
                    val line = lines[i * 2 + 1]
                    if (!LineRegex.matches(line)) {
                        throw IllegalArgumentException("Line ${(row + 1) * 2} is invalid")
                    }
                    line.split("|")
                        .filter { it.isNotEmpty() }
                        .mapIndexedNotNull { col, case ->
                            case[1].toPiece()?.let { Position(col + 1, Position.Max - row) to it }
                        }
                }
                .toMap()
        )
    }

    /**
     * Convert char to piece.
     *
     * @return Piece or null if this char is space.
     */
    private fun Char.toPiece() = when (this) {
        '♟' -> Piece.Pawn(Piece.Color.White)
        '♜' -> Piece.Rook(Piece.Color.White)
        '♞' -> Piece.Knight(Piece.Color.White)
        '♝' -> Piece.Bishop(Piece.Color.White)
        '♚' -> Piece.King(Piece.Color.White)
        '♛' -> Piece.Queen(Piece.Color.White)
        '♙' -> Piece.Pawn(Piece.Color.Black)
        '♖' -> Piece.Rook(Piece.Color.Black)
        '♘' -> Piece.Knight(Piece.Color.Black)
        '♗' -> Piece.Bishop(Piece.Color.Black)
        '♔' -> Piece.King(Piece.Color.Black)
        '♕' -> Piece.Queen(Piece.Color.Black)
        else -> null
    }
}
