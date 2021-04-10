package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.io.ColorConverter
import dev.gleroy.ivanachess.io.ColorRepresentation
import org.springframework.stereotype.Component

/**
 * Default implementation of color converter.
 */
@Component
class DefaultColorConverter : ColorConverter {
    override fun convertToColor(colorRepresentation: ColorRepresentation) = when (colorRepresentation) {
        ColorRepresentation.White -> Piece.Color.White
        ColorRepresentation.Black -> Piece.Color.Black
    }

    override fun convertToRepresentation(item: Piece.Color) = when (item) {
        Piece.Color.White -> ColorRepresentation.White
        Piece.Color.Black -> ColorRepresentation.Black
    }
}
