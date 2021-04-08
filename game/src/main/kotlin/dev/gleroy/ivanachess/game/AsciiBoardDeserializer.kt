package dev.gleroy.ivanachess.game

/**
 * String board deserializer.
 */
class AsciiBoardDeserializer : BoardDeserializer {
    private companion object {
        /**
         * Line regex.
         */
        private val LineRegex = Regex("^[1-8] (\\| [♟♜♞♝♚♛♙♖♘♗♔♕ ] ){8}\\|$")
    }

    override fun deserialize(bytes: ByteArray): Board {
        val boardStr = String(bytes)
        val lines = boardStr.lines()
        if (lines.size != Position.Max * 2 + 3) {
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
                        .mapIndexedNotNull { col, case ->
                            if (case.isEmpty() || case[0].isDigit()) {
                                null
                            } else {
                                case[1].toPiece()?.let { Position(col, Position.Max - row) to it }
                            }
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
        Piece.Pawn.WhiteSymbol -> Piece.Pawn(Piece.Color.White)
        Piece.Rook.WhiteSymbol -> Piece.Rook(Piece.Color.White)
        Piece.Knight.WhiteSymbol -> Piece.Knight(Piece.Color.White)
        Piece.Bishop.WhiteSymbol -> Piece.Bishop(Piece.Color.White)
        Piece.King.WhiteSymbol -> Piece.King(Piece.Color.White)
        Piece.Queen.WhiteSymbol -> Piece.Queen(Piece.Color.White)
        Piece.Pawn.BlackSymbol -> Piece.Pawn(Piece.Color.Black)
        Piece.Rook.BlackSymbol -> Piece.Rook(Piece.Color.Black)
        Piece.Knight.BlackSymbol -> Piece.Knight(Piece.Color.Black)
        Piece.Bishop.BlackSymbol -> Piece.Bishop(Piece.Color.Black)
        Piece.King.BlackSymbol -> Piece.King(Piece.Color.Black)
        Piece.Queen.BlackSymbol -> Piece.Queen(Piece.Color.Black)
        else -> null
    }
}
