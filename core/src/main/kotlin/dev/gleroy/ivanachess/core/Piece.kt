package dev.gleroy.ivanachess.core

import kotlin.math.abs

/**
 * Piece.
 */
sealed class Piece {
    /**
     * Piece color.
     *
     * @param rowOffset Row offset.
     */
    enum class Color(
        val rowOffset: Int
    ) {
        /**
         * White.
         */
        White(1),

        /**
         * Black.
         */
        Black(-1);

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
            const val WhiteSymbol = '♗'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♝'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingDiagonally(board, pos, target)

        override fun possibleMoves(board: Board, pos: Position, moves: List<Move>) =
            diagonalPossibleMoves(board, pos, moves)

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
            const val WhiteSymbol = '♔'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♚'
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

        override fun possibleMoves(board: Board, pos: Position, moves: List<Move>): Set<PossibleMove> {
            val boards = possibleMoves(
                board,
                pos,
                moves,
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
                        castlingPossibleMoves(board, Position(Position.Min, initialPos.row), moves) +
                        castlingPossibleMoves(board, Position(Position.Max, initialPos.row), moves)
            }
        }

        override fun toString() = symbol.toString()

        /**
         * Compute possible moves with castling.
         *
         * This method does not check if the king was already moved!
         *
         * @param board Board.
         * @param rookPos Rook position.
         * @param moves List of moves since the begin of the game.
         * @return Possible moves.
         */
        private fun castlingPossibleMoves(board: Board, rookPos: Position, moves: List<Move>) =
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
                    val move = Move.Simple(initialPos, initialPos.relativePosition(2 * offset, 0)!!)
                    val castlingBoard = move.execute(board, moves)
                    if (castlingBoard.kingIsCheck(color)) {
                        emptySet()
                    } else {
                        setOf(PossibleMove(move, castlingBoard))
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
            const val WhiteSymbol = '♘'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♞'
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

        override fun possibleMoves(board: Board, pos: Position, moves: List<Move>) = possibleMoves(
            board,
            pos,
            moves,
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
            const val WhiteSymbol = '♙'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♟'
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
            pos.relativePosition(-1, color.rowOffset) == target || pos.relativePosition(1, color.rowOffset) == target

        override fun possibleMoves(board: Board, pos: Position, moves: List<Move>): Set<PossibleMove> {
            val possibleBoards = setOf(
                pos.relativePosition(0, color.rowOffset),
                pos.relativePosition(-1, color.rowOffset),
                pos.relativePosition(1, color.rowOffset),
            ).asSequence()
                .filterNotNull()
                .filter { possiblePos ->
                    val pieceAtPos = board.pieceAt(possiblePos)
                    possiblePos.col != pos.col && pieceAtPos != null && pieceAtPos.color == color.opponent() ||
                            possiblePos.col == pos.col && pieceAtPos == null
                }
                .map { possiblePos -> Move.Simple(pos, possiblePos).let { PossibleMove(it, it.execute(board, moves)) } }
                .filterNot { it.resultingBoard.kingIsCheck(color) }
                .toSet() + firstMoves(board, pos, moves) + enPassantMoves(board, pos, moves)
            return promotionBoards(possibleBoards)
        }

        override fun toString() = symbol.toString()

        /**
         * Compute moves if en passant is possible.
         *
         * @param board Board.
         * @param pos Pawn position.
         * @param moves List of moves since the begin of the game.
         * @return Possible moves.
         */
        private fun enPassantMoves(board: Board, pos: Position, moves: List<Move>) =
            if (moves.isEmpty()) {
                emptySet()
            } else {
                val lastMove = moves.last()
                arrayOf(-1, 1)
                    .filter { colOffset ->
                        lastMove.to == pos.relativePosition(colOffset, 0) &&
                                lastMove.to.row - lastMove.from.row == 2 * -color.rowOffset &&
                                moves.none { it != lastMove && it.from == lastMove.from }
                    }
                    .map { colOffset ->
                        val move = Move.Simple(pos, pos.relativePosition(colOffset, color.rowOffset)!!)
                        PossibleMove(move, move.execute(board, moves))
                    }
                    .filterNot { board.kingIsCheck(color) }
                    .toSet()
            }

        /**
         * Compute moves if pawn was never moved.
         *
         * @param board Board.
         * @param pos Pawn position.
         * @param moves List of moves since the begin of the game.
         * @return Possible moves.
         */
        private fun firstMoves(board: Board, pos: Position, moves: List<Move>): Set<PossibleMove> {
            val oneSquarePos = pos.relativePosition(0, color.rowOffset)
            val isFirstMove = color == Color.White && pos.row == Position.Min + 1
                    || color == Color.Black && pos.row == Position.Max - 1
            return if (!isFirstMove) {
                emptySet()
            } else {
                val twoSquaresPos = oneSquarePos?.relativePosition(0, color.rowOffset)
                val isFreePath = oneSquarePos != null &&
                        twoSquaresPos != null &&
                        board.pieceAt(oneSquarePos) == null &&
                        board.pieceAt(twoSquaresPos) == null
                if (!isFreePath) {
                    emptySet()
                } else {
                    val possibleMove = Move.Simple(pos, twoSquaresPos!!).let { move ->
                        PossibleMove(move, move.execute(board, moves))
                    }
                    if (possibleMove.resultingBoard.kingIsCheck(color)) {
                        emptySet()
                    } else {
                        setOf(possibleMove)
                    }
                }
            }
        }

        /**
         * Compute promotion moves.
         *
         * @param possibleMoves Possible moves.
         * @return All possible moves.
         */
        private fun promotionBoards(possibleMoves: Set<PossibleMove>): Set<PossibleMove> {
            val targetRow = when (color) {
                Color.White -> Position.Max
                Color.Black -> Position.Min
            }
            val eligiblePossibleMoves = possibleMoves.filter { it.move.to.row == targetRow }
            val promotedPossibleMoves = eligiblePossibleMoves.flatMap { possibleMove ->
                availablePromotions.map { piece ->
                    possibleMove.copy(
                        resultingBoard = possibleMove.resultingBoard.promote(possibleMove.move.to, piece)
                    )
                }
            }
            return possibleMoves - eligiblePossibleMoves + promotedPossibleMoves
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
            const val WhiteSymbol = '♕'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♛'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingCrossly(board, pos, target) || isTargetingDiagonally(board, pos, target)

        override fun possibleMoves(board: Board, pos: Position, moves: List<Move>) =
            crossPossibleMoves(board, pos, moves) + diagonalPossibleMoves(board, pos, moves)

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
            const val WhiteSymbol = '♖'

            /**
             * Black symbol.
             */
            const val BlackSymbol = '♜'
        }

        override val symbol = when (color) {
            Color.White -> WhiteSymbol
            Color.Black -> BlackSymbol
        }

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingCrossly(board, pos, target)

        override fun possibleMoves(board: Board, pos: Position, moves: List<Move>) =
            crossPossibleMoves(board, pos, moves)

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
     * Compute possible moves.
     *
     * @param board Board.
     * @param pos Current position of this piece.
     * @param moves List of moves since the begin of the game.
     * @return All possible moves.
     */
    abstract fun possibleMoves(board: Board, pos: Position, moves: List<Move>): Set<PossibleMove>

    /**
     * Compute cross possible moves.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param moves List of moves since the begin of the game.
     * @return All possible moves in cross.
     */
    protected fun crossPossibleMoves(board: Board, initialPos: Position, moves: List<Move>) =
        recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(0, 1) } +
                recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(1, 0) } +
                recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(0, -1) } +
                recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(-1, 0) }

    /**
     * Compute diagonal possible moves.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param moves List of moves since the begin of the game.
     * @return All possible moves in diagonal.
     */
    protected fun diagonalPossibleMoves(board: Board, initialPos: Position, moves: List<Move>) =
        recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(-1, 1) } +
                recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(1, 1) } +
                recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(1, -1) } +
                recursivelyPossibleMoves(board, initialPos, moves) { it.relativePosition(-1, -1) }

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
     * Compute possible moves from possible positions.
     *
     * This method works for "standard piece" and does not handle pawn particular case.
     *
     * @param board Initial board.
     * @param pos Piece position.
     * @param moves List of moves since the begin of the game.
     * @param possiblePositions Possible moves.
     */
    protected fun possibleMoves(board: Board, pos: Position, moves: List<Move>, vararg possiblePositions: Position?) =
        possiblePositions.asSequence()
            .filterNotNull()
            .filter { possiblePos -> board.pieceAt(possiblePos)?.let { it.color == color.opponent() } ?: true }
            .map { possiblePos -> Move.Simple(pos, possiblePos).let { PossibleMove(it, it.execute(board, moves)) } }
            .filterNot { it.resultingBoard.kingIsCheck(color) }
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
     * Compute recursively possible moves.
     *
     * @param initialBoard Initial board.
     * @param initialPos Initial position.
     * @param moves List of moves since the begin of the game.
     * @param currentPos Current position.
     * @param nextPos Function to compute next position.
     * @return All possible moves.
     */
    private fun recursivelyPossibleMoves(
        initialBoard: Board,
        initialPos: Position,
        moves: List<Move>,
        currentPos: Position = initialPos,
        nextPos: (Position) -> Position?
    ): Set<PossibleMove> {
        val pos = nextPos(currentPos)
        return if (pos == null) {
            emptySet()
        } else {
            val piece = initialBoard.pieceAt(pos)
            val possibleMove = Move.Simple(initialPos, pos).let { PossibleMove(it, it.execute(initialBoard, moves)) }
            when {
                piece is King || possibleMove.resultingBoard.kingIsCheck(color) -> emptySet()
                piece == null -> setOf(possibleMove) +
                        recursivelyPossibleMoves(initialBoard, initialPos, moves, pos, nextPos)
                piece.color == color.opponent() -> setOf(possibleMove)
                else -> emptySet()
            }
        }
    }
}
