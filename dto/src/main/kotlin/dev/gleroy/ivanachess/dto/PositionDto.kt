package dev.gleroy.ivanachess.dto

import dev.gleroy.ivanachess.core.Position
import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * Position DTO.
 *
 * @param col Column index.
 * @param row Row index.
 */
data class PositionDto(
    @field:Min(Position.Min.toLong())
    @field:Max(Position.Max.toLong())
    val col: Int,

    @field:Min(Position.Min.toLong())
    @field:Max(Position.Max.toLong())
    val row: Int
) {
    companion object {
        /**
         * Instantiate DTO from position.
         *
         * @param pos Position.
         * @return DTO.
         */
        fun from(pos: Position) = PositionDto(
            col = pos.col,
            row = pos.row
        )
    }

    /**
     * Convert this DTO to position.
     *
     * @return Position.
     */
    fun convert() = Position(
        col = col,
        row = row
    )
}
