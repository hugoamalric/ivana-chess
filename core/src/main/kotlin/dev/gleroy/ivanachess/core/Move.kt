package dev.gleroy.ivanachess.core

/**
 * Move.
 */
sealed class Move {
    /**
     * Simple move.
     *
     * @param from Start position.
     * @param to Target position.
     */
    data class Simple(
        override val from: Position,
        override val to: Position
    ) : Move() {
        companion object {
            /**
             * Map which associate king target position to rook start position.
             */
            private val RookPosition = mapOf(
                Position.fromCoordinates("C1") to Position.fromCoordinates("A1"),
                Position.fromCoordinates("G1") to Position.fromCoordinates("H1"),
                Position.fromCoordinates("C8") to Position.fromCoordinates("A8"),
                Position.fromCoordinates("G8") to Position.fromCoordinates("H8"),
            )

            /**
             * Map which associate king target position to rook target position.
             */
            private val RookTargetPosition = mapOf(
                Position.fromCoordinates("C1") to Position.fromCoordinates("D1"),
                Position.fromCoordinates("G1") to Position.fromCoordinates("F1"),
                Position.fromCoordinates("C8") to Position.fromCoordinates("D8"),
                Position.fromCoordinates("G8") to Position.fromCoordinates("F8"),
            )

            /**
             * Instantiate simple move from coordinates string.
             *
             * @param from Start position coordinates string.
             * @param to Target position coordinates string.
             * @return Simple move.
             * @throws IllegalArgumentException If coordinates string is invalid.
             */
            @Throws(IllegalArgumentException::class)
            fun fromCoordinates(from: String, to: String) = Simple(
                from = Position.fromCoordinates(from),
                to = Position.fromCoordinates(to)
            )
        }

        override fun execute(board: Board): Board {
            val pieceByPosition = board.pieceByPosition.toMutableMap()
            movePiece(pieceByPosition, from, to)
            if (isCastling()) {
                movePiece(pieceByPosition, RookPosition[to]!!, RookTargetPosition[to]!!)
            }
            return Board(pieceByPosition)
        }

        override fun toString() = "$from$to"

        private fun isCastling() = from == Piece.King(Piece.Color.White).initialPos &&
                (to == Position.fromCoordinates("C1") || to == Position.fromCoordinates("G1")) ||
                from == Piece.King(Piece.Color.Black).initialPos &&
                (to == Position.fromCoordinates("C8") || to == Position.fromCoordinates("G8"))
    }

    /**
     * Start position.
     */
    abstract val from: Position

    /**
     * Target position.
     */
    abstract val to: Position

    /**
     * Execute move on given board.
     *
     * @param board Board.
     * @return Updated board.
     * @throws IllegalStateException If move is invalid.
     */
    @Throws(IllegalStateException::class)
    abstract fun execute(board: Board): Board

    /**
     * Move piece.
     *
     * This method will change map!
     *
     * @param pieceByPosition Map which associates position to piece.
     * @param from Start position.
     * @param to Target position.
     * @throws IllegalStateException If move is invalid.
     */
    @Throws(IllegalStateException::class)
    protected fun movePiece(pieceByPosition: MutableMap<Position, Piece>, from: Position, to: Position) {
        val piece = pieceByPosition[from] ?: throw IllegalStateException("No piece at position $from")
        pieceByPosition.remove(from)
        pieceByPosition[to] = piece
    }
}
