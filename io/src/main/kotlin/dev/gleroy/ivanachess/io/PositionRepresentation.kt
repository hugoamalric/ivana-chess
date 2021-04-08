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
    @field:Min(ApiConstants.Constraints.MinPositionIndex.toLong())
    @field:Max(ApiConstants.Constraints.MaxPositionIndex.toLong())
    val col: Int,

    @field:Min(ApiConstants.Constraints.MinPositionIndex.toLong())
    @field:Max(ApiConstants.Constraints.MaxPositionIndex.toLong())
    val row: Int,
)
