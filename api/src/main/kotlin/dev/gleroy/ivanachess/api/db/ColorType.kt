package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.Piece

/**
 * Color type.
 *
 * @param sqlValue SQL type value.
 * @param color Color.
 */
internal enum class ColorType(
    val sqlValue: String,
    val color: Piece.Color
) {
    White("white", Piece.Color.White),
    Black("black", Piece.Color.Black);

    companion object {
        /**
         * Get color type from color.
         *
         * @param color Color.
         * @return Color type.
         */
        fun from(color: Piece.Color) = when (color) {
            Piece.Color.White -> White
            Piece.Color.Black -> Black
        }

        /**
         * Get color type from SQL type value.
         *
         * @param sqlValue SQL type value.
         * @return Color type.
         * @throws IllegalArgumentException If SQL type value is not a valid color.
         */
        @Throws(IllegalArgumentException::class)
        fun from(sqlValue: String) = values().find { it.sqlValue == sqlValue }
            ?: throw IllegalArgumentException("Unknown color '$sqlValue'")
    }
}
