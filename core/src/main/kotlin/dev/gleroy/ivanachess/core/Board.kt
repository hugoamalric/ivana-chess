package dev.gleroy.ivanachess.core

/**
 * Board.
 *
 * @param pieceByPosition Map which associates position to piece.
 */
data class Board(
    private val pieceByPosition: Map<Position, Piece>
) {
    companion object {
        /**
         * Initial board.
         */
        val Initial = Board(
            pieceByPosition = Piece.Color.values()
                .map { color ->
                    val initRow: Int
                    val pawnsRow: Int
                    when (color) {
                        Piece.Color.White -> {
                            initRow = Position.Min
                            pawnsRow = initRow + 1
                        }
                        Piece.Color.Black -> {
                            initRow = Position.Max
                            pawnsRow = initRow - 1
                        }
                    }
                    initRow(color, initRow) + pawnsRow(color, pawnsRow)
                }
                .reduce { acc, map -> acc + map }
        )

        /**
         * Instantiate initial row.
         *
         * @param color Pieces color.
         * @param row Row number.
         * @return Map which associate position to piece.
         */
        private fun initRow(color: Piece.Color, row: Int) = mapOf(
            Position(1, row) to Piece.Rook(color),
            Position(2, row) to Piece.Knight(color),
            Position(3, row) to Piece.Bishop(color),
            Position(4, row) to Piece.King(color),
            Position(5, row) to Piece.Queen(color),
            Position(6, row) to Piece.Bishop(color),
            Position(7, row) to Piece.Knight(color),
            Position(8, row) to Piece.Rook(color)
        )

        /**
         * Instantiate row of pawns.
         *
         * @param color Pawns color.
         * @param row Row number.
         * @return Map which associate position to pawn.
         */
        private fun pawnsRow(color: Piece.Color, row: Int) = (Position.Min..Position.Max)
            .map { Position(it, row) to Piece.Pawn(color) }
            .toMap()
    }

    /**
     * Get piece.
     *
     * @param col Column index..
     * @param row Row index.
     * @return Piece at given position or null if no piece.
     * @throws IllegalArgumentException If column index or row index is not in valid range.
     */
    @Throws(IllegalArgumentException::class)
    fun pieceAt(col: Int, row: Int) = pieceAt(Position(col, row))

    /**
     * Get piece.
     *
     * @param pos Position.
     * @return Piece at given position or null if no piece.
     */
    fun pieceAt(pos: Position) = pieceByPosition[pos]
}
