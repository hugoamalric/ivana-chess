package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.PositionedPiece

/**
 * Piece converter.
 */
interface PieceConverter : ItemConverter<PositionedPiece, PieceRepresentation> {
    /**
     * Create piece from color and type.
     *
     * @param colorRepresentation Representation of color.
     * @param typeRepresentation Representation of piece type.
     * @return Piece.
     */
    fun convertToPiece(colorRepresentation: ColorRepresentation, typeRepresentation: PieceRepresentation.Type): Piece
}
