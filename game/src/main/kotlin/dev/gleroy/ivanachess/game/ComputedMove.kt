package dev.gleroy.ivanachess.game

/**
 * Computed move.
 *
 * @param move Move.
 * @param resultingBoard Resulting board.
 */
data class ComputedMove(
    val move: Move,
    val resultingBoard: Board
)
