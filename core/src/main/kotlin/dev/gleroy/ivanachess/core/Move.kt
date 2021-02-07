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

        override fun execute(board: Board, moves: List<Move>): Board {
            val pieceByPosition = board.pieceByPosition.toMutableMap()
            val piece = movePiece(pieceByPosition, from, to)
            if (isCastling()) {
                movePiece(pieceByPosition, RookPosition[to]!!, RookTargetPosition[to]!!)
            } else if (isEnPassant(moves, piece.color.rowOffset)) {
                pieceByPosition.remove(to.relativePosition(0, -piece.color.rowOffset)!!)
            }
            return Board(pieceByPosition)
        }

        override fun toString() = "$from$to"

        /**
         * Check if this move is castling.
         *
         * @return True if this move is castling, false otherwise.
         */
        private fun isCastling() = from == Piece.King(Piece.Color.White).initialPos &&
                (to == Position.fromCoordinates("C1") || to == Position.fromCoordinates("G1")) ||
                from == Piece.King(Piece.Color.Black).initialPos &&
                (to == Position.fromCoordinates("C8") || to == Position.fromCoordinates("G8"))

        /**
         * Check if this move is en passant.
         *
         * @param moves List of moves since the begin of the game.
         * @param rowOffset Row offset.
         * @return True if this move is en passant, false otherwise.
         */
        private fun isEnPassant(moves: List<Move>, rowOffset: Int) = if (moves.isEmpty()) {
            false
        } else {
            val lastMove = moves.last()
            arrayOf(-1, 1).any { colOffset ->
                lastMove.to == from.relativePosition(colOffset, 0) &&
                        lastMove.to.row - lastMove.from.row == 2 * -rowOffset &&
                        moves.none { it != lastMove && it.from == lastMove.from }
            }
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
     * @param moves List of moves since the begin of the game.
     * @return Updated board.
     * @throws IllegalStateException If move is invalid.
     */
    @Throws(IllegalStateException::class)
    abstract fun execute(board: Board, moves: List<Move>): Board

    /**
     * Move piece.
     *
     * This method will change map!
     *
     * @param pieceByPosition Map which associates position to piece.
     * @param from Start position.
     * @param to Target position.
     * @return Moved piece.
     * @throws IllegalStateException If move is invalid.
     */
    @Throws(IllegalStateException::class)
    protected fun movePiece(pieceByPosition: MutableMap<Position, Piece>, from: Position, to: Position): Piece {
        val piece = pieceByPosition[from] ?: throw IllegalStateException("No piece at position $from")
        pieceByPosition.remove(from)
        pieceByPosition[to] = piece
        return piece
    }
}
