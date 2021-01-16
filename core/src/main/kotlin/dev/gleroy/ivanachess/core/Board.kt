package dev.gleroy.ivanachess.core

/**
 * Board.
 *
 * @param pieceByPosition Map which associates position to piece.
 */
data class Board(
    internal val pieceByPosition: Map<Position, Piece>
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
            Position(4, row) to Piece.Queen(color),
            Position(5, row) to Piece.King(color),
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
     * Verify if king of given color is check.
     *
     * @param color Color.
     * @return True if king of given color is check, false otherwise.
     * @throws MissingKingException If king is not present on this board.
     */
    @Throws(MissingKingException::class)
    fun kingIsCheck(color: Piece.Color): Boolean {
        val king = Piece.King(color)
        val pos = piecePositions(king).firstOrNull() ?: throw MissingKingException(king, this)
        return pieces(color.opponent()).any { it.piece.isTargeting(this, it.pos, pos) }
    }

    /**
     * Move piece and return new board.
     *
     * @param from Starting position.
     * @param to Targeting position.
     * @return New board with executed movement.
     * @throws IllegalArgumentException If no piece at starting position.
     */
    @Throws(IllegalArgumentException::class)
    fun movePiece(from: Position, to: Position): Board {
        val piece = pieceByPosition[from] ?: throw IllegalArgumentException("No piece at position $from")
        val pieceByPosition = pieceByPosition.toMutableMap()
        pieceByPosition.remove(from)
        pieceByPosition[to] = piece
        return Board(pieceByPosition)
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

    /**
     * Get piece positions.
     *
     * @param piece Piece.
     * @return Positions of given piece or empty set if piece is not on this board.
     */
    fun piecePositions(piece: Piece) = pieceByPosition
        .filter { it.value == piece }
        .map { it.key }
        .toSet()

    /**
     * Get all pieces of given color.
     *
     * @param color Color.
     * @return Positioned pieces of given color.
     */
    fun pieces(color: Piece.Color) = pieceByPosition
        .filter { it.value.color == color }
        .map { PositionedPiece(it.value, it.key) }
        .toSet()

    /**
     * Promote piece at given position.
     *
     * @param pos Piece position.
     * @return New board with promotion.
     * @throws IllegalArgumentException If no piece at position.
     */
    @Throws(IllegalArgumentException::class)
    fun promote(pos: Position, promotion: Piece): Board {
        if (!pieceByPosition.containsKey(pos)) {
            throw IllegalArgumentException("No piece at position $pos")
        }
        val pieceByPosition = pieceByPosition.toMutableMap()
        pieceByPosition[pos] = promotion
        return Board(pieceByPosition)
    }
}
