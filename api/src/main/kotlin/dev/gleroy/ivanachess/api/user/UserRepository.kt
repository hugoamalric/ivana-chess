package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.Repository
import java.util.*

/**
 * User repository.
 */
interface UserRepository : Repository<User> {
    /**
     * Check if user exists.
     *
     * @param email Email.
     * @return True if user exists, false otherwise.
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Check if user exists ignoring one user.
     *
     * @param email Email.
     * @param id ID of user to ignore.
     * @return True if user exists and it is not given user, false otherwise.
     */
    fun existsByEmail(email: String, id: UUID): Boolean

    /**
     * Check if user exists.
     *
     * @param pseudo Pseudo.
     * @return True if user exists, false otherwise.
     */
    fun existsByPseudo(pseudo: String): Boolean

    /**
     * Get user by its email.
     *
     * @param email Email.
     * @return User or null if does not exist.
     */
    fun getByEmail(email: String): User?

    /**
     * Get user by its pseudo.
     *
     * @param pseudo Pseudo.
     * @return User or null if does not exist.
     */
    fun getByPseudo(pseudo: String): User?

    /**
     * Save user.
     *
     * @param user User.
     * @return User.
     */
    fun save(user: User): User

    /**
     * Search user by pseudo.
     *
     * @param q Part of pseudo to search.
     * @param maxSize Maximum size of returned list.
     * @return Users which match search.
     * @throws IllegalArgumentException If maxSize is negative or equal to zero.
     */
    @Throws(IllegalArgumentException::class)
    fun searchByPseudo(q: String, maxSize: Int): List<User>
}
