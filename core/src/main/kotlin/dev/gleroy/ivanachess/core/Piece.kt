package dev.gleroy.ivanachess.core

/**
 * Piece.
 */
sealed class Piece {
    /**
     * Piece color.
     */
    enum class Color {
        /**
         * White.
         */
        White,

        /**
         * Black.
         */
        Black;

        /**
         * Get opponent color.
         *
         * @return Opponent color.
         */
        fun opponent() = when (this) {
            White -> Black
            Black -> White
        }
    }

    /**
     * Bishop piece.
     */
    data class Bishop(
        override val color: Color
    ) : Piece() {
        companion object {
            /**
             * White symbol.
             */
            const val WhiteSymbol = '♝'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♗'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun possiblePositions(board: Board, pos: Position) = diagonalPossiblePositions(board, pos)

        override fun toString() = symbol.toString()
    }

    /**
     * King piece.
     */
    data class King(
        override val color: Color
    ) : Piece() {
        companion object {
            /**
             * White symbol.
             */
            const val WhiteSymbol = '♚'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♔'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun possiblePositions(board: Board, pos: Position): Set<Position> {
            TODO("Not yet implemented")
        }

        override fun toString() = symbol.toString()
    }

    /**
     * Knight piece.
     */
    data class Knight(
        override val color: Color
    ) : Piece() {
        companion object {
            /**
             * White symbol.
             */
            const val WhiteSymbol = '♞'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♘'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun possiblePositions(board: Board, pos: Position): Set<Position> {
            TODO("Not yet implemented")
        }

        override fun toString() = symbol.toString()
    }

    /**
     * Pawn piece.
     */
    data class Pawn(
        override val color: Color
    ) : Piece() {
        companion object {
            /**
             * White symbol.
             */
            const val WhiteSymbol = '♟'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♙'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun possiblePositions(board: Board, pos: Position): Set<Position> {
            TODO("Not yet implemented")
        }

        override fun toString() = symbol.toString()
    }

    /**
     * Queen piece.
     */
    data class Queen(
        override val color: Color
    ) : Piece() {
        companion object {
            /**
             * White symbol.
             */
            const val WhiteSymbol = '♛'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♕'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun possiblePositions(board: Board, pos: Position): Set<Position> {
            TODO("Not yet implemented")
        }

        override fun toString() = symbol.toString()
    }

    /**
     * Root piece.
     */
    data class Rook(
        override val color: Color
    ) : Piece() {
        companion object {
            /**
             * White symbol.
             */
            const val WhiteSymbol = '♜'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♖'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun possiblePositions(board: Board, pos: Position): Set<Position> {
            TODO("Not yet implemented")
        }

        override fun toString() = symbol.toString()
    }

    /**
     * Color.
     */
    abstract val color: Color

    /**
     * Symbol.
     */
    abstract val symbol: Char

    /**
     * Compute possible positions.
     *
     * @param board Board.
     * @param pos Current position of piece.
     * @return All possible positions.
     */
    abstract fun possiblePositions(board: Board, pos: Position): Set<Position>

    /**
     * Compute diagonal possible positions.
     *
     * @param board Board.
     * @param initialPos Initial position.
     */
    protected fun diagonalPossiblePositions(board: Board, initialPos: Position) =
        recursivelyPossiblePositions(board, initialPos) { it.relativePosition(1, 1) } +
                recursivelyPossiblePositions(board, initialPos) { it.relativePosition(1, -1) } +
                recursivelyPossiblePositions(board, initialPos) { it.relativePosition(-1, 1) } +
                recursivelyPossiblePositions(board, initialPos) { it.relativePosition(-1, -1) }

    /**
     * Compute recursively possible positions.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param nextPos Function to compute next position.
     * @return All possible positions.
     */
    private fun recursivelyPossiblePositions(
        board: Board,
        initialPos: Position,
        nextPos: (Position) -> Position?
    ): Set<Position> {
        val pos = nextPos(initialPos)
        return if (pos == null) {
            emptySet()
        } else {
            val piece = board.pieceAt(pos)
            when {
                piece == null -> setOf(pos) + recursivelyPossiblePositions(board, pos, nextPos)
                piece.color == color.opponent() -> setOf(pos)
                else -> emptySet()
            }
        }
    }
}
