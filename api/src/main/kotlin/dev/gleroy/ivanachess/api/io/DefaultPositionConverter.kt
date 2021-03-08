package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.PositionDto
import org.springframework.stereotype.Component

/**
 * Default implementation of position converter.
 */
@Component
class DefaultPositionConverter : PositionConverter {
    override fun convertToDto(pos: Position) = PositionDto(
        col = pos.col,
        row = pos.row
    )

    override fun convertToPosition(dto: PositionDto) = Position(
        col = dto.col,
        row = dto.row
    )
}
