package dev.gleroy.ivanachess.core

/**
 * Game.
 *
 * @param moves List of move since the begin of the game.
 * @throws InvalidMoveException If history of moves is invalid.
 */
data class Game(
    val moves: List<Move> = emptyList()
) {
    /**
     * State.
     */
    enum class State {
        /**
         * If players are playing.
         */
        InGame,

        /**
         * If game is ended by checkmate.
         */
        Checkmate,

        /**
         * If game is ended by stalemate.
         */
        Stalemate
    }

    /**
     * Board.
     */
    val board = moves.foldIndexed(Board.Initial) { i, board, move ->
        val piece = board.pieceAt(move.from) ?: throw InvalidMoveException("No piece at ${move.from}")
        piece.move(board, move, moves.subList(0, i))
    }

    /**
     * Color for which is turn to play.
     */
    val turnColor = if (moves.size % 2 == 0) Piece.Color.White else Piece.Color.Black

    /**
     * All next possible moves.
     */
    val nextPossibleMoves = board.pieces(turnColor)
        .flatMap { it.piece.computeMoves(board, it.pos, moves) }
        .toSet()

    /**
     * State.
     */
    val state = when {
        board.kingIsTargeted(turnColor) && nextPossibleMoves.isEmpty() -> State.Checkmate
        nextPossibleMoves.isEmpty() -> State.Stalemate
        else -> State.InGame
    }

    /**
     * Play move.
     *
     * @param move Move.
     * @return Copy of this game with executed move.
     * @throws InvalidMoveException If move is impossible.
     */
    @Throws(InvalidMoveException::class)
    fun play(move: Move): Game {
        val piece = board.pieceAt(move.from) ?: throw InvalidMoveException("No piece at ${move.from}")
        if (piece.color != turnColor) {
            throw InvalidMoveException("Piece at ${move.from} is not $turnColor")
        }
        val possibleMoves = nextPossibleMoves.map { it.move }
        if (!possibleMoves.contains(move)) {
            throw InvalidMoveException("Move from ${move.from} to ${move.to} is not allowed")
        }
        return copy(moves = moves + move)
    }
}
