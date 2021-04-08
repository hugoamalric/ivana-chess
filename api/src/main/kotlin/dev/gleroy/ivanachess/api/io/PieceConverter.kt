package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.Position
import dev.gleroy.ivanachess.io.PieceRepresentation

/**
 * Piece converter.
 */
interface PieceConverter {
    /**
     * Convert piece color to its representation.
     *
     * @param color Piece color.
     * @return Representation of color.
     */
    fun convertColorToRepresentation(color: Piece.Color): PieceRepresentation.Color

    /**
     * Convert piece to its representation.
     *
     * @param piece Piece.
     * @param pos Piece position.
     * @return Representation of piece.
     */
    fun convertToRepresentation(piece: Piece, pos: Position): PieceRepresentation

    /**
     * Create piece from color and type.
     *
     * @param color Piece color.
     * @param type Piece type.
     * @return Piece.
     */
    fun convertToPiece(color: PieceRepresentation.Color, type: PieceRepresentation.Type): Piece
}
