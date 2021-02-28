package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.Page
import java.util.*

/**
 * User service.
 */
interface UserService {
    /**
     * Create new user.
     *
     * @param pseudo Pseudo.
     * @param bcryptPassword BCrypt hash of password.
     * @param role Role.
     * @return User.
     * @throws UserPseudoAlreadyUsedException If pseudo is already used.
     */
    @Throws(UserPseudoAlreadyUsedException::class)
    fun create(pseudo: String, bcryptPassword: String, role: User.Role): User

    /**
     * Get page of users.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     */
    fun getAll(page: Int, size: Int): Page<User>

    /**
     * Get user by ID.
     *
     * @param id User ID.
     * @return User.
     * @throws UserIdNotFoundException If user does not exist.
     */
    @Throws(UserIdNotFoundException::class)
    fun getById(id: UUID): User

    /**
     * Get user by pseudo.
     *
     * @param pseudo Pseudo.
     * @return User.
     * @throws UserPseudoNotFoundException If user does not exist.
     */
    @Throws(UserPseudoNotFoundException::class)
    fun getByPseudo(pseudo: String): User

    /**
     * Update user password.
     *
     * @param id User ID.
     * @param bcryptPassword BCrypt hash of password.
     * @param role Role.
     * @return Updated user.
     * @throws UserIdNotFoundException If user does not exist.
     */
    @Throws(UserIdNotFoundException::class)
    fun update(id: UUID, bcryptPassword: String, role: User.Role): User
}
