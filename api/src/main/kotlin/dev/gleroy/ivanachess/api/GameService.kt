package dev.gleroy.ivanachess.api

/**
 * Game service.
 */
interface GameService {
    /**
     * Create new game.
     *
     * @return Game information.
     */
    fun create(): GameInfo
}
