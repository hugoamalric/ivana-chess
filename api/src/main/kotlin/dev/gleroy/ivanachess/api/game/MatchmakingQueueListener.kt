package dev.gleroy.ivanachess.api.game

/**
 * Matchmaking queue listener.
 */
interface MatchmakingQueueListener {
    /**
     * Handle matchmaking queue message.
     */
    fun handle(message: String)
}
