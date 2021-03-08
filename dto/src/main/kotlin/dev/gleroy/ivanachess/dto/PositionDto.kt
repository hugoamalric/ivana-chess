package dev.gleroy.ivanachess.dto

import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * Position DTO.
 *
 * @param col Column index.
 * @param row Row index.
 */
data class PositionDto(
    @field:Min(1)
    @field:Max(8)
    val col: Int,

    @field:Min(1)
    @field:Max(8)
    val row: Int
)
