package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.PositionDto

/**
 * Position converter.
 */
interface PositionConverter {
    /**
     * Convert position to DTO.
     *
     * @param pos Position.
     * @return Position DTO.
     */
    fun convertToDto(pos: Position): PositionDto

    /**
     * Convert DTO to position.
     *
     * @param dto DTO.
     * @return Position.
     */
    fun convertToPosition(dto: PositionDto): Position
}
