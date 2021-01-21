package dev.gleroy.ivanachess.api

import java.util.*
import kotlin.jvm.Throws

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
     * Get game information from token.
     *
     * @param token Token.
     * @return Game information or null if no game information with this token.
     */
    fun get(token: UUID): GameInfo?

    /**
     * Update game information.
     *
     * @param gameInfo Game information.
     * @throws IllegalArgumentException If game information does not exist.
     * @return Game information.
     */
    @Throws(IllegalArgumentException::class)
    fun update(gameInfo: GameInfo): GameInfo
}
