package dev.gleroy.ivanachess.core

import dev.gleroy.ivanachess.game.Game
import dev.gleroy.ivanachess.game.Piece
import java.time.OffsetDateTime
import java.util.*

/**
 * Game entity.
 *
 * @param id ID.
 * @param creationDate Creation date.
 * @param whitePlayer User who plays white color.
 * @param blackPlayer User who plays black color.
 * @param turnColor Color for which is turn to play.
 * @param state Game state.
 * @param winnerColor Color of winner or null if the game is not checkmate.
 */
data class GameEntity(
    override val id: UUID = UUID.randomUUID(),
    override val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val whitePlayer: User,
    val blackPlayer: User,
    val turnColor: Piece.Color = Piece.Color.White,
    val state: Game.State = Game.State.InGame,
    val winnerColor: Piece.Color? = null,
) : Entity
