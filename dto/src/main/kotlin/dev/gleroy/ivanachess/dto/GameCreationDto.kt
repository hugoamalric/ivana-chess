package dev.gleroy.ivanachess.dto

import java.util.*

/**
 * Game creation DTO.
 *
 * @param whitePlayer White player user ID.
 * @param blackPlayer Black player user ID.
 */
data class GameCreationDto(
    val whitePlayer: UUID,
    val blackPlayer: UUID
)
