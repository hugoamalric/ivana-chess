package dev.gleroy.ivanachess.core

import java.util.*

/**
 * User repository.
 */
interface UserRepository : SearchableEntityRepository<User> {
    /**
     * Check if user with an email exists.
     *
     * @param email Email.
     * @param excluding Set of user IDs excluded from the search.
     * @return True if user with email exists, false otherwise.
     */
    fun existsWithEmail(email: String, excluding: Set<UUID> = emptySet()): Boolean

    /**
     * Check if user with a pseudo exists.
     *
     * @param pseudo Pseudo.
     * @param excluding Set of user IDs excluded from the search.
     * @return True if user with pseudo exists, false otherwise.
     */
    fun existsWithPseudo(pseudo: String, excluding: Set<UUID> = emptySet()): Boolean

    /**
     * Get user by its email.
     *
     * @param email Email.
     * @return User or null if does not exist.
     */
    fun fetchByEmail(email: String): User?

    /**
     * Get user by its pseudo.
     *
     * @param pseudo Pseudo.
     * @return User or null if does not exist.
     */
    fun fetchByPseudo(pseudo: String): User?
}
