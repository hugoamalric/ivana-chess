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
     * Play move.
     *
     * @param move Move.
     * @return Copy of this game with executed move.
     * @throws InvalidMoveException If move is impossible.
     */
    @Throws(InvalidMoveException::class)
    fun play(move: Move): Game {
        val color = if (moves.size % 2 == 0) Piece.Color.White else Piece.Color.Black
        val piece = board.pieceAt(move.from) ?: throw InvalidMoveException("No piece at ${move.from}")
        if (piece.color != color) {
            throw InvalidMoveException("Piece at ${move.from} is not $color")
        }
        val nextBoard = board.movePiece(move)
        val pieces = board.pieces(color)
        val possibleBoards = pieces.flatMap { it.piece.possibleBoards(board, it.pos, moves) }
        if (!possibleBoards.contains(nextBoard)) {
            throw InvalidMoveException("Move from ${move.from} to ${move.to} is not allowed")
        }
        return copy(
            board = nextBoard,
            moves = moves + move
        )
    }
}
