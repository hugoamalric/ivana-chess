package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty

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
        Black
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
