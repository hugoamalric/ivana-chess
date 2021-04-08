package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Representation of piece.
 *
 * @param color Color.
 * @param type Type.
 * @param pos Position.
 */
data class PieceRepresentation(
    val color: Color,
    val type: Type,
    val pos: PositionRepresentation,
) {
    /**
     * Representation of color.
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
     * Representation of piece type.
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
