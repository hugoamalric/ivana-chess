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
    fun get(id: UUID): GameInfo

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
     * @param token Token.
     * @param move Move.
     * @return Updated game.
     * @throws PlayException If an error occurs.
     */
    @Throws(PlayException::class)
    fun play(token: UUID, move: Move): GameInfo
}
