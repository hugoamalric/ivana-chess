package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.Entity
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Piece
import java.time.OffsetDateTime
import java.util.*

/**
 * Game summary.
 *
 * @param id ID.
 * @param creationDate Creation date.
 * @param whitePlayer User who plays white color.
 * @param blackPlayer User who plays black color.
 * @param turnColor Color for which is turn to play.
 * @param state Game state.
 */
data class GameSummary(
    override val id: UUID = UUID.randomUUID(),
    override val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val whitePlayer: User,
    val blackPlayer: User,
    val turnColor: Piece.Color = Piece.Color.White,
    val state: Game.State = Game.State.InGame
) : Entity
