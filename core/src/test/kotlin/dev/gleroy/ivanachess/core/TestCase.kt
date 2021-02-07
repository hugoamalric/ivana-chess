package dev.gleroy.ivanachess.core

/**
 * Test case.
 *
 * @param name Name.
 * @param board Initial board.
 * @param moves List of moves since the begin of the game.
 * @param possibleMoves Expected possible moves.
 */
data class TestCase(
    val name: String,
    val board: Board,
    val moves: List<Move>,
    val possibleMoves: Set<Move>,
    val gameState: Game.State,
    val colorToPlay: Piece.Color
)
