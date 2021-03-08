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
     * @param email Email.
     * @param bcryptPassword BCrypt hash of password.
     * @param role Role.
     * @return User.
     * @throws UserPseudoAlreadyUsedException If pseudo is already used.
     * @throws UserEmailAlreadyUsedException If email is already used.
     */
    @Throws(exceptionClasses = [UserPseudoAlreadyUsedException::class, UserEmailAlreadyUsedException::class])
    fun create(pseudo: String, email: String, bcryptPassword: String, role: User.Role = User.Role.Simple): User

    /**
     * Get page of users.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     */
    fun getAll(page: Int, size: Int): Page<User>

    /**
     * Get user by email.
     *
     * @param email Email.
     * @return User.
     * @throws UserEmailNotFoundException If user does not exist.
     */
    @Throws(UserEmailNotFoundException::class)
    fun getByEmail(email: String): User

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
     * Search user by pseudo.
     *
     * @param q Part of pseudo to search.
     * @param maxSize Maximum size of returned list.
     * @return Users which match search.
     */
    fun searchByPseudo(q: String, maxSize: Int): List<User>

    /**
     * Update user password.
     *
     * @param id User ID.
     * @param email Email.
     * @param bcryptPassword BCrypt hash of password.
     * @param role Role.
     * @return Updated user.
     * @throws UserIdNotFoundException If user does not exist.
     * @throws UserEmailAlreadyUsedException If email is already used.
     */
    @Throws(exceptionClasses = [UserIdNotFoundException::class, UserEmailAlreadyUsedException::class])
    fun update(id: UUID, email: String, bcryptPassword: String, role: User.Role): User
}
