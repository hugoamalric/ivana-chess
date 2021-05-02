package dev.gleroy.ivanachess.core

import java.util.*

/**
 * User service.
 */
interface UserService : SearchableEntityService<User> {
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
     * Delete user with an ID.
     *
     * @param id User ID.
     * @throws EntityNotFoundException If user does not exist.
     * @throws NotAllowedException If user is super admin.
     */
    @Throws(exceptionClasses = [EntityNotFoundException::class, NotAllowedException::class])
    fun delete(id: UUID)

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
     * @return User.
     * @throws EntityNotFoundException If user does not exist.
     */
    @Throws(EntityNotFoundException::class)
    fun getByEmail(email: String): User

    /**
     * Get user by its pseudo.
     *
     * @param pseudo Pseudo.
     * @return User.
     * @throws EntityNotFoundException If user does not exist.
     */
    @Throws(EntityNotFoundException::class)
    fun getByPseudo(pseudo: String): User

    /**
     * Update user.
     *
     * @param id User ID.
     * @param email Email.
     * @param bcryptPassword BCrypt hash of password.
     * @param role Role.
     * @return Updated user.
     * @throws UserEmailAlreadyUsedException If email is already used.
     */
    @Throws(exceptionClasses = [EntityNotFoundException::class, UserEmailAlreadyUsedException::class])
    fun update(id: UUID, email: String, bcryptPassword: String, role: User.Role): User
}
