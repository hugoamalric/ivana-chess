package dev.gleroy.ivanachess.io

import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * Representation of position.
 *
 * @param col Column index.
 * @param row Row index.
 */
data class PositionRepresentation(
    @field:Min(1)
    @field:Max(8)
    val col: Int,

    @field:Min(1)
    @field:Max(8)
    val row: Int,
)
