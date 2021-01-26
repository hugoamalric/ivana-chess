package dev.gleroy.ivanachess.api

import java.util.*

/**
 * Game repository.
 */
interface GameRepository {
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
     * @return Game or null if no game with this ID.
     */
    fun getById(id: UUID): GameInfo?

    /**
     * Get game information by token.
     *
     * @param token Token.
     * @return Game information or null if no game with this token.
     */
    fun getByToken(token: UUID): GameInfo?

    /**
     * Update game information.
     *
     * @param gameInfo Game.
     * @throws IllegalArgumentException If game does not exist.
     * @return Game.
     */
    @Throws(IllegalArgumentException::class)
    fun update(gameInfo: GameInfo): GameInfo
}
