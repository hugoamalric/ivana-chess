package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Piece
import java.time.OffsetDateTime
import java.util.*

/**
 * Game summary.
 *
 * @param id ID.
 * @param creationDate Creation date.
 * @param whiteToken Token for white player.
 * @param blackToken Token for black token.
 * @param turnColor Color for which is turn to play.
 * @param state Game state.
 */
data class GameSummary(
    override val id: UUID = UUID.randomUUID(),
    override val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val whiteToken: UUID = UUID.randomUUID(),
    val blackToken: UUID = UUID.randomUUID(),
    val turnColor: Piece.Color = Piece.Color.White,
    val state: Game.State = Game.State.InGame
) : Entity
