package dev.gleroy.ivanachess.core

/**
 * Possible move.
 *
 * @param move Move.
 * @param resultingBoard Resulting board.
 */
data class PossibleMove(
    val move: Move,
    val resultingBoard: Board
)
