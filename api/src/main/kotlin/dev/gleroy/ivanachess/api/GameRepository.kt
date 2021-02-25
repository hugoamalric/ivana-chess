package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Move
import java.util.*

/**
 * Game repository.
 */
interface GameRepository : Repository<GameSummary> {
    /**
     * Get game summary by player token.
     *
     * @param token Player token.
     * @return Game summary or null if no game with this token.
     */
    fun getByToken(token: UUID): GameSummary?

    /**
     * Get list of moves since the begin of the game.
     *
     * @param id Game ID.
     * @return List of moves since the begin of the game.
     */
    fun getMoves(id: UUID): List<Move>

    /**
     * Save game summary.
     *
     * @param gameSummary Game summary.
     * @param moves List of moves since the begin of the game.
     * @return Game summary.
     */
    fun save(gameSummary: GameSummary = GameSummary(), moves: List<Move> = emptyList()): GameSummary
}
