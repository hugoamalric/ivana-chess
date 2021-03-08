package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.PieceDto

/**
 * Piece converter.
 */
interface PieceConverter {
    /**
     * Convert piece color to DTO.
     *
     * @param color Piece color.
     * @return DTO.
     */
    fun convertColorToDto(color: Piece.Color): PieceDto.Color

    /**
     * Convert piece to DTO.
     *
     * @param piece Piece.
     * @param pos Piece position.
     * @return Piece DTO.
     */
    fun convertToDto(piece: Piece, pos: Position): PieceDto

    /**
     * Create piece from color and type.
     *
     * @param color Piece color.
     * @param type Piece type.
     * @return Piece.
     */
    fun convertToPiece(color: PieceDto.Color, type: PieceDto.Type): Piece
}
