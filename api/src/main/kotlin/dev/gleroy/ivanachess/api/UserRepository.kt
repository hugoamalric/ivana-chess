package dev.gleroy.ivanachess.api

/**
 * User repository.
 */
interface UserRepository : Repository<User> {
    /**
     * Check if user exists.
     *
     * @param pseudo Pseudo.
     * @return True if user exists, false otherwise.
     */
    fun existsByPseudo(pseudo: String): Boolean

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
}
