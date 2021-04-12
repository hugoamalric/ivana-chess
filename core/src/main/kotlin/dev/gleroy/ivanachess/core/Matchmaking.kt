package dev.gleroy.ivanachess.core

/**
 * Matchmaking.
 */
interface Matchmaking {
    /**
     * Put user in this queue.
     *
     * @param user User.
     */
    fun put(user: User)

    /**
     * Remove user from queue.
     *
     * @param user User.
     */
    fun remove(user: User)
}
