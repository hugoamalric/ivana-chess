package dev.gleroy.ivanachess.api

import java.util.*

/**
 * Game repository.
 */
interface GameRepository {
    /**
     * Create new game.
     *
     * @return Game information.
     */
    fun create(): GameInfo

    /**
     * Check if game exists.
     *
     * @param id Game ID.
     * @return True if game exists, false otherwise.
     */
    fun exists(id: UUID): Boolean

    /**
     * Get information about all games.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     * @throws IllegalArgumentException If offset or limit is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun getAll(page: Int, size: Int): Page<GameInfo>

    /**
     * Get game information by ID.
     *
     * @param id Game ID.
     * @return Game information or null if no game with this ID.
     */
    fun getById(id: UUID): GameInfo?

    /**
     * Get game information by player token.
     *
     * @param token Player token.
     * @return Game information or null if no game with this token.
     */
    fun getByToken(token: UUID): GameInfo?

    /**
     * Update game information.
     *
     * @param gameInfo Game information.
     * @throws IllegalArgumentException If game does not exist.
     * @return Updated game information.
     */
    @Throws(IllegalArgumentException::class)
    fun update(gameInfo: GameInfo): GameInfo
}
