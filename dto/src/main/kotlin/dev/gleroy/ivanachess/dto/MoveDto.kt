package dev.gleroy.ivanachess.dto

import dev.gleroy.ivanachess.core.Move
import javax.validation.Valid

/**
 * Move DTO.
 *
 * @param from Start position.
 * @param to Target position.
 */
data class MoveDto(
    @field:Valid
    val from: PositionDto,

    @field:Valid
    val to: PositionDto
) {
    companion object {
        /**
         * Instantiate DTO from move.
         *
         * @param move Move.
         * @return DTO.
         */
        fun from(move: Move) = MoveDto(
            from = PositionDto.from(move.from),
            to = PositionDto.from(move.to)
        )
    }

    /**
     * Convert this DTO to move.
     *
     * @return Move.
     */
    fun convert() = Move.Simple(
        from = from.convert(),
        to = to.convert()
    )
}
