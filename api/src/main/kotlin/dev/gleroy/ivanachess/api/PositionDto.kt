package dev.gleroy.ivanachess.api

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
)
