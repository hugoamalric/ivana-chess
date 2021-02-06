package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.PositionedPiece

/**
 * Piece DTO.
 *
 * @param color Color.
 * @param type Type.
 * @param pos Position.
 */
data class PieceDto(
    val color: Color,
    val type: Type,
    val pos: PositionDto
) {
    companion object {
        /**
         * Instantiate DTO from positioned piece.
         *
         * @param positionedPiece Positioned piece.
         * @return DTO.
         */
        fun from(positionedPiece: PositionedPiece): PieceDto {
            val type = when (positionedPiece.piece) {
                is Piece.Pawn -> Type.Pawn
                is Piece.Rook -> Type.Rook
                is Piece.Knight -> Type.Knight
                is Piece.Bishop -> Type.Bishop
                is Piece.Queen -> Type.Queen
                is Piece.King -> Type.King
            }
            return PieceDto(
                color = Color.from(positionedPiece.piece.color),
                type = type,
                pos = PositionDto.from(positionedPiece.pos)
            )
        }
    }

    /**
     * Color.
     */
    enum class Color {
        /**
         * White.
         */
        @JsonProperty("white")
        White,

        /**
         * Black.
         */
        @JsonProperty("black")
        Black;

        companion object {
            /**
             * Get DTO from color.
             *
             * @param color Color.
             * @return DTO.
             */
            fun from(color: Piece.Color) = when (color) {
                Piece.Color.White -> White
                Piece.Color.Black -> Black
            }
        }
    }

    /**
     * Type.
     */
    enum class Type {
        /**
         * Pawn.
         */
        @JsonProperty("pawn")
        Pawn,

        /**
         * Rook.
         */
        @JsonProperty("rook")
        Rook,

        /**
         * Knight.
         */
        @JsonProperty("knight")
        Knight,

        /**
         * Bishop.
         */
        @JsonProperty("bishop")
        Bishop,

        /**
         * Queen.
         */
        @JsonProperty("queen")
        Queen,

        /**
         * King.
         */
        @JsonProperty("king")
        King
    }
}
