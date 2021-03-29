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

    override fun create(pseudo: String, email: String, bcryptPassword: String, role: User.Role): User {
        if (repository.existsByPseudo(pseudo)) {
            throw UserPseudoAlreadyUsedException(pseudo).apply { Logger.info(message) }
        }
        if (repository.existsByEmail(email)) {
            throw UserEmailAlreadyUsedException(email).apply { Logger.info(message) }
        }
        val user = repository.save(
            user = User(
                pseudo = pseudo,
                email = email,
                bcryptPassword = bcryptPassword
            )
        )
        Logger.info("New user '$pseudo' (${user.id}) created")
        return user
    }

    override fun existsByEmail(email: String) = repository.existsByEmail(email)

    override fun existsByPseudo(pseudo: String) = repository.existsByPseudo(pseudo)

    override fun getByEmail(email: String) = repository.getByEmail(email)
        ?: throw UserEmailNotFoundException(email).apply { Logger.info(message) }

    override fun getById(id: UUID) = repository.getById(id) ?: throw UserIdNotFoundException(id).apply {
        Logger.info(message)
    }

    override fun getByPseudo(pseudo: String) = repository.getByPseudo(pseudo)
        ?: throw UserPseudoNotFoundException(pseudo).apply { Logger.info(message) }

    override fun getAll(page: Int, size: Int) = repository.getAll(page, size)

    override fun searchByPseudo(q: String, maxSize: Int, excluding: Set<UUID>) =
        repository.searchByPseudo(q, maxSize, excluding)

    override fun update(id: UUID, email: String, bcryptPassword: String, role: User.Role): User {
        if (repository.existsByEmail(email, setOf(id))) {
            throw UserEmailAlreadyUsedException(email).apply { Logger.info(message) }
        }
        val user = getById(id)
        return repository.save(
            user = user.copy(
                email = email,
                bcryptPassword = bcryptPassword,
                role = role
            )
        ).apply { Logger.info("User '$pseudo' ($id) updated") }
    }
}
