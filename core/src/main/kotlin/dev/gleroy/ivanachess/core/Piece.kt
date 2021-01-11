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

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingDiagonally(board, pos, target)

        override fun possibleBoards(board: Board, pos: Position, hasAlreadyMoved: Boolean) =
            diagonalPossibleBoards(board, pos)

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

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            pos.relativePosition(0, 1) == target ||
                    pos.relativePosition(0, -1) == target ||
                    pos.relativePosition(1, 0) == target ||
                    pos.relativePosition(1, 1) == target ||
                    pos.relativePosition(1, -1) == target ||
                    pos.relativePosition(-1, 0) == target ||
                    pos.relativePosition(-1, 1) == target ||
                    pos.relativePosition(-1, -1) == target

        override fun possibleBoards(board: Board, pos: Position, hasAlreadyMoved: Boolean) = possibleBoards(
            board,
            pos,
            pos.relativePosition(0, 1),
            pos.relativePosition(0, -1),
            pos.relativePosition(1, 0),
            pos.relativePosition(1, 1),
            pos.relativePosition(1, -1),
            pos.relativePosition(-1, 0),
            pos.relativePosition(-1, 1),
            pos.relativePosition(-1, -1)
        )

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

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            pos.relativePosition(-1, 2) == target ||
                    pos.relativePosition(1, 2) == target ||
                    pos.relativePosition(2, 1) == target ||
                    pos.relativePosition(2, -1) == target ||
                    pos.relativePosition(1, -2) == target ||
                    pos.relativePosition(-1, -2) == target ||
                    pos.relativePosition(-2, -1) == target ||
                    pos.relativePosition(-2, 1) == target

        override fun possibleBoards(board: Board, pos: Position, hasAlreadyMoved: Boolean) = possibleBoards(
            board,
            pos,
            pos.relativePosition(-1, 2),
            pos.relativePosition(1, 2),
            pos.relativePosition(2, 1),
            pos.relativePosition(2, -1),
            pos.relativePosition(1, -2),
            pos.relativePosition(-1, -2),
            pos.relativePosition(-2, -1),
            pos.relativePosition(-2, 1)
        )

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

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            pos.relativePosition(-1, 1) == target || pos.relativePosition(1, 1) == target

        override fun possibleBoards(board: Board, pos: Position, hasAlreadyMoved: Boolean): Set<Board> {
            val commonBoards = possibleBoards(
                board,
                pos,
                pos.relativePosition(0, 1)
            )
            return if (hasAlreadyMoved) {
                commonBoards
            } else {
                commonBoards + possibleBoards(board, pos, pos.relativePosition(0, 2))
            }
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

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingCrossly(board, pos, target) || isTargetingDiagonally(board, pos, target)

        override fun possibleBoards(board: Board, pos: Position, hasAlreadyMoved: Boolean) =
            crossPossibleBoards(board, pos) + diagonalPossibleBoards(board, pos)

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

        override fun isTargeting(board: Board, pos: Position, target: Position) = isTargetingCrossly(board, pos, target)

        override fun possibleBoards(board: Board, pos: Position, hasAlreadyMoved: Boolean) =
            crossPossibleBoards(board, pos)

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
     * Verify if this piece is targeting a position.
     *
     * @param board Board.
     * @param pos Current position of this piece.
     * @param target Targeting position.
     * @return True if this piece targeting given position, false otherwise.
     */
    abstract fun isTargeting(board: Board, pos: Position, target: Position): Boolean

    /**
     * Compute possible boards.
     *
     * @param board Board.
     * @param pos Current position of this piece.
     * @return All possible boards.
     */
    abstract fun possibleBoards(board: Board, pos: Position, hasAlreadyMoved: Boolean = false): Set<Board>

    /**
     * Compute cross possible boards.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @return All possible boards in cross.
     */
    protected fun crossPossibleBoards(board: Board, initialPos: Position) =
        recursivelyPossibleBoards(board, initialPos) { it.relativePosition(0, 1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(1, 0) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(0, -1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(-1, 0) }

    /**
     * Compute diagonal possible boards.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @return All possible boards in diagonal.
     */
    protected fun diagonalPossibleBoards(board: Board, initialPos: Position) =
        recursivelyPossibleBoards(board, initialPos) { it.relativePosition(-1, 1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(1, 1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(1, -1) } +
                recursivelyPossibleBoards(board, initialPos) { it.relativePosition(-1, -1) }

    /**
     * Verify if this piece is targeting a position diagonally.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param targetingPos Targeting position.
     * @return True if piece is targeting given position diagonally, false otherwise.
     */
    protected fun isTargetingDiagonally(board: Board, initialPos: Position, targetingPos: Position) =
        isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(-1, 1) } ||
                isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(1, 1) } ||
                isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(1, -1) } ||
                isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(-1, -1) }

    /**
     * Verify if this piece is targeting a position crossly.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param targetingPos Targeting position.
     * @return True if piece is targeting given position crossly, false otherwise.
     */
    protected fun isTargetingCrossly(board: Board, initialPos: Position, targetingPos: Position) =
        isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(0, 1) } ||
                isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(1, 0) } ||
                isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(0, -1) } ||
                isTargetingRecursively(board, initialPos, targetingPos) { it.relativePosition(-1, 0) }

    /**
     * Compute possible boards from possible positions.
     *
     * @param board Initial board.
     * @param pos Piece position.
     * @param possiblePositions Possible positions.
     */
    protected fun possibleBoards(board: Board, pos: Position, vararg possiblePositions: Position?) = possiblePositions
        .filterNotNull()
        .filter { possiblePos -> board.pieceAt(possiblePos)?.let { it.color == color.opponent() } ?: true }
        .map { board.movePiece(pos, it) }
        .filterNot { it.kingIsCheck(color) }
        .toSet()

    /**
     * Verify recursively if this piece is targeting a position.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param targetingPos Targeting position.
     * @param nextPos Function to compute next position.
     * @return True if piece is targeting given position, false otherwise.
     */
    private fun isTargetingRecursively(
        board: Board,
        initialPos: Position,
        targetingPos: Position,
        nextPos: (Position) -> Position?
    ): Boolean = when (val pos = nextPos(initialPos)) {
        null -> false
        targetingPos -> true
        else -> board.pieceAt(pos) == null && isTargetingRecursively(board, pos, targetingPos, nextPos)
    }

    /**
     * Compute recursively possible boards.
     *
     * @param initialBoard Initial board.
     * @param initialPos Initial position.
     * @param currentPos Current position.
     * @param nextPos Function to compute next position.
     * @return All possible boards.
     */
    private fun recursivelyPossibleBoards(
        initialBoard: Board,
        initialPos: Position,
        currentPos: Position = initialPos,
        nextPos: (Position) -> Position?
    ): Set<Board> {
        val pos = nextPos(currentPos)
        return if (pos == null) {
            emptySet()
        } else {
            val piece = initialBoard.pieceAt(pos)
            val board = initialBoard.movePiece(initialPos, pos)
            when {
                piece is King || board.kingIsCheck(color) -> emptySet()
                piece == null -> setOf(board) + recursivelyPossibleBoards(initialBoard, initialPos, pos, nextPos)
                piece.color == color.opponent() -> setOf(board)
                else -> emptySet()
            }
        }
    }
}
