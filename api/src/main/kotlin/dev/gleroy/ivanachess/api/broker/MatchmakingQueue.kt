package dev.gleroy.ivanachess.api.broker

import dev.gleroy.ivanachess.api.user.User

/**
 * Matchmaking queue.
 */
interface MatchmakingQueue {
    /**
     * Put user in this queue.
     *
     * @param user User.
     */
    fun put(user: User)

    /**
     * Remove user from this queue.
     *
     * @param user User.
     */
    fun remove(user: User)
}
