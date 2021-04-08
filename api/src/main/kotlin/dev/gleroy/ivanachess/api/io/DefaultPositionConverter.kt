package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Position
import dev.gleroy.ivanachess.io.PositionRepresentation
import org.springframework.stereotype.Component

/**
 * Default implementation of position converter.
 */
@Component
class DefaultPositionConverter : PositionConverter {
    override fun convertToRepresentation(pos: Position) = PositionRepresentation(
        col = pos.col,
        row = pos.row
    )

    override fun convertToPosition(representation: PositionRepresentation) = Position(
        col = representation.col,
        row = representation.row
    )
}
