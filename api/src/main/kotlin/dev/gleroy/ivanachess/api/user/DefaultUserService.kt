package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.PageOptions
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
        if (repository.existsWithPseudo(pseudo)) {
            throw UserPseudoAlreadyUsedException(pseudo).apply { Logger.info(message) }
        }
        if (repository.existsWithEmail(email)) {
            throw UserEmailAlreadyUsedException(email).apply { Logger.info(message) }
        }
        val user = repository.save(
            entity = User(
                pseudo = pseudo,
                email = email,
                bcryptPassword = bcryptPassword
            )
        )
        Logger.info("New user '$pseudo' (${user.id}) created")
        return user
    }

    override fun existsByEmail(email: String) = repository.existsWithEmail(email)

    override fun existsByPseudo(pseudo: String) = repository.existsWithPseudo(pseudo)

    override fun getByEmail(email: String) = repository.fetchByEmail(email)
        ?: throw UserEmailNotFoundException(email).apply { Logger.info(message) }

    override fun getById(id: UUID) = repository.fetchById(id) ?: throw UserIdNotFoundException(id).apply {
        Logger.info(message)
    }

    override fun getByPseudo(pseudo: String) = repository.fetchByPseudo(pseudo)
        ?: throw UserPseudoNotFoundException(pseudo).apply { Logger.info(message) }

    override fun getAll(page: Int, size: Int) = repository.fetchPage(PageOptions(page, size))

    override fun searchByPseudo(q: String, maxSize: Int, excluding: Set<UUID>) =
        repository.searchByPseudo(q, maxSize, excluding)

    override fun update(id: UUID, email: String, bcryptPassword: String, role: User.Role): User {
        if (repository.existsWithEmail(email, setOf(id))) {
            throw UserEmailAlreadyUsedException(email).apply { Logger.info(message) }
        }
        val user = getById(id)
        return repository.save(
            entity = user.copy(
                email = email,
                bcryptPassword = bcryptPassword,
                role = role
            )
        ).apply { Logger.info("User '$pseudo' ($id) updated") }
    }
}
