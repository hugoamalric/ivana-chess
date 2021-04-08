package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Abstract implementation of entity service.
 *
 * @param E Type of entity.
 */
abstract class AbstractEntityService<E : Entity> : EntityService<E> {
    /**
     * Entity repository.
     */
    protected abstract val repository: EntityRepository<E>

    /**
     * Logger.
     */
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun existsWithId(id: UUID) = repository.existsWithId(id)

    override fun getById(id: UUID) = repository.fetchById(id) ?: throw EntityNotFoundException(
        message = "Entity $id does not exist"
    ).apply { logger.debug(message) }

    override fun getPage(pageOpts: PageOptions<E>) = repository.fetchPage(pageOpts)
}
