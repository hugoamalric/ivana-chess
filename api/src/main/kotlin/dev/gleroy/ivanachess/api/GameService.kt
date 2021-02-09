package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Move
import java.util.*

/**
 * Game service.
 */
interface GameService {
    /**
     * Create new game.
     *
     * @return Game.
     */
    fun create(): GameInfo

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Game.
     * @throws PlayException.GameIdNotFound If game does not exist.
     */
    @Throws(PlayException.GameIdNotFound::class)
    fun getById(id: UUID): GameInfo

    /**
     * Get game by its ID.
     *
     * @param token Player token.
     * @return Game.
     * @throws PlayException.GameTokenNotFound If game does not exist.
     */
    @Throws(PlayException.GameTokenNotFound::class)
    fun getByToken(token: UUID): GameInfo

    /**
     * Get all games.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     */
    fun getAll(page: Int, size: Int): Page<GameInfo>

    /**
     * Play move.
     *
     * @param gameInfo Game.
     * @param token Player token.
     * @param move Move.
     * @return Updated game.
     * @throws PlayException If an error occurs.
     */
    @Throws(PlayException::class)
    fun play(gameInfo: GameInfo, token: UUID, move: Move): GameInfo
}
