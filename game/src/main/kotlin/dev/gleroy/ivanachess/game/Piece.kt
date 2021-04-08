package dev.gleroy.ivanachess.game

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

        override fun computeMoves(board: Board, pos: Position, moves: List<Move>) =
            computeDiagonalMoves(board, pos, moves)

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingDiagonally(board, pos, target)

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

        override fun computeMoves(board: Board, pos: Position, moves: List<Move>): Set<ComputedMove> {
            val stdComputedMoves = setOf(
                pos.relativePosition(-1, -1),
                pos.relativePosition(-1, 0),
                pos.relativePosition(-1, 1),
                pos.relativePosition(0, -1),
                pos.relativePosition(0, 1),
                pos.relativePosition(1, -1),
                pos.relativePosition(1, 0),
                pos.relativePosition(1, 1),
            )
                .mapNotNull { to -> to?.let { Move.Simple(pos, to).let { ComputedMove(it, move(board, it, moves)) } } }
            val computedMoves = if (board.kingIsTargeted(color) || moves.any { it.from == initialPos }) {
                stdComputedMoves
            } else {
                stdComputedMoves + setOf(
                    computeCastlingMove(board, Position.Min, moves),
                    computeCastlingMove(board, Position.Max, moves)
                ).filterNotNull()
            }
            return computedMoves
                .filter { it.isPossible(board) }
                .toSet()
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

        override fun move(board: Board, move: Move, moves: List<Move>): Board {
            val pieceByPosition = board.pieceByPosition.toMutableMap()
            doMove(pieceByPosition, move.from, move.to)
            val rookPos = RookPosition[move.to]
            if (move.from == initialPos && rookPos != null) {
                doMove(pieceByPosition, rookPos, RookTargetPosition[move.to]!!, Rook(color))
            }
            return Board(pieceByPosition)
        }

        override fun toString() = symbol.toString()

        /**
         * Compute castling moves.
         *
         * @param board Board.
         * @param rookCol Rook position column index.
         * @param moves List of moves since the begin of the game.
         * @return Computed move if it is valid, null otherwise.
         */
        private fun computeCastlingMove(board: Board, rookCol: Int, moves: List<Move>): ComputedMove? {
            val rookPos = Position(rookCol, initialPos.row)
            return if (moves.any { it.from == rookPos }) {
                null
            } else {
                val difference = initialPos.col - rookPos.col
                val offset = if (difference < 0) 1 else -1
                val distance = abs(difference)
                val containsPiece = (1 until distance)
                    .any { board.pieceAt(initialPos.relativePosition(it * offset, 0)!!) != null }
                if (containsPiece) {
                    null
                } else {
                    Move.Simple(initialPos, initialPos.relativePosition(2 * offset, 0)!!).let { move ->
                        ComputedMove(move, move(board, move, moves))
                    }
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

        override fun computeMoves(board: Board, pos: Position, moves: List<Move>) = setOf(
            pos.relativePosition(-1, 2),
            pos.relativePosition(1, 2),
            pos.relativePosition(2, 1),
            pos.relativePosition(2, -1),
            pos.relativePosition(1, -2),
            pos.relativePosition(-1, -2),
            pos.relativePosition(-2, -1),
            pos.relativePosition(-2, 1)
        )
            .mapNotNull { to -> to?.let { Move.Simple(pos, to).let { ComputedMove(it, move(board, it, moves)) } } }
            .filter { it.isPossible(board) }
            .toSet()

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            pos.relativePosition(-1, 2) == target ||
                    pos.relativePosition(1, 2) == target ||
                    pos.relativePosition(2, 1) == target ||
                    pos.relativePosition(2, -1) == target ||
                    pos.relativePosition(1, -2) == target ||
                    pos.relativePosition(-1, -2) == target ||
                    pos.relativePosition(-2, -1) == target ||
                    pos.relativePosition(-2, 1) == target

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

        override fun computeMoves(board: Board, pos: Position, moves: List<Move>): Set<ComputedMove> {
            val stdComputedMoves = setOf(
                pos.relativePosition(-1, color.rowOffset),
                pos.relativePosition(0, color.rowOffset),
                pos.relativePosition(1, color.rowOffset),
            )
                .filter { to ->
                    to != null && board.pieceAt(to).let { piece ->
                        to.col == pos.col && piece == null || to.col != pos.col && piece != null
                    }
                }
                .map { to -> Move.Simple(pos, to!!).let { ComputedMove(it, move(board, it, moves)) } }
            return computePromotionMoves(
                board = board,
                computedMoves = stdComputedMoves +
                        computeEnPassantMoves(board, pos, moves) +
                        (computeFirstMove(board, pos, moves)?.let { setOf(it) } ?: emptySet()),
                moves = moves
            )
                .filter { it.isPossible(board) }
                .toSet()
        }

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            pos.relativePosition(-1, color.rowOffset) == target || pos.relativePosition(1, color.rowOffset) == target

        override fun move(board: Board, move: Move, moves: List<Move>): Board = move.execute(board, moves)

        override fun toString() = symbol.toString()

        /**
         * Compute en-passant moves.
         *
         * @param board Board.
         * @param pos Position.
         * @param moves List of moves since the begin of the game, false otherwise.
         * @return Computed moves.
         */
        private fun computeEnPassantMoves(board: Board, pos: Position, moves: List<Move>) =
            computeEnPassantPositions(board, pos, moves)
                .map { to -> Move.Simple(pos, to).let { ComputedMove(it, move(board, it, moves)) } }

        /**
         * Compute en-passant positions.
         *
         * @param board Board.
         * @param pos Position.
         * @param moves List of moves since the begin of the game.
         * @return Computed positions.
         */
        private fun computeEnPassantPositions(board: Board, pos: Position, moves: List<Move>) = if (moves.isEmpty()) {
            emptySet()
        } else {
            val lastMove = moves.last()
            val movesWithoutLastOne = moves.dropLast(1)
            arrayOf(-1, 1)
                .filter { colOffset ->
                    board.pieceAt(lastMove.to) is Pawn &&
                            lastMove.to == pos.relativePosition(colOffset, 0) &&
                            lastMove.to.row - lastMove.from.row == 2 * -color.rowOffset &&
                            movesWithoutLastOne.none { it.from == lastMove.from }
                }
                .mapNotNull { pos.relativePosition(it, color.rowOffset) }
        }

        /**
         * Compute first move.
         *
         * @param board Board.
         * @param pos Position.
         * @param moves List of moves since the begin of the game.
         * @return First move if it is valid, null otherwise.
         */
        private fun computeFirstMove(board: Board, pos: Position, moves: List<Move>): ComputedMove? {
            val to = pos.relativePosition(0, 2 * color.rowOffset)
            val canMoveTwice = (color == Color.White && pos.row == Position.Min + 1 ||
                    color == Color.Black && pos.row == Position.Max - 1) &&
                    pos.relativePosition(0, color.rowOffset).isFree(board) &&
                    to.isFree(board)
            return if (canMoveTwice) {
                Move.Simple(pos, to!!).let { ComputedMove(it, move(board, it, moves)) }
            } else {
                null
            }
        }

        /**
         * Compute promotion moves from computed moves.
         *
         * @param board Board.
         * @param computedMoves Computed moves.
         * @param moves List of moves since the begin of the game.
         * @return Computed moves.
         */
        private fun computePromotionMoves(
            board: Board,
            computedMoves: List<ComputedMove>,
            moves: List<Move>
        ): List<ComputedMove> {
            val lastRow = when (color) {
                Color.White -> Position.Max
                Color.Black -> Position.Min
            }
            val eligibleComputedMoves = computedMoves.filter { it.move.to.row == lastRow }
            return if (eligibleComputedMoves.isEmpty()) {
                computedMoves
            } else {
                val promotions = setOf(Rook(color), Knight(color), Bishop(color), Queen(color))
                val promotionComputedMoves = eligibleComputedMoves
                    .flatMap { computedMove ->
                        promotions.map {
                            Move.Promotion(computedMove.move.from, computedMove.move.to, it).let { move ->
                                ComputedMove(move, move(board, move, moves))
                            }
                        }
                    }
                computedMoves - eligibleComputedMoves + promotionComputedMoves
            }
        }

        /**
         * Execute move.
         *
         * @param board Board.
         * @param moves List of moves since the begin of the game.
         * @return Updated board.
         */
        private fun Move.execute(board: Board, moves: List<Move>) = when (this) {
            is Move.Simple -> execute(board, moves)
            is Move.Promotion -> execute(board)
        }

        /**
         * Execute promotion move.
         *
         * @param board Board.
         * @return Updated board.
         */
        private fun Move.Promotion.execute(board: Board): Board {
            val pieceByPosition = board.pieceByPosition.toMutableMap()
            pieceByPosition.remove(from)
            pieceByPosition[to] = promotion
            return Board(pieceByPosition)
        }

        /**
         * Execute simple move.
         *
         * @param board Board.
         * @param moves List of moves since the begin of the game.
         * @return Updated board.
         */
        private fun Move.Simple.execute(board: Board, moves: List<Move>): Board {
            val pieceByPosition = board.pieceByPosition.toMutableMap()
            doMove(pieceByPosition, from, to)
            val enPassantPositions = computeEnPassantPositions(board, from, moves)
            if (enPassantPositions.contains(to)) {
                pieceByPosition.remove(moves.last().to)
            }
            return Board(pieceByPosition)
        }

        /**
         * Check if this position is free.
         *
         * @param board Board.
         * @return True if this position is free, false otherwise.
         */
        private fun Position?.isFree(board: Board) = this != null && board.pieceAt(this) == null
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

        override fun computeMoves(board: Board, pos: Position, moves: List<Move>) =
            computeCrosswiseMoves(board, pos, moves) + computeDiagonalMoves(board, pos, moves)

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingCrosswise(board, pos, target) || isTargetingDiagonally(board, pos, target)

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

        override fun computeMoves(board: Board, pos: Position, moves: List<Move>) =
            computeCrosswiseMoves(board, pos, moves)

        override fun isTargeting(board: Board, pos: Position, target: Position) =
            isTargetingCrosswise(board, pos, target)

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
     * Compute possible moves from position.
     *
     * @param board Board.
     * @param pos Position.
     * @param moves List of moves since the begin of the game.
     * @return Computed moves.
     */
    abstract fun computeMoves(board: Board, pos: Position, moves: List<Move>): Set<ComputedMove>

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
     * Move this piece on given board.
     *
     * @param board Board.
     * @param move Move.
     * @param moves List of moves since the begin of the game.
     * @return Updated board.
     * @throws IllegalStateException If move is invalid.
     */
    @Throws(IllegalStateException::class)
    open fun move(board: Board, move: Move, moves: List<Move>): Board {
        val pieceByPosition = board.pieceByPosition.toMutableMap()
        doMove(pieceByPosition, move.from, move.to)
        return Board(pieceByPosition)
    }

    /**
     * Compute crosswise moves from given position.
     *
     * @param board Board.
     * @param pos Position.
     * @param moves List of moves since the begin of the game.
     * @return Diagonal moves.
     */
    protected fun computeCrosswiseMoves(board: Board, pos: Position, moves: List<Move>) =
        computeMoves(board, pos, moves, -1, 0) +
                computeMoves(board, pos, moves, 0, -1) +
                computeMoves(board, pos, moves, 1, 0) +
                computeMoves(board, pos, moves, 0, 1)

    /**
     * Compute diagonal moves from given position.
     *
     * @param board Board.
     * @param pos Position.
     * @param moves List of moves since the begin of the game.
     * @return Diagonal moves.
     */
    protected fun computeDiagonalMoves(board: Board, pos: Position, moves: List<Move>) =
        computeMoves(board, pos, moves, -1, -1) +
                computeMoves(board, pos, moves, -1, 1) +
                computeMoves(board, pos, moves, 1, -1) +
                computeMoves(board, pos, moves, 1, 1)

    /**
     * Verify if this piece is targeting a position crosswise.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param targetPos Target position.
     * @return True if piece is targeting given position crosswise, false otherwise.
     */
    protected fun isTargetingCrosswise(board: Board, initialPos: Position, targetPos: Position) =
        isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(0, 1) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(1, 0) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(0, -1) } ||
                isTargetingRecursively(board, initialPos, targetPos) { it.relativePosition(-1, 0) }

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
     * Move this piece from position to another one.
     *
     * @param pieceByPosition Map which associates position to piece.
     * @param from Start position.
     * @param to Target position.
     * @param expectedPiece Expected piece at start position.
     * @return Updated map.
     * @throws IllegalStateException If piece at start position if not expected one.
     */
    @Throws(IllegalStateException::class)
    protected fun doMove(
        pieceByPosition: MutableMap<Position, Piece>,
        from: Position,
        to: Position,
        expectedPiece: Piece = this
    ): MutableMap<Position, Piece> {
        val piece = pieceByPosition[from]
        if (piece != expectedPiece) {
            throw IllegalStateException("$expectedPiece expected at position $from (actual: $piece)")
        }
        pieceByPosition.remove(from)
        pieceByPosition[to] = piece
        return pieceByPosition
    }

    /**
     * Compute moves from given position.
     *
     * @param board Board.
     * @param initialPos Initial position.
     * @param moves List of moves since the begin of the game.
     * @param colOffset Column offset from position.
     * @param rowOffset Row offset from position.
     * @param currentPos Current position.
     * @param computedMoves Current computed moves.
     * @return Moves.
     */
    private tailrec fun computeMoves(
        board: Board,
        initialPos: Position,
        moves: List<Move>,
        colOffset: Int,
        rowOffset: Int,
        currentPos: Position = initialPos,
        computedMoves: Set<ComputedMove> = emptySet()
    ): Set<ComputedMove> {
        val pos = currentPos.relativePosition(colOffset, rowOffset)
        return if (pos == null) {
            computedMoves
        } else {
            val computedMove = Move.Simple(initialPos, pos).let { ComputedMove(it, move(board, it, moves)) }
            val piece = board.pieceAt(pos)
            val nextComputedMoves = if (computedMove.isPossible(board)) {
                computedMoves + computedMove
            } else {
                computedMoves
            }
            if (piece == null) {
                computeMoves(
                    board = board,
                    initialPos = initialPos,
                    moves = moves,
                    colOffset = colOffset,
                    rowOffset = rowOffset,
                    currentPos = pos,
                    computedMoves = nextComputedMoves
                )
            } else {
                nextComputedMoves
            }
        }
    }

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
     * Check if computed move is possible.
     *
     * It is possible if king is not targeted and if its position is free or it contains a piece of opponent color.
     *
     * @param board Board.
     * @return True if computed move is possible, false otherwise.
     */
    protected fun ComputedMove.isPossible(board: Board): Boolean {
        val piece = board.pieceAt(move.to)
        return (piece == null || piece.color == color.opponent() && piece !is King) &&
                !resultingBoard.kingIsTargeted(color)
    }
}
