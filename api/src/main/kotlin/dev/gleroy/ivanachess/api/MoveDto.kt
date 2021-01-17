package dev.gleroy.ivanachess.api

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
)
