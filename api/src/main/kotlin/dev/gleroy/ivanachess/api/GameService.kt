package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import java.util.*

/**
 * Game service.
 */
interface GameService {
    /**
     * Create new game.
     *
     * @return Game summary.
     */
    fun create(): GameSummary

    /**
     * Get page of game summaries.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     */
    fun getAllSummaries(page: Int, size: Int): Page<GameSummary>

    /**
     * Get game summary by ID.
     *
     * @param id Game ID.
     * @return Game summary.
     * @throws PlayException.GameIdNotFound If game does not exist.
     */
    @Throws(PlayException.GameIdNotFound::class)
    fun getSummaryById(id: UUID): GameSummary

    /**
     * Get game summary by player token.
     *
     * @param token Player token.
     * @return Game summary.
     * @throws PlayException.GameTokenNotFound If game does not exist.
     */
    @Throws(PlayException.GameTokenNotFound::class)
    fun getSummaryByToken(token: UUID): GameSummary

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Game.
     * @throws PlayException.GameIdNotFound If game does not exist.
     */
    @Throws(PlayException.GameIdNotFound::class)
    fun getGameById(id: UUID): Game

    /**
     * Play move.
     *
     * @param gameSummary Game summary.
     * @param token Player token.
     * @param move Move.
     * @return Updated game summary.
     * @throws PlayException If an error occurs.
     */
    @Throws(PlayException::class)
    fun play(gameSummary: GameSummary, token: UUID, move: Move): GameSummary
}
