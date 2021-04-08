package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.Position
import dev.gleroy.ivanachess.io.PieceRepresentation
import org.springframework.stereotype.Component

/**
 * Default implementation of piece converter.
 *
 * @param posConverter Position converter.
 */
@Component
class DefaultPieceConverter(
    private val posConverter: PositionConverter = DefaultPositionConverter()
) : PieceConverter {
    override fun convertColorToRepresentation(color: Piece.Color) = when (color) {
        Piece.Color.White -> PieceRepresentation.Color.White
        Piece.Color.Black -> PieceRepresentation.Color.Black
    }

    override fun convertToRepresentation(piece: Piece, pos: Position) = PieceRepresentation(
        color = convertColorToRepresentation(piece.color),
        type = piece.toTypeRepresentation(),
        pos = posConverter.convertToRepresentation(pos)
    )

    override fun convertToPiece(color: PieceRepresentation.Color, type: PieceRepresentation.Type) = when (type) {
        PieceRepresentation.Type.Pawn -> Piece.Pawn(color.toColor())
        PieceRepresentation.Type.Rook -> Piece.Rook(color.toColor())
        PieceRepresentation.Type.Knight -> Piece.Knight(color.toColor())
        PieceRepresentation.Type.Bishop -> Piece.Bishop(color.toColor())
        PieceRepresentation.Type.Queen -> Piece.Queen(color.toColor())
        PieceRepresentation.Type.King -> Piece.King(color.toColor())
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

    /**
     * Convert piece color representation to color.
     *
     * @return Color.
     */
    private fun PieceRepresentation.Color.toColor() = when (this) {
        PieceRepresentation.Color.White -> Piece.Color.White
        PieceRepresentation.Color.Black -> Piece.Color.Black
    }
}
