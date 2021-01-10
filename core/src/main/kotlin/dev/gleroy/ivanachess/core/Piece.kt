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

        override fun possibleBoards(board: Board, pos: Position) = diagonalPossibleBoards(board, pos)

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

        override fun possibleBoards(board: Board, pos: Position): Set<Board> {
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

        override fun possibleBoards(board: Board, pos: Position): Set<Board> {
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

        override fun possibleBoards(board: Board, pos: Position): Set<Board> {
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

        override fun possibleBoards(board: Board, pos: Position): Set<Board> {
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

        override fun possibleBoards(board: Board, pos: Position): Set<Board> {
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
     * Compute possible boards.
     *
     * @param board Board.
     * @param pos Current position of piece.
     * @return All possible boards.
     */
    abstract fun possibleBoards(board: Board, pos: Position): Set<Board>

    /**
     * Compute diagonal possible boards.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @return All possible boards in diagonal.
     */
    protected fun diagonalPossibleBoards(board: Board, initialPos: Position) =
        recursivelyPossibleBoards(board, initialPos) { it.relativePosition(1, 1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(1, -1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(-1, 1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(-1, -1) }

    /**
     * Compute recursively possible boards.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param currentPos Current position.
     * @param nextPos Function to compute next position.
     * @return All possible boards.
     */
    private fun recursivelyPossibleBoards(
        board: Board,
        initialPos: Position,
        currentPos: Position = initialPos,
        nextPos: (Position) -> Position?
    ): Set<Board> {
        val pos = nextPos(currentPos)
        return if (pos == null) {
            emptySet()
        } else {
            val piece = board.pieceAt(pos)
            when {
                piece == null ->
                    setOf(board.movePiece(initialPos, pos)) + recursivelyPossibleBoards(board, initialPos, pos, nextPos)
                piece.color == color.opponent() -> setOf(board.movePiece(initialPos, pos))
                else -> emptySet()
            }
        }
    }
}
