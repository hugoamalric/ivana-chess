package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.dto.MoveDto

/**
 * Move converter.
 */
interface MoveConverter {
    /**
     * Convert move to DTO.
     *
     * @param move Move.
     * @return Move DTO.
     */
    fun convertToDto(move: Move): MoveDto

    /**
     * Convert DTO to move.
     *
     * @param dto DTO.
     * @return Move.
     */
    fun convertToMove(dto: MoveDto): Move
}
