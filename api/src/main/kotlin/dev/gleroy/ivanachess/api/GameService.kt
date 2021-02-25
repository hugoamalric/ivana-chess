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
     * @return Game and summary.
     */
    fun create(): GameAndSummary

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
     * @throws GameIdNotFoundException If game does not exist.
     */
    @Throws(GameIdNotFoundException::class)
    fun getSummaryById(id: UUID): GameSummary

    /**
     * Get game summary by player token.
     *
     * @param token Player token.
     * @return Game summary.
     * @throws GameTokenNotFoundException If game does not exist.
     */
    @Throws(GameTokenNotFoundException::class)
    fun getSummaryByToken(token: UUID): GameSummary

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Game.
     * @throws GameIdNotFoundException If game does not exist.
     */
    @Throws(GameIdNotFoundException::class)
    fun getGameById(id: UUID): Game

    /**
     * Play move.
     *
     * @param token Player token.
     * @param move Move.
     * @return Game and summary.
     * @throws GameTokenNotFoundException If game does not exist.
     * @throws PlayException If an error occurs.
     */
    @Throws(exceptionClasses = [GameTokenNotFoundException::class, PlayException::class])
    fun play(token: UUID, move: Move): GameAndSummary

    /**
     * Play move.
     *
     * @param gameSummary Game summary.
     * @param token Player token.
     * @param move Move.
     * @return Game and summary.
     * @throws PlayException If an error occurs.
     */
    @Throws(exceptionClasses = [GameTokenNotFoundException::class, PlayException::class])
    fun play(gameSummary: GameSummary, token: UUID, move: Move): GameAndSummary
}
