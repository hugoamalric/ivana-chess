package dev.gleroy.ivanachess.core

import kotlin.math.abs

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

        override fun toString() = name.toLowerCase()
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

        override fun possibleBoards(board: Board, pos: Position, moves: List<Move>) =
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

        /**
         * Initial position.
         */
        val initialPos = when (color) {
            Color.White -> Position(5, 1)
            Color.Black -> Position(5, 8)
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

        override fun possibleBoards(board: Board, pos: Position, moves: List<Move>): Set<Board> {
            val boards = possibleBoards(
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
            return if (moves.any { it.from == initialPos }) {
                boards
            } else {
                boards +
                        castlingBoards(board, Position(Position.Min, initialPos.row), moves) +
                        castlingBoards(board, Position(Position.Max, initialPos.row), moves)
            }
        }

        override fun toString() = symbol.toString()

        /**
         * Compute boards with castling.
         *
         * This method does not check if the king was already moved!
         *
         * @param board Board.
         * @param rookPos Rook position.
         * @param moves List of moves since the begin of the game.
         * @return Board with castling issue or empty set if castling is not possible.
         */
        private fun castlingBoards(board: Board, rookPos: Position, moves: List<Move>) =
            if (!board.piecePositions(Rook(color)).contains(rookPos) || moves.any { it.from == rookPos }) {
                emptySet()
            } else {
                val difference = initialPos.col - rookPos.col
                val offset = if (difference < 0) 1 else -1
                val distance = abs(difference)
                val containsPiece = (1 until distance)
                    .any { board.pieceAt(initialPos.relativePosition(it * offset, 0)!!) != null }
                if (containsPiece) {
                    emptySet()
                } else {
                    val castlingBoard = board
                        .movePiece(initialPos, initialPos.relativePosition(2 * offset, 0)!!)
                        .movePiece(rookPos, initialPos.relativePosition(offset)!!)
                    if (castlingBoard.kingIsCheck(color)) {
                        emptySet()
                    } else {
                        setOf(castlingBoard)
                    }
                }
            }
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

        override fun possibleBoards(board: Board, pos: Position, moves: List<Move>) = possibleBoards(
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

        /**
         * Available pawn promotions.
         */
        val availablePromotions = setOf(
            Queen(color),
            Rook(color),
            Bishop(color),
            Knight(color)
        )

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            pos.relativePosition(-1, 1) == target || pos.relativePosition(1, 1) == target

        override fun possibleBoards(board: Board, pos: Position, moves: List<Move>): Set<Board> {
            val rowOffset = when (color) {
                Color.White -> 1
                Color.Black -> -1
            }
            val possibleBoards = setOf(
                pos.relativePosition(0, rowOffset),
                pos.relativePosition(-1, rowOffset),
                pos.relativePosition(1, rowOffset),
            ).asSequence()
                .filterNotNull()
                .filter { possiblePos ->
                    val pieceAtPos = board.pieceAt(possiblePos)
                    possiblePos.col != pos.col && pieceAtPos != null && pieceAtPos.color == color.opponent() ||
                            possiblePos.col == pos.col && pieceAtPos == null
                }
                .map { it to board.movePiece(pos, it) }
                .filterNot { it.second.kingIsCheck(color) }
                .toMap() + firstMoveBoards(board, pos, rowOffset)
            return promotionBoards(possibleBoards)
        }

        override fun toString() = symbol.toString()

        /**
         * Compute boards if pawn was never moved.
         *
         * @param board Board.
         * @param pos Pawn position.
         * @param rowOffset 1 if pawn is white, -1 if pawn is black.
         * @return Map which associates new pawn position to board with two squares position if pawn was never moved,
         * empty set otherwise.
         */
        private fun firstMoveBoards(board: Board, pos: Position, rowOffset: Int): Map<Position, Board> {
            val oneSquarePos = pos.relativePosition(0, rowOffset)
            val isFirstMove = color == Color.White && pos.row == Position.Min + 1
                    || color == Color.Black && pos.row == Position.Max - 1
            return if (!isFirstMove) {
                emptyMap()
            } else {
                val twoSquaresPos = oneSquarePos?.relativePosition(0, rowOffset)
                val isFreePath = oneSquarePos != null &&
                        twoSquaresPos != null &&
                        board.pieceAt(oneSquarePos) == null &&
                        board.pieceAt(twoSquaresPos) == null
                if (!isFreePath) {
                    emptyMap()
                } else {
                    val nextBoard = board.movePiece(pos, twoSquaresPos!!)
                    if (nextBoard.kingIsCheck(color)) {
                        emptyMap()
                    } else {
                        mapOf(twoSquaresPos to nextBoard)
                    }
                }
            }
        }

        /**
         * Compute promotion boards.
         *
         * @param boardByPos Map which associates new pawn position to possible board.
         * @return All possible boards.
         */
        private fun promotionBoards(boardByPos: Map<Position, Board>): Set<Board> {
            val targetRow = when (color) {
                Color.White -> Position.Max
                Color.Black -> Position.Min
            }
            val boards = boardByPos.filter { it.key.row == targetRow }
            val promotedBoards = boards.flatMap { entry ->
                availablePromotions.map { entry.value.promote(entry.key, it) }
            }
            return boardByPos.values.toSet() - boards.values + promotedBoards
        }
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

        override fun possibleBoards(board: Board, pos: Position, moves: List<Move>) =
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

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingCrossly(board, pos, target)

        override fun possibleBoards(board: Board, pos: Position, moves: List<Move>) =
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
     * @param moves List of moves since the begin of the game.
     * @return All possible boards.
     */
    abstract fun possibleBoards(board: Board, pos: Position, moves: List<Move>): Set<Board>

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
     * @param targetPos Target position.
     * @return True if piece is targeting given position diagonally, false otherwise.
     */
    protected fun isTargetingDiagonally(board: Board, initialPos: Position, targetPos: Position) =
        isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(-1, 1) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(1, 1) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(1, -1) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(-1, -1) }

    /**
     * Verify if this piece is targeting a position crossly.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param targetPos Target position.
     * @return True if piece is targeting given position crossly, false otherwise.
     */
    protected fun isTargetingCrossly(board: Board, initialPos: Position, targetPos: Position) =
        isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(0, 1) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(1, 0) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(0, -1) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(-1, 0) }

    /**
     * Compute possible boards from possible positions.
     *
     * This method works for "standard piece" and does not handle pawn particular case.
     *
     * @param board Initial board.
     * @param pos Piece position.
     * @param possiblePositions Possible positions.
     */
    protected fun possibleBoards(board: Board, pos: Position, vararg possiblePositions: Position?) =
        possiblePositions.asSequence()
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
     * @param targetPos Target position.
     * @param nextPos Function to compute next position.
     * @return True if piece is targeting given position, false otherwise.
     */
    private fun isTargetingRecursively(
        board: Board,
        initialPos: Position,
        targetPos: Position,
        nextPos: (Position) -> Position?
    ): Boolean = when (val pos = nextPos(initialPos)) {
        null -> false
        targetPos -> true
        else -> board.pieceAt(pos) == null && isTargetingRecursively(board, pos, targetPos, nextPos)
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
