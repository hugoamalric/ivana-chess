package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.GameInfo
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import java.time.OffsetDateTime
import java.util.*

/**
 * Game entity/
 *
 * @param id Game ID.
 * @param creationDate Creation date.
 * @param whiteToken White token.
 * @param blackToken Black token.
 */
internal data class GameEntity(
    val id: UUID,
    val creationDate: OffsetDateTime,
    val whiteToken: UUID,
    val blackToken: UUID
) {
    /**
     * Convert this game entity to game.
     *
     * @param moves List of moves since the begin of the game.
     * @return Game.
     */
    fun toGameInfo(moves: List<Move>) = GameInfo(
        id = id,
        creationDate = creationDate,
        whiteToken = whiteToken,
        blackToken = blackToken,
        game = Game(
            moves = moves
        )
    )
}
