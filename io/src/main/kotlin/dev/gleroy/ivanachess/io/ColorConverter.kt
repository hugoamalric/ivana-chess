package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Piece

/**
 * Color converter.
 */
interface ColorConverter : ItemConverter<Piece.Color, ColorRepresentation> {
    /**
     * Convert representation to color.
     *
     * @param colorRepresentation Representation of color.
     * @return Color.
     */
    fun convertToColor(colorRepresentation: ColorRepresentation): Piece.Color
}
