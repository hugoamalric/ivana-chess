package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Piece

/**
 * Color converter.
 */
interface ColorConverter {
    /**
     * Convert representation to color.
     *
     * @param colorRepresentation Representation of color.
     * @return Color.
     */
    fun convertToColor(colorRepresentation: ColorRepresentation): Piece.Color

    /**
     * Convert color to its representation.
     *
     * @param color Color.
     * @return Representation of color.
     */
    fun convertToRepresentation(color: Piece.Color): ColorRepresentation
}
