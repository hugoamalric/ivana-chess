package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.PieceDto
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
    override fun convertColorToDto(color: Piece.Color) = when (color) {
        Piece.Color.White -> PieceDto.Color.White
        Piece.Color.Black -> PieceDto.Color.Black
    }

    override fun convertToDto(piece: Piece, pos: Position) = PieceDto(
        color = convertColorToDto(piece.color),
        type = piece.toTypeDto(),
        pos = posConverter.convertToDto(pos)
    )

    override fun convertToPiece(color: PieceDto.Color, type: PieceDto.Type) = when (type) {
        PieceDto.Type.Pawn -> Piece.Pawn(color.toColor())
        PieceDto.Type.Rook -> Piece.Rook(color.toColor())
        PieceDto.Type.Knight -> Piece.Knight(color.toColor())
        PieceDto.Type.Bishop -> Piece.Bishop(color.toColor())
        PieceDto.Type.Queen -> Piece.Queen(color.toColor())
        PieceDto.Type.King -> Piece.King(color.toColor())
    }

    /**
     * Get piece type DTO.
     *
     * @return Piece type DTO.
     */
    private fun Piece.toTypeDto() = when (this) {
        is Piece.Pawn -> PieceDto.Type.Pawn
        is Piece.Rook -> PieceDto.Type.Rook
        is Piece.Knight -> PieceDto.Type.Knight
        is Piece.Bishop -> PieceDto.Type.Bishop
        is Piece.Queen -> PieceDto.Type.Queen
        is Piece.King -> PieceDto.Type.King
    }

    /**
     * Convert piece color DTO to color.
     *
     * @return Color.
     */
    private fun PieceDto.Color.toColor() = when (this) {
        PieceDto.Color.White -> Piece.Color.White
        PieceDto.Color.Black -> Piece.Color.Black
    }
}
