package dev.gleroy.ivanachess.io

import java.util.*

/**
 * Game creation.
 *
 * @param whitePlayer White player user ID.
 * @param blackPlayer Black player user ID.
 */
data class GameCreation(
    val whitePlayer: UUID,
    val blackPlayer: UUID,
)
