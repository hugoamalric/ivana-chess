package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Piece

/**
 * Color SQL enumeration value.
 *
 * @param label SQL label.
 * @param color Color.
 */
enum class ColorSqlEnumValue(
    override val label: String,
    val color: Piece.Color,
) : SqlEnumValue {
    /**
     * White.
     */
    White("white", Piece.Color.White),

    /**
     * Black.
     */
    Black("black", Piece.Color.Black);

    companion object {
        /**
         * Get color SQL enumeration value from color.
         *
         * @param color Color.
         * @return Color SQL enumeration value.
         */
        fun from(color: Piece.Color) = when (color) {
            Piece.Color.White -> White
            Piece.Color.Black -> Black
        }
    }
}
