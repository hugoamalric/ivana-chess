package dev.gleroy.ivanachess.api.user

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

/**
 * Default implementation of user service.
 *
 * @param repository User repository.
 */
@Service
class DefaultUserService(
    private val repository: UserRepository
) : UserService {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DefaultUserService::class.java)
    }

    override fun create(pseudo: String, bcryptPassword: String, role: User.Role): User {
        if (repository.existsByPseudo(pseudo)) {
            throw UserPseudoAlreadyUsedException(pseudo).apply { Logger.error(message) }
        }
        val user = repository.save(
            user = User(
                pseudo = pseudo,
                bcryptPassword = bcryptPassword
            )
        )
        Logger.info("New user '$pseudo' (${user.id}) created")
        return user
    }

    override fun getById(id: UUID) = repository.getById(id) ?: throw UserIdNotFoundException(id).apply {
        Logger.error(message)
    }

    override fun getByPseudo(pseudo: String) = repository.getByPseudo(pseudo)
        ?: throw UserPseudoNotFoundException(pseudo).apply { Logger.error(message) }

    override fun getAll(page: Int, size: Int) = repository.getAll(page, size)

    override fun update(id: UUID, bcryptPassword: String, role: User.Role): User {
        val user = getById(id)
        return repository.save(
            user = user.copy(
                bcryptPassword = bcryptPassword,
                role = role
            )
        ).apply {
            Logger.info("User '$pseudo' ($id) updated")
        }
    }
}
