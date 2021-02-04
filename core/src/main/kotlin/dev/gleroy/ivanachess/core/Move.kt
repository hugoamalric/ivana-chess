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
            val piece = pieceByPosition[from] ?: throw IllegalArgumentException("No piece at position $from")
            pieceByPosition.remove(from)
            pieceByPosition[to] = piece
            return Board(pieceByPosition)
        }
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
     * @throws IllegalArgumentException If move is invalid.
     */
    @Throws(IllegalArgumentException::class)
    abstract fun execute(board: Board): Board
}
