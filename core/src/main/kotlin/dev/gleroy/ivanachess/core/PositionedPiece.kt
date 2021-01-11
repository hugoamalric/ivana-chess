package dev.gleroy.ivanachess.core

/**
 * Positioned piece.
 *
 * @param piece Piece.
 * @param position Position.
 */
data class PositionedPiece(
    val piece: Piece,
    val position: Position
) {
    override fun toString() = "$position=$piece"
}
