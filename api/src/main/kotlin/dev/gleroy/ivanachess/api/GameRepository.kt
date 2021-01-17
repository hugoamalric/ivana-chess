package dev.gleroy.ivanachess.api

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
}
