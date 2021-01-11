package dev.gleroy.ivanachess.core

/**
 * Exception thrown when try to verify if king is check but no king on board.
 *
 * @param king King.
 * @param board Board.
 */
data class MissingKingException(
    val king: Piece.King,
    val board: Board
) : RuntimeException() {
    override val message = "${king.symbol} absent from $board"
}
