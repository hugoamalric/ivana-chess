package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.PositionedPiece
import dev.gleroy.ivanachess.io.*
import org.springframework.stereotype.Component

/**
 * Default implementation of piece converter.
 *
 * @param colorConverter Color converter.
 * @param posConverter Position converter.
 */
@Component
class DefaultPieceConverter(
    private val colorConverter: ColorConverter = DefaultColorConverter(),
    private val posConverter: PositionConverter = DefaultPositionConverter(),
) : PieceConverter {
    override fun convertToRepresentation(item: PositionedPiece) = PieceRepresentation(
        color = colorConverter.convertToRepresentation(item.piece.color),
        type = item.piece.toTypeRepresentation(),
        pos = posConverter.convertToRepresentation(item.pos),
    )

    override fun convertToPiece(
        colorRepresentation: ColorRepresentation,
        typeRepresentation: PieceRepresentation.Type
    ) = when (typeRepresentation) {
        PieceRepresentation.Type.Pawn -> Piece.Pawn(colorConverter.convertToColor(colorRepresentation))
        PieceRepresentation.Type.Rook -> Piece.Rook(colorConverter.convertToColor(colorRepresentation))
        PieceRepresentation.Type.Knight -> Piece.Knight(colorConverter.convertToColor(colorRepresentation))
        PieceRepresentation.Type.Bishop -> Piece.Bishop(colorConverter.convertToColor(colorRepresentation))
        PieceRepresentation.Type.Queen -> Piece.Queen(colorConverter.convertToColor(colorRepresentation))
        PieceRepresentation.Type.King -> Piece.King(colorConverter.convertToColor(colorRepresentation))
    }

    /**
     * Get representation of piece type.
     *
     * @return Representation of piece type.
     */
    private fun Piece.toTypeRepresentation() = when (this) {
        is Piece.Pawn -> PieceRepresentation.Type.Pawn
        is Piece.Rook -> PieceRepresentation.Type.Rook
        is Piece.Knight -> PieceRepresentation.Type.Knight
        is Piece.Bishop -> PieceRepresentation.Type.Bishop
        is Piece.Queen -> PieceRepresentation.Type.Queen
        is Piece.King -> PieceRepresentation.Type.King
    }
}
