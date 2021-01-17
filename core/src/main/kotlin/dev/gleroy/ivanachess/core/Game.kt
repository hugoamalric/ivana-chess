package dev.gleroy.ivanachess.core

/**
 * Game.
 *
 * @param board Board.
 * @param moves List of move since the begin of the game.
 */
data class Game(
    val board: Board = Board.Initial,
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
         * If game is ended by draw.
         */
        Draw
    }

    /**
     * Color which must play next move.
     */
    val colorToPlay = if (moves.size % 2 == 0) Piece.Color.White else Piece.Color.Black

    /**
     * All next possible moves.
     */
    val nextPossibleMoves = board.pieces(colorToPlay)
        .flatMap { it.piece.possibleMoves(board, it.pos, moves) }
        .toSet()

    /**
     * State.
     */
    val state = when {
        board.kingIsCheck(colorToPlay) && nextPossibleMoves.isEmpty() -> State.Checkmate
        nextPossibleMoves.isEmpty() -> State.Draw
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
        if (piece.color != colorToPlay) {
            throw InvalidMoveException("Piece at ${move.from} is not $colorToPlay")
        }
        val nextBoard = board.movePiece(move)
        val possibleMoves = nextPossibleMoves.map { it.move }
        if (!possibleMoves.contains(move)) {
            throw InvalidMoveException("Move from ${move.from} to ${move.to} is not allowed")
        }
        return copy(
            board = nextBoard,
            moves = moves + move
        )
    }
}
