package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Position
import dev.gleroy.ivanachess.io.PositionConverter
import dev.gleroy.ivanachess.io.PositionRepresentation
import org.springframework.stereotype.Component

/**
 * Default implementation of position converter.
 */
@Component
class DefaultPositionConverter : PositionConverter {
    override fun convertToRepresentation(item: Position) = PositionRepresentation(
        col = item.col,
        row = item.row,
    )

    override fun convertToPosition(posRepresentation: PositionRepresentation) = Position(
        col = posRepresentation.col,
        row = posRepresentation.row,
    )
}
